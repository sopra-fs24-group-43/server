package ch.uzh.ifi.hase.soprafs24.entity;
import java.util.Date;

import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TimerRepository;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.utils.PointCalculatorDrawer;
import ch.uzh.ifi.hase.soprafs24.utils.PointCalculatorGuesser;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import java.util.HashMap;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Answer;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LobbyInfo;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.QuestionToSend;
import lombok.Getter;
import lombok.Setter;

import ch.uzh.ifi.hase.soprafs24.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import org.springframework.web.client.RestTemplate;

/*
public class Game {
     public String totalRounds;
     public String totalPlayers;
     public String roundLength;
     public Game (String r,String p, String l) {
         totalRounds = r;
         totalPlayers = p;
         roundLength = l;

     }
}
*/
@Getter
@Setter
public class Game {
    private boolean gameStarted;  //is set to true once get startgame was called
    private RandomGenerators random;
    private HashMap<Integer, Player> players; //
    private Player admin; //
    private int gameId; //not done yet
    private Date creationDate; //
    private List<String> wordList; //init null, is a List not a ArrayList!!!
    //Settings (all accessible to admin, the ones we dont implement yet can just be a default value )
    private int currentWordIndex;
    private int maxPlayers; //
    private int maxRounds; //
    private int turnLength; //in seconds
    private String gamePassword; //not done yet, can be left for changesettings
    private String genre; //
    private ArrayList<Integer> wordLength; //not sure if necessary
    private String lobbyName; //


    private HashMap<Player, Integer> points;
    private HashMap<Player, Integer> pointsOfCurrentTurn;

    private LeaderBoardDTO leaderboardDTO;

    //variables used to keep track of the game state
    private ArrayList<Answer> answers;
    private String actualCurrentWord;
    private int answersReceived;
    private int Drawer; //identified with index in drawingOrder
    private ArrayList<Integer> drawingOrder; //identified with userId
    private ArrayList<Integer> drawingOrderLeavers; // at the index of the leaver will be a 0
    private int currentRound; //incremented once currentturn = connectedPlayers and startturn is called
    private int currentTurn; //incremented on startturn
    private HashMap<Integer, Player> connectedPlayers; //someone might disconnect and then we have to skip his turn (not needed for M3 so just = players)
    private Boolean endGame;
    private Boolean isInGuessingPhase;
    private int timeLeftInTurn;
    private int currentCorrectGuesses;
    private int remainingTime;
    private HashMap<String, Boolean> playerCorrectGuesses;
    private HashMap<String, Integer> playerIdByName;
    private Boolean roundIsActive;
    private int playersOriginally;
    private String gamePhase; // "inLobby" = after creategame, "started" = after startgame,"choosing" = after nextturn, "drawing" = after sendchosenword, "leaderbaord" = after endturn (sendguess condition and timerService condition)

