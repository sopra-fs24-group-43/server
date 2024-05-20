package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.TimerRepository;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.utils.ReconnectionHelper;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.*;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;


@Controller
public class WebSocketController {
    WebSocketService webSocketService;
    RandomGenerators randomGenerators;
    TimerService timerService;
    ReconnectionHelper reconnectionHelper;

    public WebSocketController( WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
        this.randomGenerators = new RandomGenerators();
        this.timerService = new TimerService(webSocketService);
        this.reconnectionHelper = new ReconnectionHelper(webSocketService);
    }

    @MessageMapping("/landing/createguestplayer")
    public void createguestplayer() { //how does client know its his response and not for some other client?
        //returns inboundPlayer with type = createguestplayer and with some fields = null
        InboundPlayer inboundPlayer = PlayerRepository.createPlayerFromGuest();
        this.webSocketService.sendMessageToClients("/topic/landing", inboundPlayer);
    }
    @MessageMapping("/landing/creategame")
    public void creategame(InboundPlayer inboundPlayer){ //the client can't know the gameId of the game when he first creates it so he can just pass some int (e.g. 1001)
        Player player = new Player(inboundPlayer.getUsername(),
                inboundPlayer.getUserId(), inboundPlayer.getIsGuest(),inboundPlayer.getGameId(),
                inboundPlayer.getFriends(), inboundPlayer.getRole());
        Game game = new Game(player, webSocketService, timerService);
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
        //new below
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the creator of the game
        //and for the Landingpage to update List of Lobbies, will trigger a getallgames
    }
    @MessageMapping("/games/{gameId}/deletegame")
    public void deletegame(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);
        game.deletegame(gameId);
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
    public void joingame(@DestinationVariable int gameId, InboundPlayer inboundPlayer  ){

        Player player = new Player(inboundPlayer.getUsername(),
                inboundPlayer.getUserId(), inboundPlayer.getIsGuest(),
                inboundPlayer.getGameId(), inboundPlayer.getFriends(),
                inboundPlayer.getRole());
        Game game = GameRepository.findByGameId(gameId);
        game.addPlayer(player);

        int playerId = inboundPlayer.getUserId();
        PlayerRepository.addPlayer(playerId, gameId, player);
        QuestionToSend questionToSend = new QuestionToSend("joingame");
        //new beloew

        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", inboundPlayer); //should return something joiner doesnt need to receive it
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames
    }

