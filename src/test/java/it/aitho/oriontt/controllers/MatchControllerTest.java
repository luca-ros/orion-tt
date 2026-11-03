package it.aitho.oriontt.controllers;

import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.services.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Avvia solo la parte web di Spring, solo per MatchController (niente database)
@WebMvcTest(MatchController.class)
class MatchControllerTest {

    // MockMvc: simula le richieste HTTP, come Postman ma dentro il test
    @Autowired
    private MockMvc mockMvc;

    // Service finto: Spring lo mette nel Controller al posto di quello vero
    @MockitoBean
    private MatchService matchService;

    // Caso: GET /api/matches/1 → il Controller risponde 200 con la partita
    @Test
    void getMatch_found_returns200() throws Exception {
        // Partita finta con id 1
        Match match = new Match();
        match.setId(1L);

        // Il Service finto la restituisce quando gli chiedono l'id 1
        when(matchService.getMatchById(1L)).thenReturn(match);

        // Simula la richiesta HTTP e controlla la risposta
        mockMvc.perform(get("/api/matches/1"))
                .andExpect(status().isOk())              // status 200
                .andExpect(jsonPath("$.id").value(1));   // nel JSON, il campo id vale 1
    }

    // Caso: partita inesistente → il Service finto lancia 404, il Controller deve rispondere 404
    @Test
    void getMatch_notFound_returns404() throws Exception {
        when(matchService.getMatchById(90L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        mockMvc.perform(get("/api/matches/90"))
                .andExpect(status().isNotFound());   // status 404
    }

    // Caso: lettere nell'URL → Spring non converte in Long, risponde 400 senza chiamare il Service
    @Test
    void getMatch_lettersInId_returns400() throws Exception {
        mockMvc.perform(get("/api/matches/cocco"))
                .andExpect(status().isBadRequest()); // status 400
    }
}