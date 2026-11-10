package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
// annotation: Spring crea questa class e la può passare al Controller (Dependency Injection)
public class PlayerService {

    private final PlayerRepository playerRepository;
    // CAMPO: il Repository che legge la tabella players
    // il Service parla col database solo attraverso il Repository

    public PlayerService(PlayerRepository playerRepository) {
        // COSTRUTTORE: Spring passa qui il PlayerRepository che ha già creato (constructor injection)
        // stesso schema del PlayerController: campo e parametro dello stesso tipo
        this.playerRepository = playerRepository;
        // campo ← parametro
    }

    // Method: restituisce tutti i giocatori SENZA id (solo firstName e lastName)
    // lo chiama il PlayerController (GET /api/players)
    public List<PlayerDTO> getAllPlayers() {
        // List<PlayerDTO> = tipo di ritorno: lista di DTO, non più di Player
        // DOVE GUARDARE: deve coincidere con il tipo di ritorno del Controller

        List<Player> players = playerRepository.findAll();
        // 1. tutti i giocatori dal database (CON l'id)
        // findAll() = method ereditato da JpaRepository: SELECT * FROM players

        List<PlayerDTO> result = new ArrayList<>();
        // 2. lista VUOTA da riempire con i DTO
        // List = interface (non si crea con new) → ArrayList = class concreta

        for (Player player : players) {
            // 3. for-each: "per ogni player nella lista players"
            // (in JS: for (const player of players))

            result.add(new PlayerDTO(player.getFirstName(), player.getLastName(), player.getEmail()));
            // 4. trasformo il Player in PlayerDTO e lo aggiungo a result
            // new PlayerDTO(nome, cognome) → stesso ordine del costruttore di PlayerDTO
            // getFirstName() / getLastName() → i getter di Player
            // in result entrano solo PlayerDTO, non String
        }

        return result;
        // 5. restituisco la lista piena
        // nel return va il NOME della variabile (result), non il tipo (PlayerDTO)
        // "Cannot resolve symbol" = quel nome non esiste in questa class
    }

    // VECCHIA VERSIONE (prima del DTO): restituiva anche l'id
    // public List<Player> getAllPlayers() {
    //     return playerRepository.findAll();
    // }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);

    }
}