package ch.uzh.ifi.hase.soprafs24.controller.wscontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class WsTestUtils {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    WebSocketStompClient createWebSocketClient() {
        /*
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());*/

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }

    static class MyStompSessionHandler extends StompSessionHandlerAdapter {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            log.info("Stomp client is connected");
            super.afterConnected(session, connectedHeaders);
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            log.info("Exception: " + exception);
            super.handleException(session, command, headers, payload, exception);
        }
    }
}
