package it.aitho.oriontt.controllers;

import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.services.MatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")   // tutti gli indirizzi di questa classe iniziano così
public class MatchController {

    // il Service controlla l'id e recupera la partita
    private final MatchService matchService;

    // costruttore: Spring passa qui il MatchService da solo (Dependency Injection)
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // GET /api/matches/1 → {id} è la parte variabile dell'indirizzo
    @GetMapping("/{id}")
    public Match getMatch(@PathVariable Long id) {   // legge "1" dall'URL e lo converte in Long
       // il Service controlla l’id: 400 se ≤ 0, 404 se la partita non esiste
        return matchService.getMatchById(id);
    }
}