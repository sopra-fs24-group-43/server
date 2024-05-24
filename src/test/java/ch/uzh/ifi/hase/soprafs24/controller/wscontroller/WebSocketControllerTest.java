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


import ch.uzh.ifi.hase.soprafs24.utils.ReconnectionHelper;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.*;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.*;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AbstractSoftAssertions.assertAll;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.isEqualToComparingFieldByFieldRecursively;
import static org.assertj.core.api.Assertions.*;
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
    //Lock sequential = new ReentrantLock();
    @BeforeEach
    public void setUp() throws Exception {
        //sequential.lock();
        //port = 8080;
        System.out.println(port);
        String wsUrl = "ws://localhost:" + port + "/ws";
        stompClient = wsTestUtils.createWebSocketClient();
        stompSession = stompClient.connect(wsUrl, new WsTestUtils.MyStompSessionHandler()).get();
    }

    @AfterEach
    public void teardown() throws Exception {
        //sequential.unlock();
        //stompClient.stop();
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
        if (game3 != null) {
            game3.deletegame(1);
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

        StompSession.Subscription Subscription = stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        StompSession.Subscription Subscription2 = stompSession.subscribe(
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
        Subscription.unsubscribe();
        Subscription2.unsubscribe();

    }

    @Test
    public void leavegameTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();
        CompletableFuture<Object> resultKeeper2 = new CompletableFuture<>();

        int gameId = 101;

        StompSession.Subscription Subscription = stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        StompSession.Subscription Subscription2= stompSession.subscribe(
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

        Thread.sleep(1000);

        webSocketController.leavegame(gameId, 2);

        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("leavegame");
        questionToSend.setGameId(null);
        questionToSend.setUserId(null);
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
        assertThat(resultKeeper2.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);  //never halt most of the time
        Subscription.unsubscribe();
        Subscription2.unsubscribe();
    }

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
        ArrayList<String> genres  =new ArrayList<>();
        genres.add("Science");
        genres.add("Animal");
        game.setGenres(genres);
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
        gameStateDTO.setDrawer(-1);
        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        //drawingOrderLeavers.add(1);
        //drawingOrderLeavers.add(2);
        gameStateDTO.setDrawingOrder(drawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(5);
        gameStateDTO.setGamePhase("inLobby");
        gameStateDTO.setActualCurrentWord(null);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);
    }

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
        gameStateDTO.setGamePhase("choosing");
        gameStateDTO.setActualCurrentWord(null);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);
    }

    @Test
    public void sendchosenword() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/"+ gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerChooseWordDTO((payload) -> resultKeeper.complete(payload)));
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

        ChooseWordDTO chooseWordDTO = new ChooseWordDTO();
        chooseWordDTO.setType("chooseword");
        chooseWordDTO.setWordIndex(1);
        chooseWordDTO.setWord("Family");

        webSocketController.sendchosenword(gameId, chooseWordDTO);

        ChooseWordDTO chooseWordDTO2 = new ChooseWordDTO();
        chooseWordDTO2.setType("startdrawing");
        chooseWordDTO2.setWordIndex(1);
        chooseWordDTO2.setWord("Family");
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(chooseWordDTO2);
        assertThat(game.getActualCurrentWord()).isEqualTo("Family");
        assertThat(game.getGamePhase()).isEqualTo("drawing");
    }

    @Test
    public void sendguess() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/"+ gameId + "/sendguess",
                new WsTestUtils.MyStompFrameHandlerAnswer((payload) -> resultKeeper.complete(payload)));
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
        Answer answer = new Answer();
        answer.setType("Answer");
        answer.setUsername("Markiian");
        answer.setAnswerString("Birthday");
        answer.setIsCorrect(null);
        answer.setPlayerHasGuessedCorrectly(null);

        webSocketController.sendguess(gameId, answer);
        Answer answer2 = new Answer();
        answer2.setType("Answer");
        answer2.setUsername("Markiian");
        answer2.setAnswerString("Birthday");
        answer2.setIsCorrect(false);
        answer2.setPlayerHasGuessedCorrectly(false);
        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(answer);
        assertThat(resultKeeper.get(3, SECONDS)).isEqualToComparingFieldByFieldRecursively(answer2);
        assertThat(game.getAnswers()).isEqualTo(answers);
        assertThat(game.getAnswersReceived()).isEqualTo(1);
    }
    @Test
    public void terminategameTest() throws Exception {
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

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);

        Thread.sleep(1000);

        game.terminategame(gameId, "normal");

        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(true);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        gameStateDTO.setConnectedPlayers(players);
        gameStateDTO.setPlayersOriginally(0);
        gameStateDTO.setCurrentRound(5);
        gameStateDTO.setCurrentTurn(0);
        ArrayList<String> threeWords = new ArrayList<>();
        gameStateDTO.setThreeWords(threeWords);
        gameStateDTO.setDrawer(-1);

        gameStateDTO.setDrawingOrder(drawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(5);
        gameStateDTO.setGamePhase("leaderboard");
        gameStateDTO.setActualCurrentWord(null);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);
        assertThat(game.getReason()).isEqualTo("normal");
    }

    @Test
    public void intigrateintogameTest() throws Exception {
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
        drawingOrderLeavers.add(0);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        HashMap<Integer, Player> connectedPlayers = new HashMap<>();
        connectedPlayers.put(1, player);
        game.setConnectedPlayers(connectedPlayers);
        GameRepository.addGame(gameId, game);

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);

        Thread.sleep(1000);

        game.intigrateIntoGame(2, gameId);

        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(false);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        players.put(2, player2);
        gameStateDTO.setConnectedPlayers(players);
        gameStateDTO.setPlayersOriginally(0);
        gameStateDTO.setCurrentRound(0);
        gameStateDTO.setCurrentTurn(0);
        ArrayList<String> threeWords = new ArrayList<>();
        gameStateDTO.setThreeWords(threeWords);
        gameStateDTO.setDrawer(-1);
        ArrayList<Integer> newdrawingOrderLeavers = new ArrayList<>();
        newdrawingOrderLeavers.add(1);
        newdrawingOrderLeavers.add(2);
        gameStateDTO.setDrawingOrder(newdrawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(5);
        gameStateDTO.setGamePhase("inLobby");
        gameStateDTO.setActualCurrentWord(null);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);
    }
    @Test
    public void lostconnectiongameTest() throws Exception {
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

        Player player3 = new Player("Simon", 3, false, 101, friends2, "player");

        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.addPlayer(player2);
        game.addPlayer(player3);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        PlayerRepository.addPlayer(3, gameId, player3);

        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(1);
        drawingOrderLeavers.add(2);
        drawingOrderLeavers.add(3);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        HashMap<Integer, Player> connectedPlayers = new HashMap<>();
        connectedPlayers.put(1, player);
        connectedPlayers.put(2, player2);
        connectedPlayers.put(3, player3);
        game.setDrawer(2);
        game.setGamePhase("choosing");
        game.setConnectedPlayers(connectedPlayers);
        GameRepository.addGame(gameId, game);

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);

        Thread.sleep(1000);

        game.lostConnectionToPlayer(3, gameId);

        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(false);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        players.put(2, player2);
        gameStateDTO.setConnectedPlayers(players);
        gameStateDTO.setPlayersOriginally(0);
        gameStateDTO.setCurrentRound(0);
        gameStateDTO.setCurrentTurn(0);
        ArrayList<String> threeWords = new ArrayList<>();
        gameStateDTO.setThreeWords(threeWords);
        gameStateDTO.setDrawer(2);
        ArrayList<Integer> newdrawingOrderLeavers = new ArrayList<>();
        newdrawingOrderLeavers.add(1);
        newdrawingOrderLeavers.add(2);
        newdrawingOrderLeavers.add(0);
        gameStateDTO.setDrawingOrder(newdrawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(5);
        gameStateDTO.setGamePhase("choosing");
        gameStateDTO.setActualCurrentWord(null);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);
    }

    @Test
    public void reconnectionhelpreloadTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/"+ gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));
        Thread.sleep(1000);

        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");

        Player player3 = new Player("Simon", 3, false, 101, friends2, "player");

        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.addPlayer(player2);
        game.addPlayer(player3);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        PlayerRepository.addPlayer(3, gameId, player3);

        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(1);
        drawingOrderLeavers.add(2);
        drawingOrderLeavers.add(3);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        HashMap<Integer, Player> connectedPlayers = new HashMap<>();
        connectedPlayers.put(1, player);
        connectedPlayers.put(2, player2);
        connectedPlayers.put(3, player3);
        game.setDrawer(2);
        game.setGamePhase("choosing");
        game.setConnectedPlayers(connectedPlayers);
        game.setGameStarted(true);
        GameRepository.addGame(gameId, game);

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);

        Thread.sleep(1000);
        ReconnectionHelper reconnectionHelper = new ReconnectionHelper(webSocketService);
        reconnectionHelper.reconnectionhelp(3,true);

        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("sendcanvasforrecon");
        questionToSend.setUserId(3);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
    }
    @Test
    public void reconnectionhelpreconTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/landing/alertreconnect/" + 3,
                new WsTestUtils.MyStompFrameHandlerReconnectionDTO((payload) -> resultKeeper.complete(payload)));
        Thread.sleep(1000);

        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");

        Player player3 = new Player("Simon", 3, false, 101, friends2, "player");

        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.addPlayer(player2);
        game.addPlayer(player3);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        PlayerRepository.addPlayer(3, gameId, player3);

        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(1);
        drawingOrderLeavers.add(2);
        drawingOrderLeavers.add(3);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        HashMap<Integer, Player> connectedPlayers = new HashMap<>();
        connectedPlayers.put(1, player);
        connectedPlayers.put(2, player2);
        connectedPlayers.put(3, player3);
        game.setDrawer(2);
        game.setGamePhase("choosing");
        game.setConnectedPlayers(connectedPlayers);
        game.setGameStarted(true);
        GameRepository.addGame(gameId, game);

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);

        Thread.sleep(1000);
        ReconnectionHelper reconnectionHelper = new ReconnectionHelper(webSocketService);
        reconnectionHelper.reconnectionhelp(3,false);

        ReconnectionDTO reconnectionDTO = new ReconnectionDTO();
        reconnectionDTO.setType("ReconnectionDTO");
        reconnectionDTO.setGameId(gameId);
        reconnectionDTO.setRole("player");
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(reconnectionDTO);
    }

    @Test
    public void fillCanvasFillToolTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/fillTool",
                new WsTestUtils.MyStompFrameHandlerFillToolCoordinates((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);

        FillToolCoordinates fillToolCoordinates = new FillToolCoordinates();
        fillToolCoordinates.setFillSelected(true);


        Thread.sleep(1000);
        webSocketController.fillCanvas(gameId, fillToolCoordinates);


        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(fillToolCoordinates);
    }
    @Test
    public void fillCanvasDrawTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/draw",
                new WsTestUtils.MyStompFrameHandlerDrawCoordinates((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);

        DrawCoordinates drawCoordinates = new DrawCoordinates();
        drawCoordinates.setDrawSelected(true);


        Thread.sleep(1000);
        webSocketController.fillCanvas(gameId, drawCoordinates);


        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(drawCoordinates);
    }
    @Test
    public void fillCanvasEraserTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/eraser",
                new WsTestUtils.MyStompFrameHandlerEraserCoordinates((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);

        EraserCoordinates eraserCoordinates = new EraserCoordinates();
        eraserCoordinates.setEraserSelected(true);


        Thread.sleep(1000);
        webSocketController.fillCanvas(gameId, eraserCoordinates);


        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(eraserCoordinates);
    }
    @Test
    public void fillCanvasEraseAllTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/eraseAll",
                new WsTestUtils.MyStompFrameHandlerEraseAllCoordinates((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);

        EraseAllCoordinates eraseAllCoordinates = new EraseAllCoordinates();
        eraseAllCoordinates.setEraseAllVar("EraseAll");


        Thread.sleep(1000);
        webSocketController.fillCanvas(gameId, eraseAllCoordinates);


        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(eraseAllCoordinates);
    }
    @Test
    public void fillCanvasFillTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/fill",
                new WsTestUtils.MyStompFrameHandlerFillCoordinates((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);

        FillCoordinates fillCoordinates = new FillCoordinates();
        fillCoordinates.setImageDataBuffer("imageDataBuffer");


        Thread.sleep(1000);
        webSocketController.fillCanvas(gameId, fillCoordinates);


        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(fillCoordinates);
    }
    @Test
    public void sendCanvasCoordinatesTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/coordinates",
                new WsTestUtils.MyStompFrameHandlerCoordinates((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);

        Coordinates coordinates = new Coordinates();
        coordinates.setEraserSelected(true);
        coordinates.setX(1);
        coordinates.setY(1);
        coordinates.setSelectedColor("blue");
        coordinates.setStrokeSize(2);
        coordinates.setNewX(2);
        coordinates.setNewY(2);


        Thread.sleep(1000);
        webSocketController.sendCanvas(gameId, coordinates);


        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(coordinates);
    }
    @Test
    public void createguestplayerTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerInboundPlayer((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);



        when(randomGenerators.GuestIdGenerator()).thenReturn(-999);


        Thread.sleep(1000);
        webSocketController.createguestplayer();

        InboundPlayer inboundPlayer = new InboundPlayer();
        inboundPlayer.setType("createPlayerFromGuest");
        inboundPlayer.setUsername("guestuser999");
        inboundPlayer.setIsGuest(true);
        inboundPlayer.setuserId(-999);
        inboundPlayer.setGameId(-1);
        ArrayList<Integer> friends = new ArrayList<>();
        inboundPlayer.setFriends(friends);
        inboundPlayer.setRole("guest");
        Player player = new Player("guestuser999", -999, true, -1, friends, "guest");
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(inboundPlayer);
        boolean guestIdtaken = PlayerRepository.guestIdtaken(-999);
        assert(guestIdtaken);
        Player inPlayerRepoguest = PlayerRepository.findByGuestId(-999);
        Player inPlayerRepouserId = PlayerRepository.findByUserId(-999);
        assertThat(inPlayerRepoguest).isEqualToComparingFieldByFieldRecursively(player);
        assertThat(inPlayerRepouserId).isEqualToComparingFieldByFieldRecursively(player);
    }

    @Test
    public void sendreloadattrTest() throws Exception {
        Thread.sleep(1000);
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        HashMap<String, Object> sessionAttributes = new HashMap<>();
        headerAccessor.setSessionAttributes(sessionAttributes);
        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setUserId(null);
        sessionAttributeDTO.setReload(true);
        webSocketController.sendreloadAttr(sessionAttributeDTO,  headerAccessor);
        Thread.sleep(1000);
        HashMap<String, Boolean> reload = new HashMap<>();
        reload.put("reload", true);
        assertThat(headerAccessor.getSessionAttributes()).isEqualToComparingFieldByFieldRecursively(reload);

    }

    @Test
    public void senduseridattrTest() throws Exception {


        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        HashMap<String, Object> sessionAttributes = new HashMap<>();
        headerAccessor.setSessionAttributes(sessionAttributes);
        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setUserId(-999);
        sessionAttributeDTO.setReload(null);
        webSocketController.senduserIdAttr(sessionAttributeDTO,  headerAccessor);
        Thread.sleep(1000);
        HashMap<String, Integer> userId = new HashMap<>();
        userId.put("userId", -999);
        assertThat(headerAccessor.getSessionAttributes()).isEqualToComparingFieldByFieldRecursively(userId);

    }
    @Test
    public void alertreconnect1Test() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));
        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");

        Player player3 = new Player("Simon", 3, false, 101, friends2, "player");

        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.addPlayer(player2);
        game.addPlayer(player3);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        PlayerRepository.addPlayer(3, gameId, player3);

        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(1);
        drawingOrderLeavers.add(2);
        drawingOrderLeavers.add(3);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        HashMap<Integer, Player> connectedPlayers = new HashMap<>();
        connectedPlayers.put(1, player);
        connectedPlayers.put(2, player2);
        connectedPlayers.put(3, player3);
        game.setDrawer(2);
        game.setGamePhase("choosing");
        game.setConnectedPlayers(connectedPlayers);
        game.setGameStarted(true);
        GameRepository.addGame(gameId, game);

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);
        Thread.sleep(1000);
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        HashMap<String, Object> sessionAttributes = new HashMap<>();
        headerAccessor.setSessionAttributes(sessionAttributes);
        timerService.doTimerForReloadDisc(101, 1, "inLobbyAdmin");
        Thread.sleep(500);
        webSocketController.alertreconnect(1,  headerAccessor);
        Thread.sleep(1000);
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("sendcanvasforrecon");
        questionToSend.setUserId(1);

        assertThat(headerAccessor.getSessionAttributes().get("userId")).isEqualTo(1);
        assertThat(headerAccessor.getSessionAttributes().get("reload")).isEqualTo(false);
        assertThat(resultKeeper.get(2, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);

    }
    @Test
    public void alertreconnect2Test() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/landing/alertreconnect/" + 1,
                new WsTestUtils.MyStompFrameHandlerReconnectionDTO((payload) -> resultKeeper.complete(payload)));
        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");

        Player player3 = new Player("Simon", 3, false, 101, friends2, "player");

        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.addPlayer(player2);
        game.addPlayer(player3);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        PlayerRepository.addPlayer(3, gameId, player3);

        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(1);
        drawingOrderLeavers.add(2);
        drawingOrderLeavers.add(3);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        HashMap<Integer, Player> connectedPlayers = new HashMap<>();
        connectedPlayers.put(1, player);
        connectedPlayers.put(2, player2);
        connectedPlayers.put(3, player3);
        game.setDrawer(2);
        game.setGamePhase("choosing");
        game.setConnectedPlayers(connectedPlayers);
        game.setGameStarted(true);
        GameRepository.addGame(gameId, game);

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);
        Thread.sleep(1000);
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        HashMap<String, Object> sessionAttributes = new HashMap<>();
        headerAccessor.setSessionAttributes(sessionAttributes);
        webSocketController.alertreconnect(1,  headerAccessor);
        Thread.sleep(1000);
        ReconnectionDTO reconnectionDTO = new ReconnectionDTO();
        reconnectionDTO.setType("ReconnectionDTO");
        reconnectionDTO.setGameId(player.getGameId());
        reconnectionDTO.setRole(player.getRole());

        assertThat(headerAccessor.getSessionAttributes().get("userId")).isEqualTo(1);
        assertThat(headerAccessor.getSessionAttributes().get("reload")).isEqualTo(false);
        assertThat(resultKeeper.get(4, SECONDS)).isEqualToComparingFieldByFieldRecursively(reconnectionDTO);

    }
    /*
    @Test
    public void reconnectTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerGameStateDTO((payload) -> resultKeeper.complete(payload)));
        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");
        ArrayList<Integer> friends2 = new ArrayList<>();
        friends2.add(1);
        Player player2 = new Player("Florian", 2, false, 101, friends2, "player");

        Player player3 = new Player("Simon", 3, false, 101, friends2, "player");

        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        game.addPlayer(player2);
        game.addPlayer(player3);
        PlayerRepository.addPlayer(1, gameId, player);
        PlayerRepository.addPlayer(2, gameId, player2);
        PlayerRepository.addPlayer(3, gameId, player3);

        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(0);
        drawingOrderLeavers.add(2);
        drawingOrderLeavers.add(3);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        HashMap<Integer, Player> connectedPlayers = new HashMap<>();
        connectedPlayers.put(1, player);
        connectedPlayers.put(2, player2);
        connectedPlayers.put(3, player3);
        game.setDrawer(2);
        game.setGamePhase("choosing");
        game.setConnectedPlayers(connectedPlayers);
        game.setGameStarted(true);
        GameRepository.addGame(gameId, game);

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);
        Thread.sleep(1000);
        webSocketController.reconnect(1);
        Thread.sleep(1000);
        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(false);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        players.put(2, player2);
        players.put(3, player3);
        gameStateDTO.setConnectedPlayers(players);
        gameStateDTO.setPlayersOriginally(0);
        gameStateDTO.setCurrentRound(0);
        gameStateDTO.setCurrentTurn(0);
        ArrayList<String> threeWords = new ArrayList<>();
        gameStateDTO.setThreeWords(threeWords);
        gameStateDTO.setDrawer(2);
        ArrayList<Integer> newdrawingOrderLeavers = new ArrayList<>();
        newdrawingOrderLeavers.add(1);
        newdrawingOrderLeavers.add(2);
        newdrawingOrderLeavers.add(3);

        gameStateDTO.setDrawingOrder(newdrawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(5);
        gameStateDTO.setGamePhase("choosing");
        gameStateDTO.setActualCurrentWord(null);

        assertThat(resultKeeper.get(4, SECONDS)).isEqualToComparingFieldByFieldRecursively(gameStateDTO);

    }
    */
    /*
    @Test
    public void wsdisconnecteventlistenerTest() throws Exception {
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();

        int gameId = 101;

        stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        when(randomGenerators.PasswordGenerator()).thenReturn("password");
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        Player player = new Player("Markiian", 1, false, 101, friends, "admin");

        //first game
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        PlayerRepository.addPlayer(1, gameId, player);
        ArrayList<Integer> drawingOrderLeavers = new ArrayList<>();
        drawingOrderLeavers.add(1);
        game.setDrawingOrderLeavers(drawingOrderLeavers);
        HashMap<Integer, Player> connectedPlayers = new HashMap<>();
        connectedPlayers.put(1, player);
        game.setDrawer(2);
        game.setGamePhase("choosing");
        game.setConnectedPlayers(connectedPlayers);
        game.setGameStarted(false);
        GameRepository.addGame(gameId, game);

        ArrayList<String> words = new ArrayList<>();

        game.setWordList(words);
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        HashMap<String, Object> sessionAttributes = new HashMap<>();
        headerAccessor.setSessionAttributes(sessionAttributes);
        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setUserId(1);
        sessionAttributeDTO.setReload(null);
        webSocketController.senduserIdAttr(sessionAttributeDTO,  headerAccessor);

        Thread.sleep(1000);
        stompSession.disconnect();
        Thread.sleep(1000);
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("deletegame");


        assertThat(resultKeeper.get(4, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);

    }
    */

}