    @MessageMapping("/games/{gameId}/leavegame/{playerId}")
    public void leavegame(@DestinationVariable int gameId, @DestinationVariable int playerId){ //needs change = can admin leavegame or only deletegame?
        Game game = GameRepository.findByGameId(gameId);
        game.leavegame(playerId, gameId);
/*
        GameStateDTO gameStateDTO = game.receiveGameStateDTO();
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
        lobbyInfo.setType("getlobbyinfo");
        lobbyInfo.setGameId(gameId);
        lobbyInfo.setPlayers(game.getPlayers());
        lobbyInfo.setGameSettingsDTO(game.getGameSettingsDTO());
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", lobbyInfo);
    }

    @MessageMapping("/games/{gameId}/startgame")
    public void startgame(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);
        game.startGame();
        GameStateDTO gameStateDTO = game.receiveGameStateDTO();
        QuestionToSend questionToSend = new QuestionToSend("startgame"); //this is solely for the Table to take the game off the List of lobbies
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames
    }

    @MessageMapping("/games/{gameId}/getgamestate/{userId}")
    public void getgamestate(@DestinationVariable int gameId, @DestinationVariable int userId){
        Game game = GameRepository.findByGameId(gameId);
        GameStateDTO gameStateDTO = game.receiveGameStateDTO();
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general/" + userId, gameStateDTO);
    }
    @MessageMapping("/games/sendreload")
    public void sendreloadAttr(SessionAttributeDTO sessionAttributeDTO, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("from sendreload: "+sessionAttributeDTO.getReload());
        headerAccessor.getSessionAttributes().put("reload", sessionAttributeDTO.getReload());
    }
    @MessageMapping("/games/senduserId")
    public void senduserIdAttr(SessionAttributeDTO sessionAttributeDTO, SimpMessageHeaderAccessor headerAccessor){
        System.out.println("from senduserId: "+sessionAttributeDTO.getUserId());
        headerAccessor.getSessionAttributes().put("userId", sessionAttributeDTO.getUserId());
    }
    @MessageMapping("/landing/alertreconnect/{userId}")
    public void alertreconnect(@DestinationVariable int userId, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("from alertreconnect: "+userId);
        HashMap<Integer, ScheduledFuture> task = TimerRepository.findDiscTasksByUserId(userId);
        TimerRepository.printDiscTimers();
        timerService.stopDiscTimer(userId);                 //place this correctly
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("reload", false);
        if (task != null) {
            if (!task.get(2).isDone()) {
                System.out.println("task existed and wasnt done yet"); //add call for canvas here with if gameId
                this.reconnectionHelper.reconnectionhelp(userId, true);
            }
            else {
                System.out.println("task existed and was done");
                this.reconnectionHelper.reconnectionhelp(userId, false);

            }
        }
        else {
            System.out.println("task did not exist");
            this.reconnectionHelper.reconnectionhelp(userId, false);
        }
    }
    @MessageMapping("/landing/reconnect/{userId}")
    public void reconnect(@DestinationVariable int userId) {
        System.out.println("from reconnect: "+userId);
        System.out.println(PlayerRepository.findByUserId(userId));

        if (PlayerRepository.findByUserId(userId) != null) {
            Player player = PlayerRepository.findByUserId(userId);
            int gameId = player.getGameId();
            Game game = GameRepository.findByGameId(gameId);
            System.out.println(game);
            if (game == null) {
                return;
            }
            System.out.println(game.getPlayers().containsKey(userId));
            System.out.println(game.getGameStarted());
            if (game.getPlayers().containsKey(userId) && game.getGameStarted()) {
                game.intigrateIntoGame(userId, gameId);
                QuestionToSend questionToSend = new QuestionToSend("sendcanvasforrecon");
                questionToSend.setUserId(userId);
                this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);
            }
        }
        System.out.println("from reconnect end: " +userId);
    }

    @MessageMapping("/games/{gameId}/sendcanvasforrecon/{userId}")
    public void sendcanvasforrecon(@DestinationVariable int gameId, @DestinationVariable int userId, FillCoordinates fillCoordinates) {
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/fill/" + userId, fillCoordinates);  //has userId in mapping!
    }


    @MessageMapping("/games/{gameId}/nextturn")
    public void nextturn(@DestinationVariable int gameId){
        //System.out.println("nextturn in ws started");
        Game game = GameRepository.findByGameId(gameId);
        game.nextturn(gameId);
        timerService.doTimer(15,1, gameId, "/topic/games/" + gameId + "/general", "choosing"); //Timer to choose word
        GameStateDTO gameStateDTO = game.receiveGameStateDTO();
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
        //System.out.println("nextturn in ws ended");

    }

    @MessageMapping("/games/{gameId}/sendchosenword")
    public void sendchosenword(@DestinationVariable int gameId, ChooseWordDTO chooseWordDTO) {
        timerService.doShutDownTimer(gameId); //shutsdown timer from nextturn "choosing"
        Game game = GameRepository.findByGameId(gameId);
        game.setActualCurrentWord(chooseWordDTO.getWord());
        game.setGamePhase("drawing");
        chooseWordDTO.setType("startdrawing");
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", chooseWordDTO);
        System.out.println("sendchosenwordtimer");

        timerService.doTimer(game.getTurnLength(),1, gameId, "/topic/games/" + gameId + "/general", "drawing"); //Timer to play turn (drawing & guessing)
    }
    @MessageMapping("/games/{gameId}/sendguess")
    public void sendguess(@DestinationVariable int gameId, Answer answer){
        Game game = GameRepository.findByGameId(gameId);
        int flag = game.addAnswer(answer);
        if (flag == 2){
            answer.setPlayerHasGuessedCorrectly(true);
            answer.setIsCorrect(false);
        } else if (flag == 1){
            answer.setPlayerHasGuessedCorrectly(false);
            answer.setIsCorrect(true);
        } else {
            answer.setPlayerHasGuessedCorrectly(false);
            answer.setIsCorrect(false);
        }
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", answer);

        if (game.getCurrentCorrectGuesses() >= game.getPlayers().size() && game.getTimeLeftInTurn() >= 1){
            timerService.doShutDownTimer(game.getGameId());  //shutsdown timer from sendchosenword "drawing"
            System.out.println("endturn");
            if (game.getCurrentRound()==game.getMaxRounds() && game.getCurrentTurn()== game.getPlayersOriginally()) {
                game.setEndGame(true);
            }
            LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
            game.setGamePhase("leaderboard");
            if (game.getEndGame()){
                leaderboardDTO.setReason("normal");
            }
            this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);
            timerService.doTimer(5,1, gameId, "/topic/games/" + gameId + "/general", "leaderboard"); //timer to look at leaderboard

        }
    }

    @MessageMapping("/games/{gameId}/endturn")//how to connect endturn and nextturn...
    public void endturn(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);
        HashMap<Integer, Player> players = PlayerRepository.findUsersByGameId(gameId);

        LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
        leaderboardDTO.setType("leaderboard");
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);
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
    @MessageMapping("/games/{gameId}/coordinates") //also change the MessageMapping and channel to sendCanvas
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

    @MessageMapping("/games/{gameId}/fillTool")
    public void fillCanvas(@DestinationVariable int gameId, FillToolCoordinates fillToolCoordinates) {
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/fillTool", fillToolCoordinates);
    }
}
