package it.aitho.oriontt.services;

import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.repositories.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    // Repository finto: non tocca il database, decidiamo noi cosa restituisce
    @Mock
    private MatchRepository matchRepository;

    // Il Service vero, a cui Mockito passa il repository finto
    @InjectMocks
    private MatchService matchService;

    // Caso: id 0 → il Service deve lanciare un 400 Bad Request
    @Test
    void getMatchById_idZero_throwsBadRequest() {
        // assertThrows: verifica che il method lanci proprio questa exception
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.getMatchById(0L));

        // Controlla che lo status dentro l'exception sia 400
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // Caso: partita inesistente → il Service deve lanciare un 404 Not Found
    @Test
    void getMatchById_notFound_throwsNotFound() {
        // Diciamo al repository finto: se cercano l'id 90, restituisci un Optional vuoto
        when(matchRepository.findById(90L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> matchService.getMatchById(90L));

        // Controlla che lo status sia 404
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // Caso: partita esistente → il Service deve restituirla
    @Test
    void getMatchById_found_returnsMatch() {
        // Creiamo una partita finta con id 1
        Match match = new Match();
        match.setId(1L);

        // Il repository finto la restituisce quando cercano l'id 1
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Chiamiamo il Service vero
        Match result = matchService.getMatchById(1L);

        // Controlla che sia proprio la partita con id 1
        assertEquals(1L, result.getId());
    }

}