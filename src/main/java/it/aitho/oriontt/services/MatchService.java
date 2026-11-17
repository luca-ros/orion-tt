package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.CreateMatchRequestDTO;
import it.aitho.oriontt.dto.UpdateSetRequestDTO;
import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.entities.MatchSet;
import it.aitho.oriontt.entities.MatchStatus;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.MatchRepository;
import it.aitho.oriontt.repositories.MatchSetRepository;
import it.aitho.oriontt.repositories.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MatchService {

    // Repository che legge la tabella matches
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final MatchSetRepository matchSetRepository;

    // Costruttore: Spring passa qui il MatchRepository che ha già creato (constructor injection)
    public MatchService(MatchRepository matchRepository, PlayerRepository playerRepository, MatchSetRepository matchSetRepository) {
        // Salva il repository nel campo final, così i method del Service possono usarlo
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.matchSetRepository = matchSetRepository;
    }

    // Cerca una partita per id.
    // Chi decide l'errore?
    // - /api/matches/cocco (o 1.5) → Spring non riesce a convertire in Long:
    //   risponde subito 400 e questo method NON viene chiamato
    // - /api/matches/0 o /api/matches/-1 → la conversione riesce, arriva qui:
    //   l'if sotto risponde 400 Bad Request
    // - /api/matches/90 → la conversione riesce, arriva qui, ma la partita non esiste:
    //   orElseThrow risponde 404 Not Found
    public Match getMatchById(Long id) {

        // id 0 o negativo: input sbagliato a priori → 400 Bad Request
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid match id");
        }

        // orElseThrow: se l'Optional è vuoto, lancia il 404
        Match match = matchRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
        return match;
    }

    public Long createMatch(CreateMatchRequestDTO request) {
        if (request.getPlayer1Id().equals(request.getPlayer2Id())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Players must be different");

        }
        Player player1 = playerRepository.findById(request.getPlayer1Id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        Player player2 = playerRepository.findById(request.getPlayer2Id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        // deve creare un match nel database
        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setPlannedDate(request.getPlannedDate());
        match.setStatus(MatchStatus.SCHEDULED);

        return matchRepository.save(match).getId();
    }


    // AVVIO PARTITA: crea il primo set vuoto e mette la partita IN_PROGRESS
    // @Transactional: INSERT del set + UPDATE del match valgono come un'unica operazione
    // (se una fallisce, il database torna com'era)
    @Transactional
    public Match startMatch(Long id) {

        // blocco 1: 400 se id <= 0, 404 se non esiste → lo fa già getMatchById
        Match match = getMatchById(id);

        // blocco 2: 409 se la partita è IN_PROGRESS o FINISHED
        if (match.getStatus() != MatchStatus.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Match already started or finished");
        }

        // blocco 3: INSERT del primo set vuoto (0-0) collegato a questa partita
        MatchSet firstSet = new MatchSet();
        firstSet.setMatch(match);
        firstSet.setSetNumber(1);
        firstSet.setPlayer1Points(0);
        firstSet.setPlayer2Points(0);
        matchSetRepository.save(firstSet);

        // blocco 4: UPDATE dello status → IN_PROGRESS
        match.setStatus(MatchStatus.IN_PROGRESS);
        // save con l'id già presente = UPDATE (non crea una nuova riga)
        return matchRepository.save(match);
    }

    // Update set score while status: IN_PROGRESS
    @Transactional
    public Match updateSet(Long id, Integer setNumber, UpdateSetRequestDTO request) {

        // blocco 1: 400 se id <= 0, 404 se non esiste → lo fa già getMatchById
        Match match = getMatchById(id);

        // blocco 2: 409 se la partita è SCHEDULED o FINISHED
        if (match.getStatus() != MatchStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Match not started or finished");
        }

        // Leggo i punti dal Body: request è il DTO, i getter leggono i suoi campi
        Integer p1 = request.getPlayer1Points();
        Integer p2 = request.getPlayer2Points();

        // blocco 3: 400 se il punteggio non è valido
        // campi mancanti nel JSON o punti negativi
        if (p1 == null || p2 == null || p1 < 0 || p2 < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid score");
        }

        int max = Math.max(p1, p2);        // punti di chi ha vinto il set
        int diff = Math.abs(p1 - p2);      // differenza, sempre positiva

        // regola del set:
        // max = 11 → differenza ≥ 2   (es. 11-9 ok, 11-10 no)
        // max > 11 → differenza = 2   (es. 12-10 ok, 13-10 no)
        boolean valid = (max == 11 && diff >= 2) || (max > 11 && diff == 2);

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid score");
        }

        // blocco 4: recupero il set aperto (set_number più alto della partita)
        MatchSet openSet = matchSetRepository.findTopByMatchIdOrderBySetNumberDesc(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Open set not found"));

        // 409 se il setNumber dell'URL NON è uguale a quello del set aperto
        if (!setNumber.equals(openSet.getSetNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Set already saved or not current");
        }

        // blocco 5: UPDATE del set aperto con i punti arrivati dal Body
        openSet.setPlayer1Points(p1);
        openSet.setPlayer2Points(p2);
        matchSetRepository.save(openSet);   // ha già un id → UPDATE

        // blocco 6: conto i set vinti da ciascun giocatore
        int setsWonP1 = 0;
        int setsWonP2 = 0;

        // per ogni set della partita (ora sono tutti chiusi, compreso quello appena salvato)
        for (MatchSet s : match.getMatchSets()) {
            // chi ha più punti nel set ha vinto quel set (es. 11-7 → player 1)
            if (s.getPlayer1Points() > s.getPlayer2Points()) {
                setsWonP1++;   // ++ = aggiungi 1
            } else {
                setsWonP2++;
            }
        }

        // blocco 7: partita finita o set successivo?
        if (setsWonP1 == 3 || setsWonP2 == 3) {
            // qualcuno ha vinto 3 set → UPDATE del match: chiudo e salvo il vincitore
            match.setStatus(MatchStatus.FINISHED);
            match.setWinner(setsWonP1 == 3 ? match.getPlayer1() : match.getPlayer2());
            // ? : = "se vero prendi il primo, altrimenti il secondo"
        } else {
            // nessuno a 3 → INSERT del set successivo vuoto (es. dopo il set 2 crea il set 3, 0-0)
            MatchSet nextSet = new MatchSet();
            nextSet.setMatch(match);
            nextSet.setSetNumber(setNumber + 1);
            nextSet.setPlayer1Points(0);
            nextSet.setPlayer2Points(0);
            matchSetRepository.save(nextSet);   // nessun id → INSERT
        }

        // salvo il match e lo restituisco al Controller
        return matchRepository.save(match);
    }

}