package it.aitho.oriontt.controllers;

import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.repositories.MatchRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")   // tutti gli indirizzi di questa classe iniziano così
public class MatchController {

    // il Repository che legge la tabella matches
    private final MatchRepository matchRepository;

    // costruttore: Spring passa qui il MatchRepository da solo (Dependency Injection)
    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    // GET /api/matches/1 → {id} è la parte variabile dell'indirizzo
    @GetMapping("/{id}")
    public Match getMatch(@PathVariable Long id) {   // legge "1" dall'URL e lo converte in Long
        // VERSIONE PROVVISORIA, senza logica:
        // findById cerca la partita; se non c'è restituisce null
        return matchRepository.findById(id).orElse(null);
    }
}