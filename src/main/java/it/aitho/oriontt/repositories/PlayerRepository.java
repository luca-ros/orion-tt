package it.aitho.oriontt.repositories;

import it.aitho.oriontt.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByAccountId(Long accountId);

}
