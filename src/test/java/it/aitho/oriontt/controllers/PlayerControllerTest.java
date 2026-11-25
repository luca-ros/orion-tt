package it.aitho.oriontt.controllers;

import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.dto.PlayerProfileDTO;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.services.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Avvia solo la parte web di Spring, solo per PlayerController (niente database)
@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    // MockMvc: simula le richieste HTTP, come Postman ma dentro il test
    @Autowired
    private MockMvc mockMvc;

    // Service finto: Spring lo mette nel Controller al posto di quello vero
    @MockitoBean
    private PlayerService playerService;

    @Test
    void getAllPlayers() throws Exception {

        // 1. il Service finto restituisce una lista con un DTO (id, nome, cognome, email)
        when(playerService.getAllPlayers()).thenReturn(List.of(new PlayerDTO(1L, "Long", "Ma", "aitho@test1.it")));

        // 2. simulo la GET, come Send in Postman
        mockMvc.perform(get("/api/players")).andExpect(status().isOk())   // codice 200
                .andExpect(jsonPath("$[0].id").value(1))                  // ← id (ora presente nel JSON)
                .andExpect(jsonPath("$[0].firstName").value("Long"))      // nome del primo giocatore
                .andExpect(jsonPath("$[0].lastName").value("Ma"))         // cognome
                .andExpect(jsonPath("$[0].email").value("aitho@test1.it")); // email
    }

    @Test
    void getAllPlayersEmptyList() throws Exception {
        when(playerService.getAllPlayers()).thenReturn(List.of());
        mockMvc.perform(get("/api/players")).andExpect(status().isOk())   // 200
                .andExpect(jsonPath("$").isEmpty());                      // $ = tutto il JSON: deve essere []
    }


    // Test del Controller: POST /api/players
    // Controller VERO + Service FINTO (@MockitoBean)
    // verifica che il Controller legga il JSON dal body e restituisca il giocatore salvato
    @Test
    void createPlayer() throws Exception {

        // giocatore come lo restituisce il Service finto: già salvato, con id
        Player savedPlayer = new Player();
        savedPlayer.setId(8L);
        savedPlayer.getAccount().setFirstName("Chissu");
        savedPlayer.getAccount().setLastName("Essere");
        savedPlayer.getAccount().setEmail("aitho@test1.it");
        when(playerService.createPlayer(any(Player.class))).thenReturn(savedPlayer);

        mockMvc.perform(post("/api/players").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\": \"Chissu\", \"lastName\": \"Essere\", \"email\": \"aitho@test1.it\"}"))
                .andExpect(status().isOk())                          // oggi il POST risponde 200 (non ancora 201)
                .andExpect(jsonPath("$.id").value(8))                // id del giocatore salvato
                .andExpect(jsonPath("$.firstName").value("Chissu"))  // nome
                .andExpect(jsonPath("$.lastName").value("Essere"))   // cognome
                .andExpect(jsonPath("$.email").value("aitho@test1.it")); // email
    }

    // #2
    // Test PATCH email valida:
    // mando un'email corretta al giocatore 6 → mi aspetto 200 OK
    // e che nella risposta (PlayerDTO) torni la stessa email.
    // Il Service è finto: il when gli dice cosa restituire.
    @Test
    void updateEmail() throws Exception {
        when(playerService.updateEmailPlayer(6L, "aitho@test1.it")).thenReturn(getPlayerDTOMock());

        // #3
        mockMvc.perform(patch("/api/players/6").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"aitho@test1.it\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("aitho@test1.it"));
    }


    // Test PATCH email non valida:
    // mando "ciao" come email → mi aspetto 400 Bad Request.
    // Niente when: @Valid blocca la richiesta prima del Service,
    // che quindi non viene mai chiamato.
    @Test
    void updateNotValid() throws Exception {

        mockMvc.perform(patch("/api/players/6").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"ciao\"}"))
                .andExpect(status().isBadRequest());
    }

    // GET /api/players/37/profile → 200 + JSON con i dati del profilo
    @Test
    void getPlayerProfile_whenPlayerExists_returns200() throws Exception {
        // Arrange: quando il Controller chiama il Service finto con id 37,
        // il Service restituisce il DTO finto creato dall'helper
        when(playerService.getPlayerProfile(37L)).thenReturn(getPlayerProfileDTOMock());

        // Act + Assert: chiamo l'endpoint e controllo status e campi del JSON
        mockMvc.perform(get("/api/players/37/profile"))
                .andExpect(status().isOk())                                    // 200
                .andExpect(jsonPath("$.id").value(37))                        // $.id = campo id del JSON
                .andExpect(jsonPath("$.firstName").value("Octopus"))
                .andExpect(jsonPath("$.lastName").value("Tentacul"))
                .andExpect(jsonPath("$.fitetCardNumber").value("Ot369Gnazzus"));
    }

    // GET /api/players/-2/profile → 400
    @Test
    void getPlayerProfile_whenIdInvalid_returns400() throws Exception {
        when(playerService.getPlayerProfile(-2L))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid player id"));

        mockMvc.perform(get("/api/players/-2/profile"))
                .andExpect(status().isBadRequest());   // status 400
    }

    // GET /api/players/98/profile → 404
    @Test
    void getPlayerProfile_notFound_returns404() throws Exception {
        when(playerService.getPlayerProfile(98L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        mockMvc.perform(get("/api/players/98/profile"))
                .andExpect(status().isNotFound());   // status 404
    }

    // #2 helper PlayerProfileDTO
    // helper method: crea un PlayerProfileDTO finto con dati pronti,
    // riusato nei test del Controller al posto di ricrearlo ogni volta.
    private PlayerProfileDTO getPlayerProfileDTOMock() {
        // il PlayerProfileDTO non ha setter: i dati passano dal constructor
        // ordine: id, firstName, lastName, fitetCardNumber, matches
        return new PlayerProfileDTO(
                37L,               // id
                "Octopus",         // firstName
                "Tentacul",        // lastName
                "Ot369Gnazzus",    // fitetCardNumber
                List.of()          // matches: lista vuota (per ora il test non controlla le partite)
        );
    }

    // #1 PlayerDTO
    // helper method: crea un PlayerDTO finto con dati pronti,
    // riusato nei test del Controller al posto di ricrearlo ogni volta.
    private PlayerDTO getPlayerDTOMock() {
        // il PlayerDTO non ha setter: i dati passano dal costruttore
        // (ordine: id, firstName, lastName, email)          ← aggiornato
        return new PlayerDTO(6L, "Chissu", "Essere", "aitho@test1.it");   // ← id 6 = lo stesso della PATCH
    }

}