package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.CreateMatchRequestDTO;
import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.entities.MatchStatus;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.MatchRepository;
import it.aitho.oriontt.repositories.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static it.aitho.oriontt.entities.MatchStatus.SCHEDULED;

@Service
public class MatchService {

    // Repository che legge la tabella matches
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    // Costruttore: Spring passa qui il MatchRepository che ha già creato (constructor injection)
    public MatchService(MatchRepository matchRepository, PlayerRepository playerRepository) {
        // Salva il repository nel campo final, così i method del Service possono usarlo
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
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
        if (request.getPlayer1Id().equals(request.getPlayer2Id())){
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


}