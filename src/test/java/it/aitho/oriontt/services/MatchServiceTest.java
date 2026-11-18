package it.aitho.oriontt.services;

import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.entities.MatchSet;
import it.aitho.oriontt.entities.MatchStatus;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}