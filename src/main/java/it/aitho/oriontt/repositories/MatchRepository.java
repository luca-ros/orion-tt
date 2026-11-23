package it.aitho.oriontt.repositories;

import it.aitho.oriontt.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// gestisce la tabella matches: findById, findAll, save... sono già inclusi
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    // cerca le partite dove il giocatore è player1 OPPURE player2
    List<Match> findByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id);
}