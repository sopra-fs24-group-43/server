package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Settings;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.GameStateDTO;
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

        //this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);//-->maybe refresh "getallgames" instead?
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);//or automatically change view back to homepage in frontend?
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
        GameStateDTO gameStateDTO = game.gameStateDTO();
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);

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
        game.setCurrentTurn(1);
        game.setCurrentRound(1);
        GameStateDTO gameStateDTO = game.gameStateDTO();
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);

    }

    @MessageMapping("/games/{gameId}/nextturn")
    public void nextturn(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);
        if (game.getCurrentRound()==game.getMaxRounds()&&game.getCurrentTurn()==5) {
                game.setEndGame(true);
            }
        else if (game.getCurrentTurn()==5) {
            game.setCurrentRound(game.getCurrentRound()+1);
            game.setCurrentTurn(1);
        } else {
            game.setCurrentTurn(game.getCurrentTurn()+1);
        }
        GameStateDTO gameStateDTO = game.gameStateDTO();
        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);

    }

    @MessageMapping("/games/{gameId}/sendguess")
    public void sendguess(@DestinationVariable int gameId, Answer answer){
        Game game = GameRepository.findByGameId(gameId);
        game.addAnswer(answer);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", answer);

    }

    @MessageMapping("/games/{gameId}/endturn")//how to connect endturn and nextturn...
    public void endturn(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId((int) gameId);
        ArrayList<Player> players = PlayerRepository.findUserByGameId(gameId);

        LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();

        //this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);
    }

    @MessageMapping("/games/{gameId}/endgame")//needed?
    public void endgame(@DestinationVariable int gameId){
        GameRepository.removeGame(gameId);
        //send players back to homepage in frontend?
    }
    /*//old
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


}
