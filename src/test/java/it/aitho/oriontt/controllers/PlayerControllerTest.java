package it.aitho.oriontt.controllers;


import it.aitho.oriontt.dto.PlayerDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
        when(playerService.getAllPlayers())
                .thenReturn(List.of(new PlayerDTO("Long", "Ma")));

        // 2. simulo la GET, come Send in Postman
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())                          // codice 200
                .andExpect(jsonPath("$[0].firstName").value("Long")) // nome del primo giocatore
                .andExpect(jsonPath("$[0].lastName").value("Ma"));   // cognome
    }

    @Test
    void getAllPlayersEmptyList() throws Exception {
        when(playerService.getAllPlayers()).thenReturn(List.of());
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())          // 200
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

        when(playerService.createPlayer(any(Player.class))).thenReturn(savedPlayer);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Chissu\", \"lastName\": \"Essere\"}"))
                .andExpect(status().isOk())
                .andExpect(status().isOk())                          // oggi il POST risponde 200 (non ancora 201)
                .andExpect(jsonPath("$.id").value(8))                // id del giocatore salvato
                .andExpect(jsonPath("$.firstName").value("Chissu"))  // nome
                .andExpect(jsonPath("$.lastName").value("Essere"));  // cognome  // cognome

    }
}
