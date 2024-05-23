package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;

import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;

import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;



@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class GameRepositoryTest {
    @MockBean
    private RandomGenerators randomGenerators;

    @MockBean
    private GetWordlist getWordlist;
    private Game game;

    @MockBean
    private WebSocketService webSocketService;

    @MockBean
    private TimerService timerService;

    @Mock
    private Player player;

    @BeforeEach
    void setup() {
        ArrayList<Integer> n = new ArrayList<>();
        n.add(2);
        player = new Player("1", 1, false, 1, n, "Drawer");
        WebSocketService webSocketService = new WebSocketService();
        TimerService timerService = new TimerService(webSocketService);
        game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        GameRepository.addGame(1,game);
    }
    @AfterEach
    void teardown() {
        GameRepository.removeGame(1);
    }

    @Test
    public void addGameTest() {
        assertEquals(game, GameRepository.findByGameId(1));
    }

    @Test
    public void removeGameTest() {
        GameRepository.removeGame(1);
        assertEquals(null, GameRepository.findByGameId(1));
    }
}