    public Game(Player admin) {
        this.gameStarted = false;
        this.endGame = false;
        this.random = new RandomGenerators();
        this.admin = admin;
        this.players = new HashMap<Integer, Player>();
        this.players.put(admin.getUserId(), admin);
        this.creationDate = new Date();
        this.wordList = new ArrayList<>();
        this.maxPlayers = 5;
        this.maxRounds = 5;
        this.turnLength = 60;
        this.gamePassword = this.random.PasswordGenerator();
        this.lobbyName = this.admin.getUsername() + "'s lobby";
        this.points = new HashMap<Player, Integer>();
        this.pointsOfCurrentTurn = new HashMap<Player, Integer>();
        this.answersReceived = 0;
        this.currentRound = 0;
        this.currentTurn = 0;
        this.connectedPlayers = new HashMap<Integer, Player>();
        this.connectedPlayers.put(admin.getUserId(), admin);
        this.drawingOrder = new ArrayList<Integer>();
        this.drawingOrderLeavers = new ArrayList<Integer>();
        this.currentCorrectGuesses = 0;
        this.remainingTime = 0;
        this.playerCorrectGuesses = new HashMap<String, Boolean>();
        this.playerIdByName = new HashMap<String, Integer>();
        this.roundIsActive = false;
        this.setGamePhase("inLobby");
    }
    public Boolean getGameStarted() {
        return this.gameStarted;
    }
    public void setGameStarted(Boolean gameStarted){
        this.gameStarted = gameStarted;
    }
    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
        this.playerIdByName.put(player.getUsername(), player.getUserId());
    }
    public void setGameId(int gameId) {this.gameId = gameId;}

    public int addAnswer(Answer answer) {
        this.answers.add(answer);
        this.answersReceived++;
        String name = answer.getUsername();

        if(roundIsActive){
         return 0;
        }

        if (this.playerCorrectGuesses.get(name)){
            return 2;
        }

        if (compareAnswer(answer.getAnswerString()) == 1){
            this.currentCorrectGuesses++;
            Player player = players.get(playerIdByName.get(name));
            this.pointsOfCurrentTurn.put(player, PointCalculatorGuesser.calculate(turnLength, remainingTime, currentCorrectGuesses));
            this.pointsOfCurrentTurn.put(players.get(drawingOrder.get(Drawer)), PointCalculatorDrawer.calculate(turnLength, remainingTime, currentCorrectGuesses) + pointsOfCurrentTurn.get(players.get(drawingOrder.get(Drawer))));
            this.playerCorrectGuesses.put(name, true);

        }

        return 0;
    }

    public int compareAnswer(String answer) {
        if(answer.equalsIgnoreCase(this.getCurrentWord())){
            return 1;
        } else {
            return 0;
        }
    }

    public void removePlayer(int userId){
        this.playerIdByName.remove(players.get(userId).getUsername());
        this.players.remove(userId);
        this.connectedPlayers.remove(userId);
    }

    public void updateGameSettings(GameSettingsDTO gameSettingsDTO) {
        if (gameSettingsDTO.getMaxPlayers() != null) {
            this.maxPlayers = gameSettingsDTO.getMaxPlayers();
        }
        if (gameSettingsDTO.getMaxRounds() != null) {
            this.maxRounds = gameSettingsDTO.getMaxRounds();
        }
        if (gameSettingsDTO.getTurnLength() != null) {
            this.turnLength = gameSettingsDTO.getTurnLength();
        }
        if (gameSettingsDTO.getGamePassword() != null) {
            this.gamePassword = gameSettingsDTO.getGamePassword();
        }
        if (gameSettingsDTO.getLobbyName() != null) {
            this.lobbyName = gameSettingsDTO.getLobbyName();
        }
    }

    public GameSettingsDTO getGameSettingsDTO() {
        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setType("GameSettingsDTO");
        gameSettingsDTO.setMaxPlayers(this.maxPlayers);
        gameSettingsDTO.setMaxRounds(this.maxRounds);
        gameSettingsDTO.setTurnLength(this.turnLength);
        gameSettingsDTO.setGamePassword(this.gamePassword);
        gameSettingsDTO.setLobbyName(this.lobbyName);
        return gameSettingsDTO;
    }

    public List<String> setWordList() {
        ArrayList<String> wordlist = new ArrayList<>();
        final String uri = "https://random-word-api.herokuapp.com/word";
        for (int i = 0;i<(maxRounds*5);i++){
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
            result=result.substring(2,result.length()-2);
            wordlist.add(result);
        }
        System.out.println(wordlist);
        return wordlist;

    }

    public List<String> shufflewordList() {
        ArrayList<String> wordpool = new ArrayList<String>();
        List<String> wordpool2;
        Collections.addAll(wordpool, "wedding", "interaction", "cheek", "quantity",
                "manufacturer", "city", "assignment", "tale", "actor", "bonus", "ratio", "energy",
                "son", "property", "collection", "theory", "procedure", "possession", "recommendation", "sister",
                "currency", "diamond", "stranger", "cabinet", "variation", "dad", "winner", "sir",
                "student", "event", "studio", "library", "highway", "category", "friendship", "camera",
                "quality", "society", "thought", "atmosphere", "signature", "television", "audience",
                "entry", "reception", "revolution", "hearing", "army", "conversation", "cancer");
        Collections.shuffle(wordpool);
        wordpool2 = wordpool.subList(0,25);

        return wordpool2;
    }

    public void startGame() {
        this.gameStarted = true;
        this.gamePhase = "started";
        this.genre = "Everything";
        this.wordList = setWordList();
        this.players.forEach((id, player) -> {
            this.points.put(player, 0);
            this.pointsOfCurrentTurn.put(player, 0);
            this.drawingOrder.add(id);
            this.drawingOrderLeavers.add(id);
            this.playerCorrectGuesses.put(player.getUsername(), false);
            this.connectedPlayers.put(id, player);
        });
        this.Drawer = -1;
        this.currentWordIndex = 1;
        this.currentRound = 1;
        this.currentTurn = 0;
        this.playersOriginally = players.size();
    }
    public void terminategame(int gameId, WebSocketService webSocketService, String reason) { //used if a player leaves the game and there are too little players or the admin left
        //allowed reasons: "admin left", "too few players", "normal"
        System.out.println("terminategame");
        Game game = GameRepository.findByGameId(gameId);
        game.setEndGame(true);
        game.setCurrentRound(game.getMaxRounds());
        game.setCurrentTurn(game.getPlayersOriginally());
        game.setGamePhase("leaderboard");
        LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
        leaderboardDTO.setReason(reason);
        webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", leaderboardDTO); //endturn
        //dont know what to send
        GameStateDTO gameStateDTO = game.receiveGameStateDTO();
        TimerRepository.haltTimer(gameId);
        game.deletegame(gameId);
        webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", gameStateDTO);
    }
    public void intigrateIntoGame(int userId, int gameId) {  //when players disconnect fromm running game and then reconnect to it
        System.out.println("intigrateIntoGame, gameId, userId: "+ gameId + ", " + ", " + userId);
        Game game = GameRepository.findByGameId(gameId);
        Player player = PlayerRepository.findByUserId(userId);
        HashMap<Integer, Player> connectedPlayers = game.getConnectedPlayers();
        connectedPlayers.put(userId, player);
        game.setConnectedPlayers(connectedPlayers);
        for (int i = 0; i < game.getDrawingOrderLeavers().size(); i++){  //setting the 0 in DrawingOrderLeavers back to userId
            if (game.getDrawingOrderLeavers().get(i) == 0){
                ArrayList<Integer> DrawingOrderLeavers = game.getDrawingOrderLeavers();
                DrawingOrderLeavers.set(i, userId);
                game.setDrawingOrderLeavers(DrawingOrderLeavers);
                break;
            }
        }

    }
    public void lostConnectionToPlayer(int userId, int gameId, WebSocketService webSocketService) {
        Game game = GameRepository.findByGameId(gameId);
        System.out.println("executing lostConnectionToPlayer: "+ userId);
        Player player = game.getPlayers().get(userId);
        game.getConnectedPlayers().remove(userId);
        if (game.getAdmin().getUserId() == player.getUserId()) { //if leaver was admin
            this.terminategame(gameId, webSocketService, "admin left");
        }
        if (game.getConnectedPlayers().size() == 1) { //if only 1 connectedplayer left
            this.terminategame(gameId, webSocketService, "too few players");
        }
        for (int id : game.getDrawingOrderLeavers() ) //setting leaver's index to 0 in DrawingOrderLeavers
            if (id == userId) {
                if (game.getDrawingOrderLeavers().indexOf(id) == game.getDrawer()) {  //leaver was Drawer
                    ArrayList<Integer> DrawingOrderLeavers = game.getDrawingOrderLeavers();
                    DrawingOrderLeavers.set(DrawingOrderLeavers.indexOf(id), 0);
                    game.setDrawingOrderLeavers(DrawingOrderLeavers);
                    this.nextturn(player.getGameId());
                }
                else {
                    ArrayList<Integer> DrawingOrderLeavers = game.getDrawingOrderLeavers();
                    DrawingOrderLeavers.set(DrawingOrderLeavers.indexOf(id), 0);
                    game.setDrawingOrderLeavers(DrawingOrderLeavers);
                }
            }
    }

    public void leavegame(int playerId, int gameId, WebSocketService webSocketService) {
        System.out.println("leavegame, playerId, gameId: "+playerId + ", "+ gameId);
        Game game = GameRepository.findByGameId(gameId);
        game.removePlayer(playerId);
        Player player = PlayerRepository.findByUserId(playerId);
        boolean wasAdmin = (player.getRole() == "admin");
        int currentPlayerCount = game.getPlayers().size();
        PlayerRepository.removePlayer(player.getUserId(), gameId);
        QuestionToSend questionToSend = new QuestionToSend("leavegame");
        questionToSend.setLeaver(player);
        questionToSend.setWasAdmin(wasAdmin); //what should happen if player was the admin? (delete game or give admin to other player?)
        questionToSend.setCurrentPlayerCount(currentPlayerCount);
        LobbyInfo lobbyInfo = new LobbyInfo();
        lobbyInfo.setType("getlobbyinfo");
        lobbyInfo.setGameId(gameId);
        lobbyInfo.setPlayers(game.getPlayers());
        lobbyInfo.setGameSettingsDTO(game.getGameSettingsDTO());
        webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", lobbyInfo);
        webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);
        webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames
        System.out.println(9);
    }
    public void deletegame(int gameId) {
        GameRepository.removeGame(gameId);
        HashMap<Integer, Player> players = PlayerRepository.findUsersByGameId(gameId); //<gameId, Player>
        players.forEach((key, value) -> {
            PlayerRepository.removePlayer(value.getUserId(), key);
        });
        PlayerRepository.removeGameId(gameId);

    }
    public void nextturn(int  gameId) {
        Game game = GameRepository.findByGameId(gameId);

        if (game.getCurrentTurn()==game.getPlayersOriginally()) {
            System.out.println("a round ended");
            game.setCurrentTurn(1);
            game.setCurrentRound(game.getCurrentRound()+1);
            choosenextdrawer(gameId);
            int currentWordIndex = game.getCurrentWordIndex() + 3;
            game.setCurrentWordIndex(currentWordIndex);
        } else {
            game.setCurrentTurn(game.getCurrentTurn() + 1);
            choosenextdrawer(gameId);
            int currentWordIndex = game.getCurrentWordIndex() + 3;
            game.setCurrentWordIndex(currentWordIndex);
        }
        game.setGamePhase("choosing");
        System.out.println("nextturn");


    }
    public void choosenextdrawer(int gameId) {
        Game game = GameRepository.findByGameId(gameId);
        int Drawer = (game.getDrawer()+1);
        Drawer = Drawer % game.getDrawingOrder().size();
        while (game.getDrawingOrderLeavers().get(Drawer) == 0) {
            Drawer = (game.getDrawer()+1);
            Drawer = Drawer % game.getDrawingOrder().size();
        }
        game.setDrawer(Drawer);
    }

    public String getCurrentWord(){
        return this.wordList.get(this.currentWordIndex);
    }

    public GameStateDTO receiveGameStateDTO() {
        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setEndGame(this.endGame);
        gameStateDTO.setConnectedPlayers(this.connectedPlayers);
        gameStateDTO.setPlayersOriginally(this.playersOriginally);
        gameStateDTO.setCurrentRound(this.currentRound);
        gameStateDTO.setCurrentTurn(this.currentTurn);
        ArrayList<String> threeWords = new ArrayList<>();
        threeWords.add(wordList.get(this.currentWordIndex-1));
        threeWords.add(wordList.get(this.currentWordIndex));
        threeWords.add(wordList.get(this.currentWordIndex+1));
        gameStateDTO.setThreeWords(threeWords);
        gameStateDTO.setDrawer(this.Drawer);
        gameStateDTO.setDrawingOrder(this.drawingOrderLeavers);  //changed to drawingOrderLeavers
        gameStateDTO.setMaxRounds(this.maxRounds);
        gameStateDTO.setGamePhase(this.gamePhase);
        gameStateDTO.setActualCurrentWord(this.actualCurrentWord);
        return gameStateDTO;
    }

    public LeaderBoardDTO calculateLeaderboard() {
        LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
        leaderboardDTO.setType("leaderboard");
        leaderboardDTO.setEndGame(this.endGame);
        this.pointsOfCurrentTurn.forEach((key, value) -> {this.points.put(key, this.points.get(key)+value);});

        leaderboardDTO.setUserIdToPlayer(this.players);

        HashMap<Integer, Integer> map = new HashMap<>();
        HashMap<Integer, Integer> map2 = new HashMap<>();

        this.points.forEach((key, value) -> {map.put(key.getUserId(), value);});
        leaderboardDTO.setTotalPoints(map);
        this.points.forEach((key, value) -> {key.setTotalPoints(value);});

        this.pointsOfCurrentTurn.forEach((key, value) -> {map2.put(key.getUserId(), value);});
        leaderboardDTO.setNewlyEarnedPoints(map2);
        this.pointsOfCurrentTurn.forEach((key, value) -> {key.setNewlyEarnedPoints(value);});


        leaderboardDTO.setPodium(assignPodiumPosition());
        this.assignPodiumPosition().forEach((key, value) -> {
            PlayerRepository.findByUserId(key).setPodiumPosition(value);
        });

        return leaderboardDTO;

    }

    public HashMap<Integer, Integer> assignPodiumPosition() {
        HashMap<Integer, Integer> map = new HashMap<>();
        this.points.forEach((key, value) -> {
            AtomicInteger i = new AtomicInteger();
            i.set(1);
            this.points.forEach((key2, value2) -> {
                if (value<value2) {
                    i.getAndIncrement();
                }
            });
            map.put(key.getUserId(), i.get());
        });
        return map;
    }

}
