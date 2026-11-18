package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.UpdateSetRequestDTO;
import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.entities.MatchSet;
import it.aitho.oriontt.entities.MatchStatus;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.MatchRepository;
import it.aitho.oriontt.repositories.MatchSetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    // Repository finto: non tocca il database, decidiamo noi cosa restituisce
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchSetRepository matchSetRepository;

    // Il Service vero, a cui Mockito passa il repository finto
    @InjectMocks
    private MatchService matchService;

    // ===== getMatchById =====
    // Caso: id 0 → il Service deve lanciare un 400 Bad Request
    @Test
    void getMatchById_idZero_throwsBadRequest() {
        // assertThrows: verifica che il method lanci proprio questa exception
        ResponseStatusException serviceResponse = assertThrows(ResponseStatusException.class,
                () -> matchService.getMatchById(0L));

        // Controlla che lo status dentro l'exception sia 400
        assertEquals(HttpStatus.BAD_REQUEST, serviceResponse.getStatusCode());
    }

    // Caso: partita inesistente → il Service deve lanciare un 404 Not Found
    @Test
    void getMatchById_notFound_throwsNotFound() {
        // Diciamo al repository finto: se cercano l'id 90, restituisci un Optional vuoto
        when(matchRepository.findById(90L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.getMatchById(90L));

        // Controlla che lo status sia 404
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // Caso: partita esistente → il Service deve restituirla
    @Test
    void getMatchById_found_returnsMatch() {
        // Creiamo una partita finta con id 1
        Match match = new Match();
        match.setId(1L);

        // Il repository finto la restituisce quando cercano l'id 1
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Chiamiamo il Service vero
        Match result = matchService.getMatchById(1L);

        // Controlla che sia proprio la partita con id 1
        assertEquals(1L, result.getId());
    }

    // Helper: partita finta con id 1 in stato SCHEDULED
    private Match getMatchMock() {
        // crea un nuovo oggetto Match, ancora vuoto
        Match match = new Match();
        // setter: imposta l'id della partita a 1
        match.setId(1L);
        // setter: la partita parte SCHEDULED, cioè non ancora iniziata
        match.setStatus(MatchStatus.SCHEDULED);
        // restituisce la partita pronta a chi chiama getMatchMock()
        return match;
    }

    // ===== startMatch =====
    // Test del metodo che avvia una partita (POST /api/matches/{id}/start)

    //  1. match inesistente → eccezione 404 NOT_FOUND
    @Test
    void startMatch_whenMatchNotFound_throwsException() {
        // Arrange: il Repository finto non trova nessuna partita con id 1
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());
        // Act + Assert: startMatch deve LANCIARE l'exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.startMatch(1L));
        // Assert: lo status dentro l'eccezione deve essere 404 NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        //           ↑ atteso               ↑ attuale (letto dall'eccezione)
    }

    //  2. match non SCHEDULED (IN_PROGRESS o FINISHED) → eccezione 409 Conflict
    @Test
    void startMatch_whenMatchNotScheduled_throwsConflict() {
        // Arrange: il Repository finto trova la partita con id 1 status: IN_PROGRESS
        Match match = getMatchMock();
        match.setStatus(MatchStatus.IN_PROGRESS);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        // Act + Assert: startMatch deve LANCIARE l'exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.startMatch(1L));
        // Assert: lo status dentro l'eccezione deve essere 409 CONFLICT
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        //           ↑ atteso               ↑ attuale (letto dall'eccezione)
    }

    //  3. match SCHEDULED → status diventa IN_PROGRESS e viene creato il set 1 (0-0)
    @Test
    void startMatch_whenMatchScheduled_setsInProgressAndCreatesFirstSet() {
        // Arrange: il Repository finto trova la partita con id 1 in stato SCHEDULED
        Match match = getMatchMock();

        // 1. il Service cerca la partita → la trova
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        // 2. il Service salva la partita → restituisce la stessa partita
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // ACT
        // Chiamiamo il Service vero
        Match result = matchService.startMatch(1L);

        // Assert: mi aspetto IN_PROGRESS, controllo lo status reale del risultato
        assertEquals(MatchStatus.IN_PROGRESS, result.getStatus());
        // Controllo che il Repository dei set abbia ricevuto un save(...)
        verify(matchSetRepository).save(any(MatchSet.class));
    }

    // ===== updateSet =====
    // Test del metodo che salva il punteggio di un set finito (PATCH /api/matches/{id}/sets/{setNumber})

    // --- casi di errore ---

    //  1. match inesistente → 404 NOT_FOUND (arriva da getMatchById)
    @Test
    void updateSet_whenMatchNotFound_throwsNotFound() {
        // Arrange: il Repository finto non trova nessuna partita con id 1
        UpdateSetRequestDTO request = new UpdateSetRequestDTO(11, 7);
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());
        // Act + Assert: updateSet deve LANCIARE l'exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.updateSet(1L, 1, request));
        // Assert: lo status dentro l'eccezione deve essere 404 NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        //           ↑ atteso               ↑ attuale (letto dall'eccezione)
    }

    //  2. match non IN_PROGRESS (SCHEDULED o FINISHED) → 409 CONFLICT
    @Test
    void updateSet_whenMatchNotInProgress_throwsConflict() {
        // Arrange: il Repository finto trova la partita con id 1 status: FINISHED
        UpdateSetRequestDTO request = new UpdateSetRequestDTO(11, 7);
        Match match = getMatchMock();
        match.setStatus(MatchStatus.FINISHED);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        // Act + Assert: updateSet deve LANCIARE l'exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.updateSet(1L, 1, request));
        // Assert: lo status dentro l'eccezione deve essere 409 CONFLICT
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        //           ↑ atteso               ↑ attuale (letto dall'eccezione)
    }

    //  3. punti mancanti o negativi (es. null, -1) → 400 BAD_REQUEST
    @Test
    void updateSet_whenPointsMissingOrNegative_throwsBadRequest() {
        // Arrange: il Repository finto trova la partita con status: IN_PROGRESS
        UpdateSetRequestDTO request = new UpdateSetRequestDTO(-1, 11);
        Match match = getMatchMock();
        match.setStatus(MatchStatus.IN_PROGRESS);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        // Act: updateSet deve LANCIARE l'exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.updateSet(1L, 1, request));
        // Assert: lo status dentro l'eccezione deve essere 400 BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        //           ↑ atteso               ↑ attuale (letto dall'eccezione)
    }

    //  4. punteggio che viola la regola (es. 5-3, 11-10, 13-10) → 400 BAD_REQUEST
    @Test
    void updateSet_whenScoreBreaksRules_throwsBadRequest() {
        // Arrange: il Repository finto trova la partita con status: IN_PROGRESS
        // 12-9: sopra l'11 la differenza deve essere esattamente 2 → punteggio non valido
        UpdateSetRequestDTO request = new UpdateSetRequestDTO(12, 9);
        Match match = getMatchMock();
        match.setStatus(MatchStatus.IN_PROGRESS);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        // Act: updateSet deve LANCIARE l'exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.updateSet(1L, 1, request));
        // Assert: lo status dentro l'eccezione deve essere 400 BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        //           ↑ atteso               ↑ attuale (letto dall'eccezione)
    }

    //  5. nessun set aperto trovato → 404 NOT_FOUND
    @Test
    void updateSet_whenOpenSetNotFound_throwsNotFound() {
        // Arrange: partita IN_PROGRESS trovata, punteggio valido
        UpdateSetRequestDTO request = new UpdateSetRequestDTO(11, 7);
        Match match = getMatchMock();
        match.setStatus(MatchStatus.IN_PROGRESS);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // il Repository dei set non trova nessun set aperto per la partita 1 → scatola vuota
        when(matchSetRepository.findTopByMatchIdOrderBySetNumberDesc(1L))
                .thenReturn(Optional.empty());

        // Act: updateSet deve LANCIARE l'exception (si ferma al blocco 4)
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.updateSet(1L, 1, request));

        // Assert: lo status dentro l'eccezione deve essere 404 NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    //  6. setNumber dell'URL diverso dal set aperto → 409 CONFLICT
    @Test
    void updateSet_whenSetNumberNotCurrent_throwsConflict() {
        // Arrange: partita IN_PROGRESS trovata, punteggio valido
        UpdateSetRequestDTO request = new UpdateSetRequestDTO(11, 7);
        Match match = getMatchMock();
        match.setStatus(MatchStatus.IN_PROGRESS);
        MatchSet openSet = new MatchSet();
        // set aperto = 2, ma nell'URL chiedo il set 1 → non è il set corrente
        openSet.setSetNumber(2);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        //  il Repository trova un set aperto per la partita 1 → piena
        when(matchSetRepository.findTopByMatchIdOrderBySetNumberDesc(1L))
                .thenReturn(Optional.of(openSet));

        // Act: updateSet deve LANCIARE l'exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.updateSet(1L, 1, request));

        // Assert: lo status dentro l'eccezione deve essere 409
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    // --- casi felici ---

    //  7. set vinto, partita non decisa → crea il set successivo
    @Test
    void updateSet_whenSetWonAndMatchNotOver_createsNextSet() {
        // Arrange
        // Body: il set 1 finisce 11-7 → lo vince il player 1
        UpdateSetRequestDTO request = new UpdateSetRequestDTO(11, 7);

        // partita in corso
        Match match = getMatchMock();
        match.setStatus(MatchStatus.IN_PROGRESS);

        // set aperto: è il set 1, ancora 0-0
        MatchSet openSet = new MatchSet();
        openSet.setSetNumber(1);
        openSet.setPlayer1Points(0);
        openSet.setPlayer2Points(0);

        // lo stesso oggetto openSet va anche nella lista della partita,
        // così il for del blocco 6 lo conta dopo che il Service gli ha scritto 11-7
        match.getMatchSets().add(openSet);

        // 1. il Service cerca la partita → la trova
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        // 2. il Service cerca il set aperto → trova openSet
        when(matchSetRepository.findTopByMatchIdOrderBySetNumberDesc(1L))
                .thenReturn(Optional.of(openSet));
        // 3. il Service salva la partita → restituisce la stessa partita (altrimenti null)
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act: chiamata normale, nessuna eccezione attesa
        Match result = matchService.updateSet(1L, 1, request);

        // Assert
        // 1. il set 1 ha ricevuto il punteggio dal Body
        assertEquals(11, openSet.getPlayer1Points());
        assertEquals(7, openSet.getPlayer2Points());

        // 2. la partita NON è finita: 1 set vinto, nessuno a 3
        assertEquals(MatchStatus.IN_PROGRESS, result.getStatus());

        // 3. nella lista ora ci sono 2 set: il set 1 chiuso + il nuovo set 2
        assertEquals(2, result.getMatchSets().size());

        // 4. il nuovo set è il numero 2, vuoto (0-0)
        MatchSet nextSet = result.getMatchSets().get(1);   // get(1) = secondo elemento (si parte da 0)
        assertEquals(2, nextSet.getSetNumber());
        assertEquals(0, nextSet.getPlayer1Points());
        assertEquals(0, nextSet.getPlayer2Points());

        // 5. save sul Repository dei set chiamato 2 volte: UPDATE del set 1 + INSERT del set 2
        verify(matchSetRepository, times(2)).save(any(MatchSet.class));
    }

    //  8. 3° set vinto → FINISHED + winner impostato
    @Test
    void updateSet_whenSetWinsMatch_finishesMatchWithWinner() {
        // Arrange
        // Body: il set 3 finisce 11-7 → lo vince il player 1 (che arriva a 3 set)
        UpdateSetRequestDTO request = new UpdateSetRequestDTO(11, 7);

        // partita in corso
        Match match = getMatchMock();
        match.setStatus(MatchStatus.IN_PROGRESS);

        // giocatori: servono perché il Service fa setWinner(match.getPlayer1())
        Player player1 = new Player();
        player1.setId(1L);
        Player player2 = new Player();
        player2.setId(2L);
        match.setPlayer1(player1);
        match.setPlayer2(player2);

        // set 1 e 2 già chiusi, entrambi vinti dal player 1
        MatchSet set1 = new MatchSet();
        set1.setSetNumber(1);
        set1.setPlayer1Points(11);
        set1.setPlayer2Points(5);

        MatchSet set2 = new MatchSet();
        set2.setSetNumber(2);
        set2.setPlayer1Points(11);
        set2.setPlayer2Points(8);

        // set aperto: è il set 3, ancora 0-0
        MatchSet openSet = new MatchSet();
        openSet.setSetNumber(3);
        openSet.setPlayer1Points(0);
        openSet.setPlayer2Points(0);

        // tutti e 3 i set nella lista della partita (il for del blocco 6 li conta tutti)
        match.getMatchSets().add(set1);
        match.getMatchSets().add(set2);
        match.getMatchSets().add(openSet);

        // 1. il Service cerca la partita → la trova
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        // 2. il Service cerca il set aperto → trova il set 3
        when(matchSetRepository.findTopByMatchIdOrderBySetNumberDesc(1L))
                .thenReturn(Optional.of(openSet));
        // 3. il Service salva la partita → restituisce la stessa partita
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act: chiamata normale, nessuna eccezione attesa (URL: set 3)
        Match result = matchService.updateSet(1L, 3, request);

        // Assert
        // 1. partita chiusa
        assertEquals(MatchStatus.FINISHED, result.getStatus());

        // 2. il vincitore è il player 1
        assertEquals(player1, result.getWinner());

        // 3.  NESSUN set nuovo creato: la lista resta di 3 set
        assertEquals(3, result.getMatchSets().size());

        // 4.  save sui set chiamato 1 sola volta: solo l'UPDATE del set 3, niente INSERT
        verify(matchSetRepository, times(1)).save(any(MatchSet.class));
    }

}