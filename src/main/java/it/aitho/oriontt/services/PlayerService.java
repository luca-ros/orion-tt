package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.MatchSummaryDTO;
import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.dto.PlayerProfileDTO;
import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.entities.MatchSet;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.MatchRepository;
import it.aitho.oriontt.repositories.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
// annotation: Spring crea questa class e la può passare al Controller (Dependency Injection)
public class PlayerService {

    // il Repository che legge la tabella players e matches
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    // constructor: Spring passa qui i due Repository (constructor injection)
    public PlayerService(PlayerRepository playerRepository, MatchRepository matchRepository) {
        this.playerRepository = playerRepository;   // campo ← parametro
        this.matchRepository = matchRepository;
    }

    // GET /api/players → tutti i giocatori come DTO
    public List<PlayerDTO> getAllPlayers() {
        List<Player> players = playerRepository.findAll();   // SELECT * FROM players
        List<PlayerDTO> result = new ArrayList<>();          // lista vuota da riempire

        // per ogni Player del DB creo il suo PlayerDTO e lo aggiungo a result
        for (Player player : players) {
            result.add(toDTO(player));
        }
        return result;
    }

    // POST /api/players → salva un nuovo giocatore
    public Player createPlayer(Player player) {
        return playerRepository.save(player);   // nessun id → INSERT
    }

    // PATCH /api/players/{id} → aggiorna l'email
    // 404 se il giocatore non esiste; email salvata in minuscolo
    public PlayerDTO updateEmailPlayer(Long id, String email) {
        Player player = playerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        player.setEmail(email.toLowerCase());   // sanificazione: tutto minuscolo
        playerRepository.save(player);          // ha già un id → UPDATE
        return toDTO(player);
    }

    // Converte Entity → DTO in UN solo punto:
    // se domani aggiungi un campo al DTO, lo cambi solo qui
    private PlayerDTO toDTO(Player player) {
        return new PlayerDTO(player.getId(), player.getFirstName(), player.getLastName(), player.getEmail());
    }

    // GET PlayerProfile api/players/{id}/profile
    public PlayerProfileDTO getPlayerProfile(Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid player id");
        }
        //dichiarazione di variabile player locale con inizializzazione
        Player player = playerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        return new PlayerProfileDTO(player.getId(), player.getFirstName(), player.getLastName(), player.getFitetCardNumber(), getPlayerMatches(id));
    }

    // GET /api/players/{id}/matches
    public List<MatchSummaryDTO> getPlayerMatches(Long id) {

        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid player id");
        }

        // cerco il GIOCATORE (playerRepository)
        // se non esiste → 404
        Player player = playerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        // cerco tutte le partite dove il giocatore (id) è player1 OPPURE player2
        List<Match> matches = matchRepository.findByPlayer1IdOrPlayer2Id(id, id);

        // lista risultato: all'inizio vuota, la riempio nel ciclo

        List<MatchSummaryDTO> result = new ArrayList<>();

        // ciclo ESTERNO: per ogni partita del giocatore
        // per ogni match di tipo Match dentro la lista matches
        for (Match match : matches) {

            // avversario
            Player opponent;
            // id del player1 della partita == id del profilo (dall'URL)? → allora io sono player1
            if (match.getPlayer1().getId().equals(id)) {
                opponent = match.getPlayer2();
            } else {
                opponent = match.getPlayer1();
            }

            // vinta / persa / null
            Boolean won;
            if (match.getWinner() == null) {
                won = null;                                   // partita non ancora finita
            } else if (match.getWinner().getId().equals(id)) {
                won = true;                                   // ho vinto io
            } else {
                won = false;                                   // ha vinto l'avversario
            }

            int setsWon = 0;
            int opponentSetsWon = 0;
            // serve per leggere i MIEI punti nel set: player1Points se true, player2Points se false
            boolean isPlayer1 = match.getPlayer1().getId().equals(id);

            // ciclo INTERNO: i set di questa partita
            for (MatchSet set : match.getMatchSets()) {
                int myPoints = isPlayer1 ? set.getPlayer1Points() : set.getPlayer2Points();
                int opponentPoints = isPlayer1 ? set.getPlayer2Points() : set.getPlayer1Points();

                if (myPoints == opponentPoints) {
                    continue;
                }
                if (myPoints > opponentPoints) {
                    setsWon++;
                } else {
                    opponentSetsWon++;
                }
            }

            // creo il DTO con i dati di questa partita
            MatchSummaryDTO dto = new MatchSummaryDTO(
                    match.getId(),                // matchId
                    opponent.getFirstName(),      // nome avversario
                    opponent.getLastName(),       // cognome avversario
                    match.getStatus(),            // status
                    won,                          // won (calcolato sopra)
                    match.getPlannedDate(),       // data prevista
                    match.getFinishedAt(),         // data di fine (null se non finita)
                    setsWon,
                    opponentSetsWon
            );

            // lo aggiungo alla lista risultato
            result.add(dto);

        }

        // restituisco la lista dei DTO (vuota se il giocatore non ha partite)
        return result;
    }
}