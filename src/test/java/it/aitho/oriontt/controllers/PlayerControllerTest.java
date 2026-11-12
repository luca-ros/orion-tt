package it.aitho.oriontt.controllers;


import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.services.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

        // 1. il Service finto restituisce una lista con un DTO
        when(playerService.getAllPlayers()).thenReturn(List.of(new PlayerDTO("Long", "Ma", "aitho@test1.it")));

        // 2. simulo la GET, come Send in Postman
        mockMvc.perform(get("/api/players")).andExpect(status().isOk())                          // codice 200
                .andExpect(jsonPath("$[0].firstName").value("Long")) // nome del primo giocatore
                .andExpect(jsonPath("$[0].lastName").value("Ma"))   // cognome
                .andExpect(jsonPath("$[0].email").value("aitho@test1.it"));
    }

    @Test
    void getAllPlayersEmptyList() throws Exception {
        when(playerService.getAllPlayers()).thenReturn(List.of());
        mockMvc.perform(get("/api/players")).andExpect(status().isOk())          // 200
                .andExpect(jsonPath("$").isEmpty()); // $ = tutto il JSON: deve essere []
    }


    // Test del Controller: POST /api/players
    // Controller VERO + Service FINTO (@MockitoBean)
    // verifica che il Controller legga il JSON dal body e restituisca il giocatore salvato
    @Test
    void createPlayer() throws Exception {

        // giocatore come lo restituisce il Service finto: già salvato, con id
        Player savedPlayer = new Player();
        savedPlayer.setId(8L);
        savedPlayer.setFirstName("Chissu");
        savedPlayer.setLastName("Essere");
        savedPlayer.setEmail("aitho@test1.it");
        when(playerService.createPlayer(any(Player.class))).thenReturn(savedPlayer);

        mockMvc.perform(post("/api/players").contentType(MediaType.APPLICATION_JSON).content("{\"firstName\": \"Chissu\", \"lastName\": \"Essere\", \"email\": \"aitho@test1.it\"}")).andExpect(status().isOk())                          // oggi il POST risponde 200 (non ancora 201)
                .andExpect(jsonPath("$.id").value(8))                // id del giocatore salvato
                .andExpect(jsonPath("$.firstName").value("Chissu"))  // nome
                .andExpect(jsonPath("$.lastName").value("Essere"))  // cognome  // cognome
                .andExpect(jsonPath("$.email").value("aitho@test1.it"));  // email

    }

    // #2
     @Test
     void updateEmail() throws Exception {
         when(playerService.updateEmailPlayer(6L, "aitho@test1.it")).thenReturn(getPlayerDTOMock());

         // #3
         mockMvc.perform(patch("/api/players/6").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"aitho@test1.it\"}")).andExpect(status().isOk())
                 .andExpect(jsonPath("$.email").value("aitho@test1.it"));
     }
    // #1
    // helper method: crea un PlayerDTO finto con dati pronti,
    // riusato nei test del Controller al posto di ricrearlo ogni volta.
    private PlayerDTO getPlayerDTOMock() {
        // il PlayerDTO non ha setter: i dati passano dal costruttore
        // (ordine: firstName, lastName, email)
        return new PlayerDTO("Chissu", "Essere", "aitho@test1.it");
    }

    @Test
    void updateNotValid() throws Exception {

        mockMvc.perform(patch("/api/players/6").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"ciao\"}"))
                .andExpect(status().isBadRequest());
    }

}
