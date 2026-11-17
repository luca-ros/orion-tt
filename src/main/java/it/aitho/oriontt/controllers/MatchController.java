package it.aitho.oriontt.controllers;

import it.aitho.oriontt.dto.CreateMatchRequestDTO;
import it.aitho.oriontt.dto.UpdateSetRequestDTO;
import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.services.MatchService;
import org.springframework.web.bind.annotation.*;

// Autorizza le chiamate dal frontend React in sviluppo (Vite gira su localhost:5173)
// Senza questa riga il browser blocca le richieste: porte diverse = "origini" diverse
@CrossOrigin(origins = "http://localhost:5173")
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
    // POST NEW MATCH STATUS: SCHEDULED
    @PostMapping
    public Long createMatch(@RequestBody CreateMatchRequestDTO request) {
        return matchService.createMatch(request);
    }

    // POST NEW MATCH STATUS: IN_PROGRESS
    // AVVIO PARTITA: la partita esiste già, la mette IN_PROGRESS e crea il primo set vuoto
    @PostMapping("/{id}/start")
    public Match startMatch(@PathVariable Long id) {
        return matchService.startMatch(id);
    }

    // PATCH /api/matches/6/sets/1  con Body { "player1Points": 11, "player2Points": 7 }
    @PatchMapping("/{id}/sets/{setNumber}")
    public Match updateSet(
            @PathVariable Long id,
            @PathVariable Integer setNumber,
            @RequestBody UpdateSetRequestDTO request
    ) {
        // Passa tutto al Service
        return matchService.updateSet(id, setNumber, request);
    }
}