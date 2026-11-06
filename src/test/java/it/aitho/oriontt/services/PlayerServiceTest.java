package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.entities.Match;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
// @Test: questo method è un test, JUnit lo esegue
    void getAllPlayers() {

        // 1. preparo i dati finti: un Player come se arrivasse dal database
        Player player = new Player();
        // costruttore vuoto di Player (quello che usa JPA)
        player.setFirstName("Long");
        player.setLastName("Ma");
        // setter: mettono i valori nei campi del Player

        // 2. dico al Repository finto cosa restituire
        when(playerRepository.findAll()).thenReturn(List.of(player));
        // "quando il Service chiama findAll(), restituisci una lista con questo Player"
        // il Repository restituisce Player (non DTO): la trasformazione la fa il Service

        // 3. chiamo il Service vero
        List<PlayerDTO> result = playerService.getAllPlayers();
        // result = la lista di DTO creata dal Service

        // 4. controllo che il Service abbia trasformato bene il Player in DTO
        assertEquals("Long", result.get(0).getFirstName());
        assertEquals("Ma", result.get(0).getLastName());
        // get(0) = primo elemento della lista (un PlayerDTO)
        // .getFirstName() = getter del DTO: legge il nome
        // assertEquals(atteso, reale): se non coincidono il test diventa rosso
    }

    @Test
    void getAllPlayersEmptyList() {
        when(playerRepository.findAll()).thenReturn(List.of());
        List<PlayerDTO> result = playerService.getAllPlayers();
        assertTrue(result.isEmpty()); // vero se la lista non ha elementi

    }


}
