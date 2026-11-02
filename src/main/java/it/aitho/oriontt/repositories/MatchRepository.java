package it.aitho.oriontt.repositories;

import it.aitho.oriontt.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// gestisce la tabella matches: findById, findAll, save... sono già inclusi
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
}