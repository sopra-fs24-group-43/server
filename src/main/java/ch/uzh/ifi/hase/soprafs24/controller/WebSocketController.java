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
import org.springframework.messaging.handler.annotation.DestinationVariable;

@Controller
public class WebSocketController {
    WebSocketService webSocketService;

    public WebSocketController( WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }


    @MessageMapping("/lobby/getalllobbies")
    public void getalllobbies(){
        //this.webSocketService.sendMessageToClients("/lobby", games);

    }
    @MessageMapping("/game/{gameId}/joingame")
    public void joingame(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/general", questionToSend);

    }
    @MessageMapping("/game/{gameId}/leavegame")
    public void leavegame(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/general", questionToSend);

    }
    @MessageMapping("/game/{gameId}/updategamesettings")
    public void updategamesettings(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/general", gamesettingsDTO);

    }

    @MessageMapping("/game/{gameId}/startgame")
    public void startgame(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/general", questionToSend);

    }

    @MessageMapping("/game/{gameId}/nextturn")
    public void nextturn(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/general", questionToSend);

    }

    @MessageMapping("/game/{gameId}/sendguess")
    public void sendguess(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/general", answer);

    }

    @MessageMapping("/game/{gameId}/endturn")
    public void endturn(@DestinationVariable int gameId){
        //Game game = GameRepository.findByLobbyId((int) gameId);
        //List<Player> players = PlayerRepository.findUserByLobbyId(gameId);
        //RoundDTO round = roundService.endRound(players)
        //LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();

        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/general", leaderboardDTO);
    }

    @MessageMapping("/game/{gameId}/endgame")
    public void endgame(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/general", questionToSend);

    }

    @MessageMapping("/game/{gameId}/coordinates")
    public void coordinates(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/games/" + gameId + "/coordinates", coordinates);

    }

}
