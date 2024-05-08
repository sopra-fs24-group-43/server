package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Settings;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.InboundPlayer;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Answer;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.GamesDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Coordinates;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.EraseAllCoordinates;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.FillCoordinates;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.EraserCoordinates;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.DrawCoordinates;

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
    @MessageMapping("/landing/creategame")
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

        this.webSocketService.sendMessageToClients("/topic/landing", inboundPlayer);

    }
    @MessageMapping("/landing/deletegame") //should be braodcasted to the players inside the game also (when setting up the settings) thats why two sendMessageToClients
    public void deletegame(int gameId){
        Game game = GameRepository.findByGameId(gameId);
        GameRepository.removeGame(gameId);

        //this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);
    }
    @MessageMapping("/landing/getallgames")
    public void getalllobbies(){

        HashMap<Integer,Game> games = GameRepository.getallGames();
        GamesDTO gamesDTO = new GamesDTO();
        List<Integer> listGameIds = new ArrayList<>();
        List<String> listLobbyName = new ArrayList<>();
        List<List<Player>> listPlayers = new ArrayList<>();
        List<Integer> listMaxPlayers = new ArrayList<>();
        List<String> listGamePassword = new ArrayList<>();
        games.forEach((number, game) -> {
            listGameIds.add(game.getGameId());
            listLobbyName.add(game.getLobbyName());
            List<Player> players = new ArrayList<>();
            game.getPlayers().forEach((nr, player) -> {
                players.add(player);
            });
            listPlayers.add(players);
            listMaxPlayers.add(game.getMaxPlayers());
            listGamePassword.add(game.getGamePassword());
        });
        gamesDTO.setType("gamesDTO");
        gamesDTO.setGameId(listGameIds);
        gamesDTO.setLobbyName(listLobbyName);
        gamesDTO.setPlayers(listPlayers);
        gamesDTO.setMaxPlayers(listMaxPlayers);
        gamesDTO.setGamePassword(listGamePassword);



        this.webSocketService.sendMessageToClients("/topic/landing", gamesDTO);

    }

    @MessageMapping("/games/{gameId}/joingame")
    public void joingame(@DestinationVariable int gameId, InboundPlayer inboundPlayer){
        Player player = new Player(inboundPlayer.getUsername(),
                inboundPlayer.getUserId(), gameId,
                inboundPlayer.getFriends(), inboundPlayer.getRole());
        Game game = GameRepository.findByGameId(gameId);
        game.addPlayer(player);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", inboundPlayer);

    }
    @MessageMapping("/games/{gameId}/leavegame")
    public void leavegame(@DestinationVariable int gameId, InboundPlayer inboundPlayer){
        Game game = GameRepository.findByGameId(gameId);
        game.removePlayer(inboundPlayer.getUserId());
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);

    }
    @MessageMapping("/games/{gameId}/updategamesettings")
    public void updategamesettings(@DestinationVariable int gameId, GameSettingsDTO gameSettingsDTO){
        Game game = GameRepository.findByGameId(gameId);
        game.updateGameSettings(gameSettingsDTO);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameSettingsDTO);

    }

    @MessageMapping("/games/{gameId}/startgame")
    public void startgame(@DestinationVariable int gameId, GameSettingsDTO gameSettingsDTO){
        Game game = GameRepository.findByGameId(gameId);
        game.updateGameSettings(gameSettingsDTO);
        game.startGame();
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);

    }

    @MessageMapping("/games/{gameId}/nextturn")
    public void nextturn(@DestinationVariable int gameId){
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);

    }

    @MessageMapping("/games/{gameId}/sendguess")
    public void sendguess(@DestinationVariable int gameId, Answer answer){
        Game game = GameRepository.findByGameId(gameId);
        game.addAnswer(answer);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", answer);

    }

    @MessageMapping("/games/{gameId}/endturn")
    public void endturn(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId((int) gameId);
        ArrayList<Player> players = PlayerRepository.findUserByGameId(gameId);

        //LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();

        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);
    }

    @MessageMapping("/games/{gameId}/endgame")
    public void endgame(@DestinationVariable int gameId){
        GameRepository.removeGame((int) gameId);
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);

    }
    /*
    @MessageMapping("game/{gameId}/postgame")
    @SendTo("game/{gameId}/general")
    public void postgame(@DestinationVariable long gameId) {
        Game game = GameRepository.findByGameId((int) gameId);
        LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();

        this.webSocketService.sendMessageToClients("game/{gameId}", leaderboardDTO);
    }
    */
    @MessageMapping("/games/{gameId}/coordinates")
    public void sendCanvas(@DestinationVariable int gameId, Coordinates coordinates){
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/coordinates", coordinates);
    }

    @MessageMapping("/games/{gameId}/fill")
    public void fillCanvas(@DestinationVariable int gameId, FillCoordinates fillCoordinates) {
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/fill", fillCoordinates);
    }

    @MessageMapping("/games/{gameId}/eraseAll")
    public void fillCanvas(@DestinationVariable int gameId, EraseAllCoordinates eraseAllCoordinates) {
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/eraseAll", eraseAllCoordinates);
    }

    @MessageMapping("/games/{gameId}/eraser")
    public void fillCanvas(@DestinationVariable int gameId, EraserCoordinates eraserCoordinates) {
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/eraser", eraserCoordinates);
    }

    @MessageMapping("/games/{gameId}/draw")
    public void fillCanvas(@DestinationVariable int gameId, DrawCoordinates drawCoordinates) {
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/draw", drawCoordinates);
    }
}
