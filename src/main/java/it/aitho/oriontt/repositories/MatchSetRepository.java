package it.aitho.oriontt.repositories;

import it.aitho.oriontt.entities.MatchSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchSetRepository extends JpaRepository<MatchSet, Long> {
}
