package ch.uzh.ifi.hase.soprafs24.controller.wscontroller;

import ch.uzh.ifi.hase.soprafs24.controller.wscontroller.WsTestUtils;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TimerRepository;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.QuestionToSend;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TimerServiceTests {
    @Value("${local.server.port}")
    private String port;

    @Autowired
    WebSocketService webSocketService;
    @Autowired
    TimerService timerService;
    @MockBean
    private RandomGenerators randomGenerators;
    @MockBean
    private GetWordlist getWordlist;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private final WsTestUtils wsTestUtils = new WsTestUtils();

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
    void dotimerdrawingphase() throws Exception {
        int gameId = 101;

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
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        players.put(2, player2);
        game.setConnectedPlayers(players);
        game.setCurrentCorrectGuesses(2);
        Thread.sleep(1000);

        timerService.doTimer(3,1,gameId,"/topic/games/" + gameId + "/general", "drawing");
        Thread.sleep(5000);
        int tasks = TimerRepository.findTasksByGameId(gameId).size();
        assertThat(3).isEqualTo(TimerRepository.findTasksByGameId(gameId).size());
        int timeleft = game.getTimeLeftInTurn();
        assertThat(0).isEqualTo(game.getTimeLeftInTurn());
    }

    @Test
    void dotimerdrawingandendturn() throws Exception {
        int gameId = 101;

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
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        players.put(2, player2);
        game.setConnectedPlayers(players);
        game.setCurrentCorrectGuesses(1);
        Thread.sleep(1000);

        timerService.doTimer(3,1,gameId,"/topic/games/" + gameId + "/general", "drawing");
        Thread.sleep(5000);
        int tasks = TimerRepository.findTasksByGameId(gameId).size();
        assertThat(5).isEqualTo(tasks); //cause doTimer triggers another doTimer with gamephase leaderboard and length 5
        int timeleft = game.getTimeLeftInTurn();
        assertThat(0).isEqualTo(timeleft);
        assertThat(false).isEqualTo(game.getEndGame());
    }
    @Test
    void doshutdowntimerTest() throws Exception {
        int gameId = 101;

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
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1, player);
        players.put(2, player2);
        game.setConnectedPlayers(players);
        game.setCurrentCorrectGuesses(1);
        Thread.sleep(1000);

        timerService.doTimer(3,1,gameId,"/topic/games/" + gameId + "/general", "drawing");
        timerService.doShutDownTimer(gameId);
        Thread.sleep(1000);
        HashMap<Integer, ScheduledFuture> tasks = TimerRepository.findTasksByGameId(gameId);
        if (tasks == null) {
            assert(true);
        }
        else {
            assert(false);
        }
        int timeleft = game.getTimeLeftInTurn();
        assertThat(3).isEqualTo(timeleft);//first task executed
    }

    @Test
    void dotimerforreloaddiscTest() throws Exception {
        int gameId = 101;
        int userId = 1;
        String action = "inLobbyAdmin";
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();
        CompletableFuture<Object> resultKeeper2 = new CompletableFuture<>();


        stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper2.complete(payload)));

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

        timerService.doTimerForReloadDisc(gameId,userId,action);
        Thread.sleep(2000);
        HashMap<Integer, ScheduledFuture> tasks = TimerRepository.findDiscTasksByUserId(gameId);
        if (tasks == null) {
            assert(true);
        }
        else {
            assert(false);
        }
        Thread.sleep(1000);
        Game game2 = GameRepository.findByGameId(gameId);
        if (game2 == null) {
            assert(true);
        }
        else {
            assert(false);
        }
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("deletegame");
        assertThat(resultKeeper.get(4, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
        assertThat(resultKeeper2.get(4, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);  //never halt most of the time

    }

    @Test
    void dotimerforreloaddisc2Test() throws Exception {
        int gameId = 101;
        int userId = 2;
        String action = "inLobbyPlayer";  //changes
        CompletableFuture<Object> resultKeeper = new CompletableFuture<>();
        CompletableFuture<Object> resultKeeper2 = new CompletableFuture<>();


        stompSession.subscribe(
                "/topic/landing",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        stompSession.subscribe(
                "/topic/games/" + gameId + "/general",
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper2.complete(payload)));

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

        timerService.doTimerForReloadDisc(gameId,userId,action);
        Thread.sleep(2000);
        HashMap<Integer, ScheduledFuture> tasks = TimerRepository.findDiscTasksByUserId(gameId);
        if (tasks == null) {
            assert(true);
        }
        else {
            assert(false);
        }
        Game game2 = GameRepository.findByGameId(gameId);
        HashMap<Integer, Player> players = new HashMap<>();
        players.put(1,player);
        HashMap<Integer, Player> playersactual = new HashMap<>();
        playersactual = game2.getPlayers();
        assertThat(playersactual).isEqualTo(players);
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("leavegame");
        assertThat(resultKeeper.get(4, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);
        assertThat(resultKeeper2.get(4, SECONDS)).isEqualToComparingFieldByFieldRecursively(questionToSend);  //never halt most of the time

    }


}
