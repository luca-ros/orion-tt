package it.aitho.oriontt.repositories;

import it.aitho.oriontt.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    // interface: descrive cosa si può fare, il codice lo genera Spring
    // extends: eredita tutti i method di JpaRepository (findAll, findById, save, deleteById...)
    //          senza scriverli → per questo la interface è vuota
    // <Player, Long>:
    //   Player = la Entity gestita (tabella players)
    //   Long   = il TIPO del campo @Id di Player
    // il Repository è l'unico che parla con il database
}
