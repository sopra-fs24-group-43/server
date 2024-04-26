package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Coordinates;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Lobby;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/game/coordinates") // /app/game/{id}/coordinates"
    @SendTo("/topic/coordinates")
    public Coordinates displayCoordinates(@Payload Coordinates coordinates){
        return coordinates;
    }

    @MessageMapping("/game/lobby") // /app/game/{id}/lobby"
    @SendTo("/topic/lobby")
    public Lobby displayLobby(@Payload Lobby lobby){
        return lobby;
    }


}
