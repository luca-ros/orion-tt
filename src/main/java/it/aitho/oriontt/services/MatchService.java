package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.CreateMatchRequestDTO;
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
}