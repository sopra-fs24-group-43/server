package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Settings;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.HashMap;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;

@Controller
public class WebSocketController {
    WebSocketService webSocketService;

    public WebSocketController( WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
    @MessageMapping("/message") // /app/message
    @SendTo("/settings")
    public Settings createGame(@Payload Settings message){
        Game game = new Game(message.getTotalRounds(),message.getTotalPlayers(),message.getRoundLength());
        GameRepository.addGame(1,game);
        return message;
    }

    @MessageMapping("/message/{settingsid}") // /app/message
    @SendTo("settings")
    public void lookatGame(){
        Game game = GameRepository.findByLobbyId(1);
        HashMap<String, String> payload = new HashMap<String, String>();
        payload.put("totalRounds",game.totalRounds);
        payload.put("totalPlayers",game.totalPlayers);
        payload.put("roundLength",game.roundLength);

        this.webSocketService.sendMessageToClients("/settings",payload);

    }
}
