package it.aitho.oriontt.repositories;

import it.aitho.oriontt.entities.MatchSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchSetRepository extends JpaRepository<MatchSet, Long> {

    // Cerca il set con set_number più alto per quella partita (= set aperto)
    Optional<MatchSet> findTopByMatchIdOrderBySetNumberDesc(Long matchId);

}
