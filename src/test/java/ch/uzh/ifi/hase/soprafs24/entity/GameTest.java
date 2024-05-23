package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;

import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class GameTest {/*

    @MockBean
    private RandomGenerators randomGenerators;
    @MockBean
    private GetWordlist getWordlist;


    @MockBean
    private WebSocketService webSocketService;
    @MockBean
    private TimerService timerService;

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
        player = new Player("1", 1, false, 1, n, "Drawer");
        ArrayList<Integer> n2 = new ArrayList<>();
        n.add(1);
        player2 = new Player("2",2,false,1,n2,"Guesser");
        WebSocketService webSocketService = new WebSocketService();
        TimerService timerService = new TimerService(webSocketService);
        game = new Game(player, webSocketService, timerService, randomGenerators ,getWordlist);
        ArrayList<Player> players = new ArrayList<>();
        HashMap<Integer,Player> plHM = new HashMap<>();
        plHM.put(1,player);
        plHM.put(2,player2);
        game.setPlayers(plHM);
        players.add(player);
        players.add(player2);
        PlayerRepository.addPlayer(1,1,player);
        PlayerRepository.addPlayer(2,1,player2);
        HashMap<Integer,Player> playershm = new HashMap<>();
        playershm.put(1,player);
        playershm.put(2,player2);
        game.setConnectedPlayers(playershm);
        GameRepository.addGame(1,game);
    }
    @AfterEach
    void teardown() {
        GameRepository.removeGame(1);
        PlayerRepository.removePlayer(1,1);
        PlayerRepository.removePlayer(2,1);
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


        GameSettingsDTO GSDTO = game.getGameSettingsDTO();
        assertEquals(gameSettingsDTO.getLobbyName(), GSDTO.getLobbyName());
        assertEquals(gameSettingsDTO.getGamePassword(), GSDTO.getGamePassword());
        assertEquals(gameSettingsDTO.getTurnLength(), GSDTO.getTurnLength());
        assertEquals(gameSettingsDTO.getMaxPlayers(), GSDTO.getMaxPlayers());
        assertEquals(gameSettingsDTO.getMaxRounds(), GSDTO.getMaxRounds());
    }

    @Test
    void setWordListTest() {
        game.startGame();

        for (int j = 0;j<game.getGenres().size();j++){
            List<String> l1 = new ArrayList<>();
            l1.addAll(getWordlist.getWordlist2(game.getGenres().get(j)));
            List<String> l2 = getWordlist.getWordlist2(game.getGenres().get(j));
            for (int i = 0;i<game.getWordList().size();i++) {
                if (l1.contains(game.getWordList().get(i))) {

                    assertTrue(l2.contains(game.getWordList().get(i)));
                    l1.remove(game.getWordList().get(i));
                }}
    }}

    @Test
    void startGameTest() {
        game.startGame();
        HashMap<Integer,Player> playershm = new HashMap<>();
        playershm.put(1,player);
        playershm.put(2,player2);


        assertEquals(game.getPlayersOriginally(),2);
        assertEquals(game.getPlayers(),playershm);
        assertEquals(game.getConnectedPlayers(),playershm);

    }

/*error this.websocketService is null
    @Test
    void terminateGameTest() {
        game.startGame();
        game.terminategame(1,"admin left");
        assertEquals(null, GameRepository.findByGameId(1));
    }

 *//*

    @Test
    void nextturnTest() {
        ArrayList<String> words = new ArrayList<>();
        words.add("firstword");
        words.add("secondword");
        words.add("thirdword");
        words.add("forthword");
        words.add("fifthword");
        ArrayList<String> words2 = new ArrayList<>();
        words2.add("sixthword");
        words2.add("seventhword");
        words2.add("eightword");
        words2.add("ninthword");
        words2.add("tenthword");
        ArrayList<String> words3 = new ArrayList<>();
        words3.add("firstword");
        words3.add("secondword");
        words3.add("thirdword");
        words3.add("forthword");
        words3.add("fifthword");
        words3.add("sixthword");
        words3.add("seventhword");
        words3.add("eightword");
        words3.add("ninthword");
        words3.add("tenthword");
        when(getWordlist.getWordlist2("Science")).thenReturn(words);
        when(getWordlist.getWordlist2("Animal")).thenReturn(words2);
        when(randomGenerators.DoShuffle(words3)).thenReturn(words3);
        //added the above so startgame doesnt use getWordlist to get from API and then shuffles them (random)
        game.startGame();
        game.nextturn(1);
        game.nextturn(1);
        //game.setCurrentTurn(2);
        //game.setCurrentRound(1);
        int i = game.getCurrentWordIndex();
        int j = game.getDrawer();
        game.nextturn(1);

        assertEquals("choosing", game.getGamePhase());
        assertEquals(1, game.getCurrentTurn());
        assertEquals(2, game.getCurrentRound());
        assertEquals(0, game.getDrawer());
        assertEquals(7, game.getCurrentWordIndex());  //changed to 7 (from 10) because the first nextturn doesnt add +3 (gamePhase == "started", after its "choosing")

        GameStateDTO actual = game.receiveGameStateDTO();

        assertEquals(actual.getCurrentRound(), 2);
        assertEquals(actual.getCurrentTurn(), 1);
        assertEquals(actual.getDrawer(), 0);
        //assertEquals(actual.getActualCurrentWord(),game.getCurrentWord());
        List<String> three_words = new ArrayList<>();
        //the threewords:
        three_words.add("seventhword");
        three_words.add("eightword");
        three_words.add("ninthword");
        //three_words.add(game.getWordList().get(game.getCurrentWordIndex()-1));  not need now cause it uses the Mock
        //three_words.add(game.getWordList().get(game.getCurrentWordIndex()));
        //three_words.add(game.getWordList().get(game.getCurrentWordIndex()+1));
        assertEquals(actual.getThreeWords(), three_words);

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
        LinkedHashMap<Integer,Player> hm4 = new LinkedHashMap<>();
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
        hm4.put(2,player2);
        hm4.put(1,player);

        game.setPoints(hm0);
        game.setPointsOfCurrentTurn(hm);

        leaderboardDTO.setTotalPoints(hm1);
        leaderboardDTO.setNewlyEarnedPoints(hm2);
        leaderboardDTO.setPodium(hm3);
        leaderboardDTO.setUserIdToPlayerSorted(hm4);

        LeaderBoardDTO actual = game.calculateLeaderboard();

        assertEquals(actual.getNewlyEarnedPoints(),leaderboardDTO.getNewlyEarnedPoints());
        assertEquals(actual.getTotalPoints(),leaderboardDTO.getTotalPoints());
        assertEquals(actual.getUserIdToPlayer(),leaderboardDTO.getUserIdToPlayer());
        assertEquals(actual.getPodium(),leaderboardDTO.getPodium());

        assertEquals(actual.getUserIdToPlayerSorted(),leaderboardDTO.getUserIdToPlayerSorted());
    }*/
}
