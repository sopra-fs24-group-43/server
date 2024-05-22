package ch.uzh.ifi.hase.soprafs24.controller.wscontroller;

import ch.uzh.ifi.hase.soprafs24.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.InboundPlayer;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.QuestionToSend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.doNothing;

@ExtendWith(SpringExtension.class)
public class WebSocketControllerTest {/*
    //@Value("${local.server.port}")
    private int port;
    @MockBean
    private WebSocketController webSocketController;
    @MockBean
    private PlayerRepository playerRepository;
    @MockBean
    private Game game;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private final WsTestUtils wsTestUtils = new WsTestUtils();

    @BeforeEach
    public void setUp() throws Exception {
        port = 8080;
        System.out.println(port);
        String wsUrl = "ws://localhost:" + port + "/ws";
        stompClient = wsTestUtils.createWebSocketClient();
        stompSession = stompClient.connect(wsUrl, new WsTestUtils.MyStompSessionHandler()).get();
    }

    @Test
    void connectsToSocket() throws Exception {

        assertThat(stompSession.isConnected()).isTrue();
    }

    @Test
    public void creategameTest() throws Exception {
        CompletableFuture<String> resultKeeper = new CompletableFuture<>();
        int gameId = 101;
        stompSession.subscribe(
                "/topic/lobbies/" + gameId,
                new WsTestUtils.MyStompFrameHandlerQuestionToSend((payload) -> resultKeeper.complete(payload)));

        Thread.sleep(1000);


        InboundPlayer inboundPlayer = new InboundPlayer();
        inboundPlayer.setType("InboundPlayer");
        inboundPlayer.setUsername("Markiian");
        inboundPlayer.setuserId(1);
        inboundPlayer.setGameId(1);
        ArrayList<Integer> friends = new ArrayList<>();
        friends.add(2);
        inboundPlayer.setFriends(friends);
        inboundPlayer.setRole("admin");

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setLobbyName("1");
        gameSettingsDTO.setMaxPlayers(2);
        gameSettingsDTO.setTurnLength(5);
        gameSettingsDTO.setMaxRounds(1);

        doNothing().when(game).updateGameSettings(gameSettingsDTO);

        Thread.sleep(1000);
        // when
        webSocketController.creategame(inboundPlayer);

        // then
        QuestionToSend questionToSend = new QuestionToSend("creategame");

        assertThat(resultKeeper.get(2, SECONDS)).isEqualTo(questionToSend.toString());
    }
*/

}
