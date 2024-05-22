package ch.uzh.ifi.hase.soprafs24.controller.wscontroller;

import ch.uzh.ifi.hase.soprafs24.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.Game;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;

import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.InboundPlayer;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.*;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AbstractSoftAssertions.assertAll;
import static org.assertj.core.api.Assertions.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.isEqualToComparingFieldByFieldRecursively;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WebSocketControllerTest {
    @Value("${local.server.port}")
    private String port;

    @Autowired
    WebSocketService webSocketService;
    @Autowired
    TimerService timerService;
    @Autowired
    private WebSocketController webSocketController;
    @MockBean
    private RandomGenerators randomGenerators;
    @MockBean
    private GetWordlist getWordlist;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private final WsTestUtils wsTestUtils = new WsTestUtils();

    @BeforeEach
    public void setUp() throws Exception {
        //port = 8080;
        System.out.println(port);
        String wsUrl = "ws://localhost:" + port + "/ws";
        stompClient = wsTestUtils.createWebSocketClient();
        stompSession = stompClient.connect(wsUrl, new WsTestUtils.MyStompSessionHandler()).get();
    }

    @AfterEach
    public void teardown() throws Exception {
        Game game = GameRepository.findByGameId(1);
        timerService.doShutDownTimer(1);
        if (game != null) {
            game.deletegame(1);
        }
        Game game2 = GameRepository.findByGameId(101);
        if (game2 != null) {
            game2.deletegame(1);
        }
        timerService.doShutDownTimer(101);
        Game game3 = GameRepository.findByGameId(102);
        if (game2 != null) {
            game2.deletegame(1);
        }
        timerService.doShutDownTimer(102);
    }
    @Test
    void connectsToSocket() throws Exception {
        assertThat(stompSession.isConnected()).isTrue();
    }

    @Test
    public void creategameTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);

        InboundPlayer inboundPlayer = new InboundPlayer();
        inboundPlayer.setType("InboundPlayer");
        inboundPlayer.setUsername("Markiian");
        inboundPlayer.setIsGuest(true);
        inboundPlayer.setuserId(1);
        inboundPlayer.setGameId(1);
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        inboundPlayer.setFriends(friends);
        inboundPlayer.setRole("admin");

        when(randomGenerators.GameIdGenerator()).thenReturn(1);


        Thread.sleep(1000);
        webSocketController.creategame(inboundPlayer);

        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("creategame");
        questionToSend.setGameId(inboundPlayer.getGameId());
        questionToSend.setUserId(inboundPlayer.getUserId());
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
    }


    @Test
    public void deletegameTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();
        CompletableFuture<Object> resultKeeper2 = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper2.complete(payload)));


        Thread.sleep(1000);
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends, "player");


        Game game = new Game(player, webSocketService, timerService, randomGenerators ,getWordlist);
        game.addPlayer(player2);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        GameRepository.addGame(gameId, game);

        Thread.sleep(1000);
        webSocketController.deletegame(gameId);

        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("deletegame");
        questionToSend.setGameId(null);
        questionToSend.setUserId(null);
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
        assertThat(resultKeeper2.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);  //never halt most of the time

    }
    @Test
    public void getalllobbiesTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;
        int gameId2 = 102;
        int userId = 1;
        stompSession.subscribe(
                "/topic/landing/" + userId,
                new WsTestUtils.MyStompFrameHandlerGamesDTO((payload) -> resultKeeper.complete(payload)));


        Thread.sleep(1000);
        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");
        Player player3 = new Player("Simon", 3, false, 102, friends2, "admin");
        Player player4 = new Player("Robin", 4, false, 102, friends2, "player");
        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.setGameStarted(true);
        game.addPlayer(player2);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        GameRepository.addGame(gameId, game);
        //second game
        Game game2 = new Game(player3, webSocketService, timerService, randomGenerators, getWordlist);
        game2.setGameStarted(false);
        game2.setGameId(gameId2);
        game2.addPlayer(player4);
        PlayerRepository.addPlayer(1, gameId2, player);
        PlayerRepository.addPlayer(2, gameId2, player2);
        GameRepository.addGame(gameId2, game2);
        Thread.sleep(1000);

        webSocketController.getalllobbies(userId);


        GamesDTO gamesDTO = new GamesDTO();
        List<Integer> listGameIds = new ArrayList<>();
        listGameIds.add(gameId2);
        List<String> listLobbyName = new ArrayList<>();
        listLobbyName.add("Simon's lobby");
        List<List<Player>> listPlayers = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        players.add(player3);
        players.add(player4);
        listPlayers.add(players);
        List<Integer> listMaxPlayers = new ArrayList<>();
        listMaxPlayers.add(5);
        List<String> listGamePassword = new ArrayList<>();
        listGamePassword.add("password");
        gamesDTO.setType("gamesDTO");
        gamesDTO.setGameId(listGameIds);
        gamesDTO.setLobbyName(listLobbyName);
        gamesDTO.setPlayers(listPlayers);
        gamesDTO.setMaxPlayers(listMaxPlayers);
        gamesDTO.setGamePassword(listGamePassword);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gamesDTO);

    }

    @Test
    public void joingameTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();
        CompletableFuture<Object> resultKeeper2 = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerInboundPlayer((payload) -> resultKeeper2.complete(payload)));


        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends2, "admin");
        Thread.sleep(1000);

        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.setGameId(gameId);
        PlayerRepository.addPlayer(1, gameId, player);
        GameRepository.addGame(gameId, game);

        InboundPlayer inboundPlayer = new InboundPlayer();
        inboundPlayer.setType("InboundPlayer");
        inboundPlayer.setUsername("Florian");
        inboundPlayer.setIsGuest(true);
        inboundPlayer.setuserId(2);
        inboundPlayer.setGameId(101);
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(1);
        inboundPlayer.setFriends(friends);
        inboundPlayer.setRole("player");
        Thread.sleep(1000);
        // when
        webSocketController.joingame(gameId, inboundPlayer);
        // then
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("joingame");
        questionToSend.setGameId(null);
        questionToSend.setUserId(null);
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
        assertThat(resultKeeper2.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(inboundPlayer);  //never halt most of the time
    }
    /*
    @Test
    public void leavegameTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();
        CompletableFuture<Object> resultKeeper2 = new CompletableFuture<>();

        int gameId = 101;

        Object Subsctiption = stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        Object Subsctiption2 = stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper2.complete(payload)));
        /*
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends, "player");


        Game game = new Game(player, webSocketService, timerService, randomGenerators);
        game.addPlayer(player2);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        GameRepository.addGame(gameId, game);
        */
        /*
        Thread.sleep(1000);

        webSocketController.leavegame(gameId, 2);

        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("leavegame");
        questionToSend.setGameId(null);
        questionToSend.setUserId(null);
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
        assertThat(resultKeeper2.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);  //never halt most of the time
    }
    */
    @Test
    public void updategamesettingsTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;
        stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerGameSettingsDTO((payload) -> resultKeeper.complete(payload)));

        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends2, "admin");
        Thread.sleep(1000);

        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.setGameId(gameId);
        PlayerRepository.addPlayer(1, gameId, player);
        GameRepository.addGame(gameId, game);

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setType("gameSettings");
        gameSettingsDTO.setMaxPlayers(3);
        gameSettingsDTO.setMaxRounds(1);
        gameSettingsDTO.setTurnLength(15);
        gameSettingsDTO.setGamePassword("password");
        gameSettingsDTO.setLobbyName("my lobby");
        ArrayList<String> genres  =new ArrayList<>();
        genres.add("nature");
        genres.add("school");
        gameSettingsDTO.setGenres(genres);

        Thread.sleep(1000);

        webSocketController.updategamesettings(gameId, gameSettingsDTO);


        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameSettingsDTO);
    }
    @Test
    public void getlobbyinfoTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;
        stompSession.subscribe(
                "/topic/games/"+ gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerLobbyInfo((payload) -> resultKeeper.complete(payload)));


        Thread.sleep(1000);
        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");
        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.setGameStarted(true);
        game.addPlayer(player2);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        GameRepository.addGame(gameId, game);
        //second game
        Thread.sleep(1000);

        webSocketController.getlobbyinfo(gameId);


        LobbyInfo lobbyInfo = new LobbyInfo();
        lobbyInfo.setType("getlobbyinfo");
        lobbyInfo.setGameId(gameId);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        players.put(2, player2);
        lobbyInfo.setPlayers(players);

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setType("GameSettingsDTO");
        gameSettingsDTO.setMaxPlayers(5);
        gameSettingsDTO.setMaxRounds(5);
        gameSettingsDTO.setTurnLength(60);
        gameSettingsDTO.setGamePassword("password");
        gameSettingsDTO.setLobbyName("Markiian's lobby");
        gameSettingsDTO.setGenres(new ArrayList<>());
        lobbyInfo.setGameSettingsDTO(gameSettingsDTO);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(lobbyInfo);
    }
    /*
    @Test
    public void startgameTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();
        CompletableFuture<Object> resultKeeper2 = new CompletableFuture<>();

        int gameId = 101;
        stompSession.subscribe(
                "/topic/games/"+ gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerGameStateDTO((payload) -> resultKeeper.complete(payload)));

        stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper2.complete(payload)));

        Thread.sleep(1000);

        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");
        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.setGameStarted(true);
        game.addPlayer(player2);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        GameRepository.addGame(gameId, game);

        Thread.sleep(1000);


        ArrayList<String> words = new ArrayList<>();
        words.add("firstword");
        words.add("secondword");
        words.add("thirdword");
        ArrayList<String> words2 = new ArrayList<>();
        words2.add("forthword");
        words2.add("fifthword");
        words2.add("sixthword");
        ArrayList<String> words3 = new ArrayList<>();
        words3.add("firstword");
        words3.add("secondword");
        words3.add("thirdword");
        words3.add("forthword");
        words3.add("fifthword");
        words3.add("sixthword");
        when(getWordlist.getWordlist2("Science")).thenReturn(words);
        when(getWordlist.getWordlist2("Animal")).thenReturn(words2);
        when(randomGenerators.DoShuffle(words3)).thenReturn(words3);

        webSocketController.startgame(gameId);


        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(false);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        players.put(2, player2);
        gameStateDTO.setConnectedPlayers(players);
        gameStateDTO.setPlayersOriginally(2);
        gameStateDTO.setCurrentRound(1);
        gameStateDTO.setCurrentTurn(0);
        gameStateDTO.setThreeWords(words);
        gameStateDTO.setDrawer(-1);
        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(1);
        drawingOrderLeavers.add(2);
        gameStateDTO.setDrawingOrder(drawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(5);
        gameStateDTO.setGamePhase("started");
        gameStateDTO.setActualCurrentWord(null);


        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("startgame");

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);
        assertThat(resultKeeper2.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
    }
    */
    @Test
    public void getgamestateTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;
        int userId  = 1;
        stompSession.subscribe(
                "/topic/games/"+ gameId + "/general/" + userId,
                new WsTestUtils.MyStompFrameHandlerGameStateDTO((payload) -> resultKeeper.complete(payload)));



        Thread.sleep(1000);

        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");
        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.addPlayer(player2);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        GameRepository.addGame(gameId, game);

        Thread.sleep(1000);

        /*
        ArrayList<String> words = new ArrayList<>();
        words.add("firstword");
        words.add("secondword");
        words.add("thirdword");
        ArrayList<String> words2 = new ArrayList<>();
        words2.add("forthword");
        words2.add("fifthword");
        words2.add("sixthword");
        ArrayList<String> words3 = new ArrayList<>();
        words3.add("firstword");
        words3.add("secondword");
        words3.add("thirdword");
        words3.add("forthword");
        words3.add("fifthword");
        words3.add("sixthword");
        when(getWordlist.getWordlist2("Science")).thenReturn(words);
        when(getWordlist.getWordlist2("Animal")).thenReturn(words2);
        when(randomGenerators.DoShuffle(words3)).thenReturn(words3);
        */
        webSocketController.getgamestate(gameId, userId);


        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(false);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        gameStateDTO.setConnectedPlayers(players);
        gameStateDTO.setPlayersOriginally(0);
        gameStateDTO.setCurrentRound(0);
        gameStateDTO.setCurrentTurn(0);
        ArrayList<String> threeWords = new ArrayList<>();
        gameStateDTO.setThreeWords(threeWords);
        gameStateDTO.setDrawer(0);
        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        //drawingOrderLeavers.add(1);
        //drawingOrderLeavers.add(2);
        gameStateDTO.setDrawingOrder(drawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(5);
        gameStateDTO.setGamePhase("inLobby");
        gameStateDTO.setActualCurrentWord(null);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);
    }
    /*
    @Test
    public void nextturnTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/"+ gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerGameStateDTO((payload) -> resultKeeper.complete(payload)));



        Thread.sleep(1000);

        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");
        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.addPlayer(player2);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(1);
        drawingOrderLeavers.add(2);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        GameRepository.addGame(gameId, game);

        Thread.sleep(1000);

        /*
        ArrayList<String> words = new ArrayList<>();
        words.add("firstword");
        words.add("secondword");
        words.add("thirdword");
        ArrayList<String> words2 = new ArrayList<>();
        words2.add("forthword");
        words2.add("fifthword");
        words2.add("sixthword");
        ArrayList<String> words3 = new ArrayList<>();
        words3.add("firstword");
        words3.add("secondword");
        words3.add("thirdword");
        words3.add("forthword");
        words3.add("fifthword");
        words3.add("sixthword");
        when(getWordlist.getWordlist2("Science")).thenReturn(words);
        when(getWordlist.getWordlist2("Animal")).thenReturn(words2);
        when(randomGenerators.DoShuffle(words3)).thenReturn(words3);
        */
    /*
        webSocketController.nextturn(gameId);


        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(false);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        gameStateDTO.setConnectedPlayers(players);
        gameStateDTO.setPlayersOriginally(0);
        gameStateDTO.setCurrentRound(0);
        gameStateDTO.setCurrentTurn(1);
        ArrayList<String> threeWords = new ArrayList<>();
        gameStateDTO.setThreeWords(threeWords);
        gameStateDTO.setDrawer(0);

        gameStateDTO.setDrawingOrder(drawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(5);
        gameStateDTO.setGamePhase("inLobby");
        gameStateDTO.setActualCurrentWord(null);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);
    }
*/
}
