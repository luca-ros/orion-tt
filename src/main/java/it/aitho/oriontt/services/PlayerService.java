package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
// annotation: Spring crea questa class e la può passare al Controller (Dependency Injection)
public class PlayerService {

    // il Repository che legge la tabella players
    private final PlayerRepository playerRepository;

    // costruttore: Spring passa qui il PlayerRepository (constructor injection)
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;   // campo ← parametro
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
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        player.setEmail(email.toLowerCase());   // sanificazione: tutto minuscolo
        playerRepository.save(player);          // ha già un id → UPDATE
        return toDTO(player);
    }

    // Converte Entity → DTO in UN solo punto:
    // se domani aggiungi un campo al DTO, lo cambi solo qui
    private PlayerDTO toDTO(Player player) {
        return new PlayerDTO(player.getId(), player.getFirstName(), player.getLastName(), player.getEmail());
    }
}