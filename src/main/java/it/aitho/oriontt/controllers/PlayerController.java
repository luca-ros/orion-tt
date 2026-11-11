package it.aitho.oriontt.controllers;

import it.aitho.oriontt.dto.EmailUpdateDTO;
import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// List: viene da Java (java.util), non da Spring

@RestController
// annotation: questa class riceve richieste HTTP e restituisce dati
// Spring trasforma da solo il valore di ritorno in JSON

@RequestMapping("/api/players")
// percorso base di tutta la class: ogni method parte da /api/players
// URL al plurale = la risorsa "players" (convenzione REST)

public class PlayerController {

    //  CAMPO: vive nella classe, resta finché esiste il Controller
    // private = visibile solo dentro questa class
    // final = una volta assegnato non cambia più
    // il Controller non parla col database: chiede al Service
    private final PlayerService playerService;


    public PlayerController(PlayerService playerService) {// ← PARAMETRO: arriva da Spring, esiste solo dentro il costruttore
        // COSTRUTTORE: stesso nome della class, nessun tipo di ritorno
        // PARAMETRO: il PlayerService arriva da Spring (Dependency Injection)
        // esiste solo dentro il costruttore
        this.playerService = playerService;
        // campo                parametro
        // campo e parametro dello stesso tipo, e il parametro viene salvato nel campo.
        //prendi il PlayerService che mi ha dato Spring (parametro) e salvalo nel mio campo, così lo posso usare dopo
        // this = "di questa class": distingue il campo dal parametro
        // campo ← parametro: salvo quello che mi dà Spring per usarlo nei method
        // senza questa riga il campo resterebbe null → NullPointerException
    }

    // GET /api/players
    @GetMapping
    // vuota: non aggiunge niente al percorso → l'URL resta /api/players
    // (con "/players" diventerebbe /api/players/players)
    public List<PlayerDTO> getAllPlayers() {
        // List<Player> = tipo di ritorno: una lista di oggetti Player (tutti i giocatori)
        // parentesi vuote: l'URL non porta nessun valore da leggere (niente id)
        return playerService.getAllPlayers();
        // chiamo il method del Service: Controller → Service → Repository
        // () obbligatorie: senza parentesi Java cerca un campo, non un method
    }

    // POST new Player  /api/players
    @PostMapping
    public Player createPlayer(@RequestBody Player player){
        return playerService.createPlayer(player);
    }

    // PATCH update email
    @PatchMapping("/{id}")
    public PlayerDTO updateEmailPlayer(@PathVariable Long id, @Valid @RequestBody EmailUpdateDTO request) {
        //restituisci quello che ti dà il Service
        return playerService.updateEmailPlayer(id, request.getEmail());
    }
}
