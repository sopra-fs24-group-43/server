package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Settings;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.*;
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
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Coordinates;


@Controller
public class WebSocketController {
    WebSocketService webSocketService;
    RandomGenerators randomGenerators;
    public WebSocketController( WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
        this.randomGenerators = new RandomGenerators();
    }

    @MessageMapping("/landing/creategame")
    public void creategame(InboundPlayer inboundPlayer){ //the client can't know the gameId of the game when he first creates it so he can just pass some int (e.g. 1001)
        Player player = new Player(inboundPlayer.getUsername(),
                inboundPlayer.getUserId(), inboundPlayer.getGameId(),
                inboundPlayer.getFriends(), inboundPlayer.getRole());
        Game game = new Game(player);
        int gameId = randomGenerators.GameIdGenerator();
        while (GameRepository.gameIdtaken(gameId)) {
            gameId = randomGenerators.GameIdGenerator();
        }
        player.setGameId(gameId);
        int playerId = inboundPlayer.getUserId();
        PlayerRepository.addPlayer(playerId, gameId, player);
        game.setGameId(gameId);
        GameRepository.addGame(gameId,game);
        QuestionToSend questionToSend = new QuestionToSend("creategame");
        questionToSend.setGameId(gameId);
        questionToSend.setUserId(player.getUserId());
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the creator of the game
        //and for the Landingpage to update List of Lobbies, will trigger a getallgames
    }
    @MessageMapping("/games/{gameId}/deletegame")
    public void deletegame(int gameId){
        GameRepository.removeGame(gameId);
        HashMap<Integer, Player> players = PlayerRepository.findUsersByGameId(gameId); //<gameId, Player>
        players.forEach((key, value) -> {
            PlayerRepository.removePlayer(value.getUserId(), key);
        });
        PlayerRepository.removeGameId(gameId);

        QuestionToSend questionToSend = new QuestionToSend("deletegame");
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);  //for the players in the Lobby
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames
    }
    @MessageMapping("/landing/{userId}/getallgames")
    public void getalllobbies(@DestinationVariable int userId){

        HashMap<Integer,Game> games = GameRepository.getallGames();
        GamesDTO gamesDTO = new GamesDTO();
        List<Integer> listGameIds = new ArrayList<>();
        List<String> listLobbyName = new ArrayList<>();

        List<List<Player>> listPlayers = new ArrayList<>();
        List<Integer> listMaxPlayers = new ArrayList<>();
        List<String> listGamePassword = new ArrayList<>();
        games.forEach((number, game) -> {
            if (!game.getGameStarted()) {  //it shouldnt return games that are already playing
                listGameIds.add(game.getGameId());
                listLobbyName.add(game.getLobbyName());
                List<Player> players = new ArrayList<>();
                game.getPlayers().forEach((nr, player) -> {
                    players.add(player);
                });
                listPlayers.add(players);
                listMaxPlayers.add(game.getMaxPlayers());
                listGamePassword.add(game.getGamePassword());
            }
        });
        gamesDTO.setType("gamesDTO");
        gamesDTO.setGameId(listGameIds);
        gamesDTO.setLobbyName(listLobbyName);
        gamesDTO.setPlayers(listPlayers);
        gamesDTO.setMaxPlayers(listMaxPlayers);
        gamesDTO.setGamePassword(listGamePassword);



        this.webSocketService.sendMessageToClients("/topic/landing/" + userId, gamesDTO);
    }

    @MessageMapping("/games/{gameId}/joingame")
    public void joingame(@DestinationVariable int gameId, InboundPlayer inboundPlayer){

        Player player = new Player(inboundPlayer.getUsername(),
                inboundPlayer.getUserId(), inboundPlayer.getGameId(),
                inboundPlayer.getFriends(), inboundPlayer.getRole());
        Game game = GameRepository.findByGameId(gameId);
        game.addPlayer(player);

        int playerId = inboundPlayer.getUserId();
        PlayerRepository.addPlayer(playerId, gameId, player);
        QuestionToSend questionToSend = new QuestionToSend("joingame");
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", inboundPlayer); //should return something joiner doesnt need to receive it
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames
    }

    @MessageMapping("/games/{gameId}/leavegame")
    public void leavegame(@DestinationVariable int gameId, InboundPlayer inboundPlayer){ //needs change = can admin leavegame or only deletegame?
        Game game = GameRepository.findByGameId(gameId);
        game.removePlayer(inboundPlayer.getUserId());
        Player player = PlayerRepository.findByUserId(inboundPlayer.getUserId());
        boolean wasAdmin = (player.getRole() == "admin");
        int currentPlayerCount = game.getPlayers().size();
        PlayerRepository.removePlayer(player.getUserId(), gameId);

        QuestionToSend questionToSend = new QuestionToSend("leavegame");
        questionToSend.setLeaver(player);
        questionToSend.setWasAdmin(wasAdmin);           //what should happen if player was the admin? (delete game or give admin to other player?)
        questionToSend.setCurrentPlayerCount(currentPlayerCount);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames

/*
        GameStateDTO gameStateDTO = game.gameStateDTO();
        PlayerRepository.removePlayer(inboundPlayer.getUserId(),gameId);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
*/

    }
    @MessageMapping("/games/{gameId}/updategamesettings")
    public void updategamesettings(@DestinationVariable int gameId, GameSettingsDTO gameSettingsDTO){
        Game game = GameRepository.findByGameId(gameId);
        game.updateGameSettings(gameSettingsDTO);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameSettingsDTO);

    }
    @MessageMapping("/games/{gameId}/getlobbyinfo")
    public void getlobbyinfo(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);
        LobbyInfo lobbyInfo = new LobbyInfo();
        lobbyInfo.setGameId(gameId);
        lobbyInfo.setPlayers(game.getPlayers());
        lobbyInfo.setGameSettingsDTO(game.getGameSettingsDTO());
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", lobbyInfo);
    }

    @MessageMapping("/games/{gameId}/startgame")
    public void startgame(@DestinationVariable int gameId, GameSettingsDTO gameSettingsDTO){
        Game game = GameRepository.findByGameId(gameId);
        game.updateGameSettings(gameSettingsDTO);
        game.startGame();
        GameStateDTO gameStateDTO = game.gameStateDTO();
        QuestionToSend questionToSend = new QuestionToSend("startgame"); //this is solely for the Table to take the game off the List of lobbies
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames

    }

    @MessageMapping("/games/{gameId}/nextturn")
    public void nextturn(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);
        if (game.getCurrentRound()==game.getMaxRounds()&&game.getCurrentTurn()==5) {
            game.setEndGame(true);
        }
        else if (game.getCurrentTurn()==5) { //shouldnt be == 5 but == game.getconnectedPlayers.size()?
            game.setCurrentRound(game.getCurrentRound()+1);
            game.setCurrentTurn(1);
        } else {
            game.setCurrentTurn(game.getCurrentTurn()+1);
        }
        GameStateDTO gameStateDTO = game.gameStateDTO();
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
    }

    @MessageMapping("/games/{gameId}/sendguess")
    public void sendguess(@DestinationVariable int gameId, Answer answer){
        Game game = GameRepository.findByGameId(gameId);
        game.addAnswer(answer);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", answer);

    }

    @MessageMapping("/games/{gameId}/endturn")//how to connect endturn and nextturn...
    public void endturn(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);
        HashMap<Integer, Player> players = PlayerRepository.findUsersByGameId(gameId);

        LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();

        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);
    }

/*
        @MessageMapping("/games/{gameId}/endgame")//needed?
        public void endgame(@DestinationVariable int gameId){
            GameRepository.removeGame(gameId);
            //send players back to homepage in frontend?
        }
        */
    /*//old
    @MessageMapping("game/{gameId}/postgame")
    @SendTo("game/{gameId}/general")
    public void postgame(@DestinationVariable long gameId) {
        Game game = GameRepository.findByGameId((int) gameId);
        LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();

        this.webSocketService.sendMessageToClients("game/{gameId}", leaderboardDTO);
    }
    */
    @MessageMapping("/games/{gameId}/coordinates") //also change the MessageMapping and channel to sendCanvas
    public void sendCanvas(@DestinationVariable int gameId, Coordinates coordinates){
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/coordinates", coordinates);
    }
}
