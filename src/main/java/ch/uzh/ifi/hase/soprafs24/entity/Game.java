package ch.uzh.ifi.hase.soprafs24.entity;
import java.util.Date;

import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
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
    private int answersReceived;
    private int Drawer; //identified with index in drawingOrder
    private ArrayList<Integer> drawingOrder; //identified with userId
    private int currentRound; //incremented once currentturn = connectedPlayers and startturn is called
    private int currentTurn; //incremented on startturn
    private ArrayList<Player> connectedPlayers; //someone might disconnect and then we have to skip his turn (not needed for M3 so just = players)
    private Boolean endGame;
    private Boolean isInGuessingPhase;
    private int timeLeftInTurn;
    private int currentCorrectGuesses;
    private int remainingTime;
    private HashMap<String, Boolean> PlayerCorrectGuesses;

    public Game(Player admin) {
        this.gameStarted = false;
        this.endGame = false;
        this.random = new RandomGenerators();
        this.admin = admin;
        this.players = new HashMap<Integer, Player>();
        this.players.put(admin.getUserId(), admin);
        //this.gameId = this.random.random.nextInt(1000); // needs to be unqiue (check with gamerepository)

        this.creationDate = new Date();
        this.wordList = new ArrayList<>();
        this.maxPlayers = 5;
        this.maxRounds = 5;
        this.turnLength = 60;
        this.gamePassword = this.random.PasswordGenerator();
        //this.lobbyName = Integer.toString(this.gameId);
        this.lobbyName = this.admin.getUsername() + "'s lobby";
        this.points = new HashMap<Player, Integer>();
        this.pointsOfCurrentTurn = new HashMap<Player, Integer>();
        this.answersReceived = 0;
        this.currentRound = 0;
        this.currentTurn = 0;
        this.connectedPlayers = new ArrayList<>();
        this.connectedPlayers.add(admin);
        this.drawingOrder = new ArrayList<Integer>();
        this.currentCorrectGuesses = 0;
        this.remainingTime = 0;
        this.PlayerCorrectGuesses = new HashMap<String, Boolean>();
    }
    public Boolean getGameStarted() {
        return this.gameStarted;
    }
    public void setGameStarted(Boolean gameStarted){
        this.gameStarted = gameStarted;
    }
    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
    }
    public void setGameId(int gameId) {this.gameId = gameId;}

    public int addAnswer(Answer answer) {
        this.answers.add(answer);
        this.answersReceived++;

        int result = compareAnswer(answer.getAnswerString());
        String name = answer.getUsername();
        if (result == 1){
            if (this.PlayerCorrectGuesses.get(name))
            currentCorrectGuesses++;
            Player player = players.get(name);
            player.setNewlyEarnedPoints(PointCalculatorGuesser.calculate(turnLength, remainingTime, currentCorrectGuesses));
            players.get(drawingOrder.get(Drawer)).setNewlyEarnedPoints(PointCalculatorDrawer.calculate(turnLength, remainingTime, currentCorrectGuesses));
        }
        return result;
    }

    public int compareAnswer(String answer) {
        if(answer.equalsIgnoreCase(this.getCurrentWord())){
            return 1;
        } else {
            return 0;
        }
    }

    public void removePlayer(int userId){
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
        this.genre = "Everything";
        this.wordList = setWordList();
        this.players.forEach((id, player) -> {
            this.points.put(player, 0);
            this.pointsOfCurrentTurn.put(player, 0);
            this.drawingOrder.add(id);
        });
        this.Drawer = 0;
        this.currentWordIndex = 0;
        this.currentRound = 1;
        this.currentTurn = 1;
    }

    public void chooseNewDrawer() {
        this.Drawer = this.Drawer+1;
        this.Drawer = this.Drawer % this.players.size();
    }

    public String getCurrentWord(){
        return this.wordList.get(this.currentWordIndex);
    }

    public GameStateDTO receiveGameStateDTO() {
        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setType("GameStateDTO");
        gameStateDTO.setCurrentRound(this.currentRound);
        gameStateDTO.setCurrentTurn(this.currentTurn);
        gameStateDTO.setCurrentWordIndex(this.currentWordIndex); //not index (from the list of words) but the actual word
        gameStateDTO.setDrawer(this.Drawer);
        return gameStateDTO;
    }

    public LeaderBoardDTO calculateLeaderboard() {
        LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
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
