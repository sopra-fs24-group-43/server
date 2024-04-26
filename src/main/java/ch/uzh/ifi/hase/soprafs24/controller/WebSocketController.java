package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Settings;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.InboundPlayer;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
@Controller
public class WebSocketController {
    WebSocketService webSocketService;
    RandomGenerators randomGenerators;
    public WebSocketController( WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
        this.randomGenerators = new RandomGenerators();
    }
    /*
    @MessageMapping("/message") // /app/message
    @SendTo("/topic/lobby") //enablesimpleBroker
    public Settings createGame(@Payload Settings message){
        Game game = new Game(message.getTotalRounds(),message.getTotalPlayers());
        GameRepository.addGame(1,game);
        return message;
    }

    @MessageMapping("/message/{settingsid}") // /app/message
    @SendTo("/topic/games/1")
    public void lookatGame(){
        //Game game = GameRepository.findByGameId(1);

        HashMap<String, String> payload = new HashMap<String, String>();
        payload.put("totalRounds",game.totalRounds);
        payload.put("totalPlayers",game.totalPlayers);
        payload.put("roundLength",game.roundLength);

        this.webSocketService.sendMessageToClients("/topic/games/1","payload");

    }
    @MessageMapping("/game/{gameid}/endgame")
    @SendTo("topic")
    public void updatesettings(){
        Game game = GameRepository.findByGameId(1);
        //HashMap<String, String> payload = new HashMap<String, String>();
        //payload.put("totalRounds",game.totalRounds);
        //payload.put("totalPlayers",game.totalPlayers);
        //payload.put("roundLength",game.roundLength);

        this.webSocketService.sendMessageToClients("/topic","payload");

    }
    */
    @MessageMapping("/lobby/creategame")
    public void creategame(InboundPlayer inboundPlayer){

        Player player = new Player(inboundPlayer.getUsername(),
                inboundPlayer.getUserId(), inboundPlayer.getGameId(),
                inboundPlayer.getFriends(), inboundPlayer.getRole());
        Game game = new Game(player);
        int gameId = 1;
        while (GameRepository.gameIdtaken(gameId)) {
            gameId = randomGenerators.GameIdGenerator();
        }
        player.setGameId(gameId);
        game.setGameId(gameId);
        GameRepository.addGame(gameId,game);

        this.webSocketService.sendMessageToClients("/topic/lobby", player);

    }
    @MessageMapping("/lobby/deletegame") //should be braodcasted to the players inside the game also (when setting up the settings) thats why two sendMessageToClients
    public void deletegame(int gameId){
        Game game = GameRepository.findByGameId(gameId);
        GameRepository.removeGame(gameId);

        //this.webSocketService.sendMessageToClients("/topic/lobby", questionToSend);
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);
    }
    @MessageMapping("/lobby/getallgames")
    public void getalllobbies(){
        HashMap games = GameRepository.getallGames();

        this.webSocketService.sendMessageToClients("/topic/lobby", games);

    }
    @MessageMapping("/games/{gameId}/joingame")
    public void joingame(@DestinationVariable int gameId, InboundPlayer inboundPlayer){
        Player player = new Player(inboundPlayer.getUsername(),
                inboundPlayer.getUserId(), gameId,
                inboundPlayer.getFriends(), inboundPlayer.getRole());
        Game game = GameRepository.findByGameId(gameId);
        game.addPlayer(player);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", player);

    }
    @MessageMapping("/games/{gameId}/leavegame")
    public void leavegame(@DestinationVariable int gameId, InboundPlayer inboundPlayer){
        Game game = GameRepository.findByGameId(gameId);
        game.removePlayer(inboundPlayer.getUserId());
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);

    }
    @MessageMapping("/games/{gameId}/updategamesettings")
    public void updategamesettings(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gamesettingsDTO);

    }

    @MessageMapping("/games/{gameId}/startgame")
    public void startgame(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);

    }

    @MessageMapping("/games/{gameId}/nextturn")
    public void nextturn(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);

    }

    @MessageMapping("/games/{gameId}/sendguess")
    public void sendguess(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", answer);

    }

    @MessageMapping("/games/{gameId}/endturn")
    public void endturn(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId((int) gameId);
        ArrayList<Player> players = PlayerRepository.findUserByGameId(gameId);
        //RoundDTO round = roundService.endRound(players)
        //LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();

        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);
    }

    @MessageMapping("/games/{gameId}/endgame")
    public void endgame(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);

    }

    @MessageMapping("/games/{gameId}/coordinates")
    public void coordinates(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/coordinates", coordinates);

    }

}
