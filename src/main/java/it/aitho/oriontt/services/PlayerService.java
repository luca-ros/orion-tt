package it.aitho.oriontt.services;

import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    // Repository che legge la tabella player
    // campo: chi usa il Service per parlare col database?
    private final PlayerRepository playerRepository;

    // Costruttore: Spring passa qui il PlayerRepository che ha gia creato (constructor injection)
    // stesso schema del PlayerController
    public PlayerService(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
    }

    // Metodo
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
        // il Service può chiamare solo chi sta "dopo" nella catena: il Repository
        // Controller → Service → Repository → DB
        // findAll() = method ereditato da JpaRepository: SELECT * FROM players
        // getAllPlayers() invece esiste solo in Service e Controller (nome scelto da noi)
    }

}
