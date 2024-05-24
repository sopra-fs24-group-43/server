package ch.uzh.ifi.hase.soprafs24.controller.wscontroller;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.*;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    public static class MyStompFrameHandlerQuestionToSend implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerQuestionToSend(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return QuestionToSend.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            QuestionToSend obj = (QuestionToSend) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }

    public static class MyStompFrameHandlerReconnectionDTO implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerReconnectionDTO(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ReconnectionDTO.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            ReconnectionDTO obj = (ReconnectionDTO) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }
    public static class MyStompFrameHandlerGamesDTO implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerGamesDTO(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return GamesDTO.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            GamesDTO obj = (GamesDTO) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }

    public static class MyStompFrameHandlerInboundPlayer implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerInboundPlayer(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return InboundPlayer.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            InboundPlayer obj = (InboundPlayer) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }

    public static class MyStompFrameHandlerGameSettingsDTO implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerGameSettingsDTO(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return GameSettingsDTO.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            GameSettingsDTO obj = (GameSettingsDTO) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }

    public static class MyStompFrameHandlerLobbyInfo implements StompFrameHandler {

        private final Consumer<Object> frameHandler;


        public MyStompFrameHandlerLobbyInfo(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return LobbyInfo.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            LobbyInfo obj = (LobbyInfo) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }
    public static class MyStompFrameHandlerGameStateDTO implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerGameStateDTO(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return GameStateDTO.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            GameStateDTO obj = (GameStateDTO) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }

    public static class MyStompFrameHandlerChooseWordDTO implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerChooseWordDTO(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ChooseWordDTO.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            ChooseWordDTO obj = (ChooseWordDTO) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }
    public static class MyStompFrameHandlerAnswer implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerAnswer(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Answer.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            Answer obj = (Answer) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }

    public static class MyStompFrameHandlerFillToolCoordinates implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerFillToolCoordinates(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return FillToolCoordinates.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            FillToolCoordinates obj = (FillToolCoordinates) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }
    public static class MyStompFrameHandlerDrawCoordinates implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerDrawCoordinates(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return DrawCoordinates.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            DrawCoordinates obj = (DrawCoordinates) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }
    public static class MyStompFrameHandlerEraserCoordinates implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerEraserCoordinates(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return EraserCoordinates.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            EraserCoordinates obj = (EraserCoordinates) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }
    public static class MyStompFrameHandlerEraseAllCoordinates implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerEraseAllCoordinates(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return EraseAllCoordinates.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            EraseAllCoordinates obj = (EraseAllCoordinates) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }
    public static class MyStompFrameHandlerFillCoordinates implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerFillCoordinates(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return FillCoordinates.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            FillCoordinates obj = (FillCoordinates) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }
    public static class MyStompFrameHandlerCoordinates implements StompFrameHandler {

        private final Consumer<Object> frameHandler;

        public MyStompFrameHandlerCoordinates(Consumer<Object> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Coordinates.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            Coordinates obj = (Coordinates) payload;
            log.info("received message: {} with headers: {}", obj, headers);
            frameHandler.accept(payload);
        }
    }

}
