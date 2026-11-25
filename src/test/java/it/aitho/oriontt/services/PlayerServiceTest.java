package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.dto.PlayerProfileDTO;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.MatchRepository;
import it.aitho.oriontt.repositories.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository; // Repository finto: niente DB
    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private PlayerService playerService; // Service vero, con dentro il Repository finto

    // GET /api/players
    @Test
    void getAllPlayers() {
        // Arrange: quando il Service chiama findAll(), il Repository finto restituisce una lista con un Player
        // il Repository restituisce Player (non DTO): la trasformazione la fa il Service
        when(playerRepository.findAll()).thenReturn(List.of(getPlayerMock()));

        // Act: chiamo il Service vero, result = lista di DTO creata dal Service
        List<PlayerDTO> result = playerService.getAllPlayers();

        // Assert: il Service ha trasformato bene il Player in DTO
        // get(0) = primo elemento della lista; assertEquals(atteso, reale): se diversi il test è rosso
        assertEquals("Long", result.get(0).getFirstName());
        assertEquals("Ma", result.get(0).getLastName());
    }

    // GET /api/players (lista vuota)
    @Test
    void getAllPlayersEmptyList() {
        when(playerRepository.findAll()).thenReturn(List.of());

        List<PlayerDTO> result = playerService.getAllPlayers();

        assertTrue(result.isEmpty()); // vero se la lista non ha elementi
    }

    // POST /api/players
    @Test
    void createPlayer() {
        // Arrange: giocatore come arriva da Postman (senza id)
        Player player = new Player();
        player.getAccount().setFirstName("Chissu");
        player.getAccount().setLastName("Essere");

        // giocatore dopo il salvataggio nel DB: ha l'id
        Player savedPlayer = new Player();
        savedPlayer.setId(8L);
        savedPlayer.getAccount().setFirstName("Chissu");
        savedPlayer.getAccount().setLastName("Essere");

        when(playerRepository.save(player)).thenReturn(savedPlayer);

        // Act
        Player result = playerService.createPlayer(player);

        // Assert: il Service restituisce il giocatore salvato, con id, first name e last name
        assertEquals(8L, result.getId());
        assertEquals("Chissu", result.getAccount().getFirstName());
        assertEquals("Essere", result.getAccount().getLastName());
    }

    // PATCH /api/players/{id}/email   ← verifica l'URL esatto nel tuo Controller
    @Test
    void updateEmailPlayer() {
        // Arrange: findById restituisce un Optional, quindi Optional.of(...)
        when(playerRepository.findById(1L)).thenReturn(Optional.of(getPlayerMock()));

        // Act: email "sporca" (con maiuscole)
        PlayerDTO result = playerService.updateEmailPlayer(1L, "ma.Long@gmail.it");

        // Assert: l'email è stata sanificata (tutta minuscola)
        assertEquals("ma.long@gmail.it", result.getEmail());
    }

    // GET /api/players/{id}/profile
    @Test
    void getPlayerProfile_whenPlayerExists_returnsProfile() {
        // Arrange: il Repository finto restituisce il Player di getPlayerMock()
        when(playerRepository.findById(1L)).thenReturn(Optional.of(getPlayerMock()));

        // Act: chiamo il Service con il solo id
        PlayerProfileDTO result = playerService.getPlayerProfile(1L);

        // Assert: i campi del DTO sono quelli del Player finto
        assertEquals(1L, result.getId());
        assertEquals("Long", result.getFirstName());
        assertEquals("Ma", result.getLastName());
        assertEquals("IT12345", result.getFitetCardNumber());
    }

    // GET /api/players/{id}/profile 400
    @Test
    void getPlayerProfile_whenIdIsInvalid_throwsBadRequest() {
        // Arrange (when): Qui Non serve, Il Repository qui non viene mai chiamato, con id -1 il Service si ferma all’if

        // Act + Assert: getPlayerProfile deve lanciare l’eccezione
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> playerService.getPlayerProfile(-1L));
        // Assert: lo status dentro l'eccezione deve essere 400 BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        //           ↑ atteso               ↑ attuale (letto dall'eccezione)
    }

    // GET /api/players/{id}/profile 404
    @Test
    void getPlayerProfile_whenIdIsNotFound_throwsNotFound() {
        // Arrange: il Repository finto restituisce un Optional vuoto
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert: getPlayerProfile deve lanciare l’eccezione
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> playerService.getPlayerProfile(999L));
        // Assert: lo status dentro l'eccezione deve essere 404 NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        //           ↑ atteso               ↑ attuale (letto dall'eccezione)
    }

    // helper method: Player finto riusato in più test
    private Player getPlayerMock() {
        Player player = new Player();
        player.setId(1L);
        player.getAccount().setFirstName("Long");
        player.getAccount().setLastName("Ma");
        player.setFitetCardNumber("IT12345");
        return player;
    }
}