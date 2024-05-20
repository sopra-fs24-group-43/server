package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class GameRepositoryTest {/*

    private Game game;

    @Mock
    private Player player;

    @BeforeEach
    void setup() {
        ArrayList<Integer> n = new ArrayList<>();
        n.add(2);
        player = new Player("1", 1, false, 1, n, "Drawer");
        game = new Game(player);
        GameRepository.addGame(1,game);
    }

    @Test
    public void addGameTest() {
        assertEquals(game, GameRepository.findByGameId(1));
    }

    @Test
    public void removeGameTest() {
        GameRepository.removeGame(1);
        assertThrows(ResponseStatusException.class, () -> GameRepository.findByGameId(1));
    }*/
}
