package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
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
import java.util.Map;
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
    GetWordlist getWordlist;

    public WebSocketController( WebSocketService webSocketService, RandomGenerators randomGenerators, GetWordlist getWordlist) {
        this.webSocketService = webSocketService;
        this.randomGenerators = randomGenerators;
        this.timerService = new TimerService(webSocketService);
        this.reconnectionHelper = new ReconnectionHelper(webSocketService);
        this.getWordlist = getWordlist;
    }
    @MessageMapping("/games/{gameId}/invitefriend/{userId}")
    public void invitefriend(@DestinationVariable int gameId, @DestinationVariable int userId) {
        Game game = GameRepository.findByGameId(gameId);
        if (game != null) {
                QuestionToSend questionToSend = new QuestionToSend();
                questionToSend.setType("invitefriend");
                questionToSend.setUserId(userId); //userId of the friend that got invited
                questionToSend.setGameId(gameId);
            this.webSocketService.sendMessageToClients("/topic/landing/"+userId, questionToSend);
            Player player = PlayerRepository.findByUserId(userId);
            if (player != null) {
                if (player.getGameId() != -1) {
                    this.webSocketService.sendMessageToClients("/topic/games/" + player.getGameId() + "/general/"+userId, questionToSend);  //for the players in the Lobby
                }
            }
        }
    }
    @MessageMapping("/landing/createguestplayer")
    public void createguestplayer() { //how does client know its his response and not for some other client?
        //returns inboundPlayer with type = createguestplayer and with some fields = null
        int guestId = this.randomGenerators.GuestIdGenerator();
        InboundPlayer inboundPlayer = PlayerRepository.createPlayerFromGuest(guestId);
        this.webSocketService.sendMessageToClients("/topic/landing", inboundPlayer);
    }
    @MessageMapping("/landing/deletetempguestplayer/{guestId}")
    public void deletetempguestplayer(@DestinationVariable int guestId) {
        System.out.println("deleting temp guest player");
        PlayerRepository.printAllPlayers();
        PlayerRepository.removePlayer(guestId, -1);
        PlayerRepository.printAllPlayers();

    }
    @MessageMapping("/landing/creategame")
    public void creategame(InboundPlayer inboundPlayer){ //the client can't know the gameId of the game when he first creates it so he can just pass some int (e.g. 1001)
        Player player = new Player(inboundPlayer.getUsername(),
                inboundPlayer.getUserId(), inboundPlayer.getIsGuest(),inboundPlayer.getGameId(),
                inboundPlayer.getFriends(), inboundPlayer.getRole());
        Game game = new Game(player, webSocketService, timerService, randomGenerators, getWordlist);
        int gameId = randomGenerators.GameIdGenerator();
        while (GameRepository.gameIdtaken(gameId)) {
            gameId = randomGenerators.GameIdGenerator();
        }
        player.setGameId(gameId);
        int playerId = inboundPlayer.getUserId();
        PlayerRepository.addPlayer(playerId, gameId, player);
        game.setGameId(gameId);
        GameRepository.addGame(gameId,game);
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("creategame");
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
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("deletegame");
        questionToSend.setGameId(null);
        questionToSend.setUserId(null);
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
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("joingame");
        //new beloew


        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", inboundPlayer); //should return something joiner doesnt need to receive it
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames
    }

    @MessageMapping("/games/{gameId}/leavegame/{playerId}")
    public void leavegame(@DestinationVariable int gameId, @DestinationVariable int playerId){ //needs change = can admin leavegame or only deletegame?
        Game game = GameRepository.findByGameId(gameId);
        if (game != null) {
            game.leavegame(playerId, gameId);
        }
        QuestionToSend questionToSend = new QuestionToSend();
        questionToSend.setType("leavegame");
        questionToSend.setGameId(null);
        questionToSend.setUserId(null);

/*
        GameStateDTO gameStateDTO = game.receiveGameStateDTO();
        PlayerRepository.removePlayer(inboundPlayer.getUserId(),gameId);
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
*/
    }
    @MessageMapping("/games/{gameId}/updategamesettings")
    public void updategamesettings(@DestinationVariable int gameId, GameSettingsDTO gameSettingsDTO){
        Game game = GameRepository.findByGameId(gameId);
        if (game == null) {
            return;
        }
        game.updateGameSettings(gameSettingsDTO);

        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameSettingsDTO);

    }
    @MessageMapping("/games/{gameId}/getlobbyinfo")
    public void getlobbyinfo(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);
        if (game == null) {
            return;
        }
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
        QuestionToSend questionToSend = new QuestionToSend(); //this is solely for the Table to take the game off the List of lobbies
        questionToSend.setType("startgame");
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
        this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames
    }

    @MessageMapping("/games/{gameId}/getgamestate/{userId}")
    public void getgamestate(@DestinationVariable int gameId, @DestinationVariable int userId){
        Game game = GameRepository.findByGameId(gameId);
        if (game == null) {
            return;
        }
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
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        System.out.println("sessionAttr: "+sessionAttributes);
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

        if (PlayerRepository.findByUserId(userId) != null) {
            Player player = PlayerRepository.findByUserId(userId);
            int gameId = player.getGameId();
            Game game = GameRepository.findByGameId(gameId);
            if (game == null) {
                return;
            }
            if (game.getPlayers().containsKey(userId) && game.getGameStarted()) {
                game.intigrateIntoGame(userId, gameId);
                QuestionToSend questionToSend = new QuestionToSend();
                questionToSend.setType("sendcanvasforrecon");
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
        Game game = GameRepository.findByGameId(gameId);
        if (game == null) {
            return;
        }
        game.nextturn(gameId);
        GameStateDTO gameStateDTO = game.receiveGameStateDTO();
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
        timerService.doTimer(15,1, gameId, "/topic/games/" + gameId + "/general", "choosing"); //Timer to choose word

    }

    @MessageMapping("/games/{gameId}/sendchosenword")
    public void sendchosenword(@DestinationVariable int gameId, ChooseWordDTO chooseWordDTO) {
        timerService.doShutDownTimer(gameId); //shutsdown timer from nextturn "choosing"
        Game game = GameRepository.findByGameId(gameId);
        if (game == null) {
            return;
        }
        game.setActualCurrentWord(chooseWordDTO.getWord());
        game.setGamePhase("drawing");
        chooseWordDTO.setType("startdrawing");
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", chooseWordDTO);
        System.out.println("sendchosenwordtimer");

        timerService.doTimer(game.getTurnLength(),1, gameId, "/topic/games/" + gameId + "/general", "drawing"); //Timer to play turn (drawing & guessing)
    }
    @MessageMapping("/games/{gameId}/sendguess")
    public void sendguess(@DestinationVariable int gameId, Answer answer){
        System.out.println("sendguess output");
        Game game = GameRepository.findByGameId(gameId);
        if (game == null) {
            return;
        }
        int flag = game.addAnswer(answer);
        if (flag == 2){
            answer.setPlayerHasGuessedCorrectly(true);
            answer.setIsCorrect(false);
        } else if (flag == 1){
            answer.setPlayerHasGuessedCorrectly(false);
            answer.setIsCorrect(true);
            Answer correctGuessNotification = new Answer();
            correctGuessNotification.setUsername(answer.getUsername());
            correctGuessNotification.setAnswerString(answer.getUsername() + " has guessed the word correctly!");
            correctGuessNotification.setType("notification");
            this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/sendguess", correctGuessNotification);
        } else {
            answer.setPlayerHasGuessedCorrectly(false);
            answer.setIsCorrect(false);
        } 
        if (!answer.IsCorrect) {
        answer.setType("Answer");
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/sendguess", answer);
        }
        
        if (flag == 3){
            return;
        }
        if (game.getCurrentCorrectGuesses() >= game.getConnectedPlayers().size()-1 && game.getTimeLeftInTurn() >= 0){
            timerService.doShutDownTimer(game.getGameId());  //shutsdown timer from sendchosenword "drawing"
            if (game.getCurrentRound()==game.getMaxRounds() && game.getCurrentTurn()== game.getConnectedPlayers().size()) {
                game.setEndGame(true);
            }
            LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
            game.setGamePhase("leaderboard");
            if (game.getEndGame()){
                game.setReason("normal");
                leaderboardDTO.setReason("normal");
            }
            this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);
            if (game.getEndGame()) { //more time to look at Podium (endGame)
                System.out.println("endgame");
                timerService.doTimer(20,1, gameId, "/topic/games/" + gameId + "/general", "leaderboard"); //timer to look at leaderboard
            }
            else {
                System.out.println("endturn");
                timerService.doTimer(5,1, gameId, "/topic/games/" + gameId + "/general", "leaderboard"); //timer to look at leaderboard
            }
        }
    }

    @MessageMapping("/games/{gameId}/getleaderboard")
    public void endturn(@DestinationVariable int gameId){
        Game game = GameRepository.findByGameId(gameId);

        LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
        if (game.getEndGame()){
            leaderboardDTO.setReason(game.getReason());
        }
        leaderboardDTO.setType("leaderboard2");
        this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO);

    }


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
