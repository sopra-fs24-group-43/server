package ch.uzh.ifi.hase.soprafs24.controller.wscontroller;

import ch.uzh.ifi.hase.soprafs24.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(SpringExtension.class)
public class WebSocketControllerTest {
    //@Value("${local.server.port}")
    private int port;

    private WebSocketController webSocketController;
    @MockBean
    private PlayerRepository playerRepository;
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
}
