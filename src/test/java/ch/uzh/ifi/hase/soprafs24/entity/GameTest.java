package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.external_api.getWordlist;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {/*

    @Mock
    private Player player;
    @Mock
    private Player player2;
    @Mock
    private Game game;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private LeaderBoardDTO leaderBoardDTO;
    @Mock
    private GameStateDTO gameStateDTO;

    @BeforeEach
    void setup() {
        ArrayList<Integer> n = new ArrayList<>();
        n.add(2);
        player = new Player("1",1,1,n,"Drawer");
        ArrayList<Integer> n2 = new ArrayList<>();
        n.add(1);
        player2 = new Player("2",2,1,n2,"Guesser");
        game = new Game(player);
        ArrayList<Player> players = new ArrayList<>();
        HashMap<Integer,Player> plHM = new HashMap<>();
        plHM.put(1,player);
        plHM.put(2,player2);
        game.setPlayers(plHM);
        players.add(player);
        players.add(player2);
        PlayerRepository.addPlayer(1,1,player);
        PlayerRepository.addPlayer(2,1,player2);
        game.setConnectedPlayers(players);
        GameRepository.addGame(1,game);
    }
    @AfterEach
    void teardown() {
        GameRepository.removeGame(1);
    }
    @Test
    void updateGameSettingsTest() {
        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setGamePassword("1");
        gameSettingsDTO.setTurnLength(5);
        gameSettingsDTO.setMaxPlayers(5);
        gameSettingsDTO.setMaxRounds(5);
        gameSettingsDTO.setLobbyName("1");
        //gameSettingsDTO.setType("gameSettingsDTO");

        game.updateGameSettings(gameSettingsDTO);

        assertEquals(gameSettingsDTO.getGamePassword(), game.getGamePassword());
        assertEquals(gameSettingsDTO.getTurnLength(), game.getTurnLength());
        assertEquals(gameSettingsDTO.getMaxPlayers(), game.getMaxPlayers());
        assertEquals(gameSettingsDTO.getMaxRounds(), game.getMaxRounds());
        assertEquals(gameSettingsDTO.getLobbyName(), game.getLobbyName());
    }

    @Test
    void setWordListTest() {
        game.startGame();
        assertEquals(10, game.getWordList().size());
        for (int i = 0;i<game.getWordList().size();i++) {
            assertTrue(getWordlist.getWordlist(game.getGenre()).contains(game.getWordList().get(i)));}
    }

    @Test
    void gameStateDTOTest() {//test after some turns...
        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setCurrentRound(0);
        gameStateDTO.setCurrentTurn(0);
        gameStateDTO.setCurrentWordIndex(0);
        gameStateDTO.setDrawer(0);

        GameStateDTO actual = game.gameStateDTO();

        assertEquals(actual.getCurrentRound(),gameStateDTO.getCurrentRound());
    }

    @Test
    void calculateLeaderboardTest() {
        LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
        leaderboardDTO.setUserIdToPlayer(game.getPlayers());
        HashMap<Player,Integer> hm = new HashMap<>();
        HashMap<Player,Integer> hm0 = new HashMap<>();
        HashMap<Integer,Integer> hm2 = new HashMap<>();
        HashMap<Integer,Integer> hm3 = new HashMap<>();
        HashMap<Integer,Integer> hm1 = new HashMap<>();
        hm3.put(2,1);
        hm3.put(1,2);
        hm.put(player,10);
        hm.put(player2,30);
        hm0.put(player,0);
        hm0.put(player2,0);
        hm1.put(1,10);
        hm1.put(2,30);
        hm2.put(1,10);
        hm2.put(2,30);

        game.setPoints(hm0);
        game.setPointsOfCurrentTurn(hm);

        leaderboardDTO.setTotalPoints(hm1);
        leaderboardDTO.setNewlyEarnedPoints(hm2);
        leaderboardDTO.setPodium(hm3);

        LeaderBoardDTO actual = game.calculateLeaderboard();

        assertEquals(actual.getNewlyEarnedPoints(),leaderboardDTO.getNewlyEarnedPoints());
        assertEquals(actual.getTotalPoints(),leaderboardDTO.getTotalPoints());
        assertEquals(actual.getUserIdToPlayer(),leaderboardDTO.getUserIdToPlayer());
        assertEquals(actual.getPodium(),leaderboardDTO.getPodium());
    }*/
}
