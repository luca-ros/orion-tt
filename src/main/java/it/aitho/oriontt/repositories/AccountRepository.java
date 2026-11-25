package it.aitho.oriontt.repositories;

import it.aitho.oriontt.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
}
