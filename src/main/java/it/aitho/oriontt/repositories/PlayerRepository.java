package it.aitho.oriontt.repositories;

import it.aitho.oriontt.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByAccountId(Long accountId);

}
