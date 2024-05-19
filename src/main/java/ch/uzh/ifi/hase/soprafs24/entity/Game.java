package ch.uzh.ifi.hase.soprafs24.entity;
import java.util.*;

import ch.uzh.ifi.hase.soprafs24.external_api.getWordlist;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.utils.PointCalculatorDrawer;
import ch.uzh.ifi.hase.soprafs24.utils.PointCalculatorGuesser;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Answer;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;


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
    private List<String> selected_genres; //
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
    private HashMap<String,List<String>> wordlists;
    //private int nr_genres;

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
        this.connectedPlayers = new HashMap<Integer, Player>();
        this.connectedPlayers.put(admin.getUserId(), admin);
        this.drawingOrder = new ArrayList<Integer>();
        this.currentCorrectGuesses = 0;
        this.remainingTime = 0;
        this.playerCorrectGuesses = new HashMap<String, Boolean>();
        this.playerIdByName = new HashMap<String, Integer>();
        this.roundIsActive = false;
        this.wordlists = new HashMap<>();
        this.selected_genres = new ArrayList<>();
        //this.nr_genres = 0;
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

    public HashMap<String,List<String>> setWordList(List<String> selected_genres) {
        //ArrayList<Integer> listlengths = new ArrayList<>();
        //ArrayList<String> genres = new ArrayList<>();
        //genres.addAll(selected_genres);
        //genres.addAll(Arrays.asList("Science", "Philosophy", "Sport", "Animal", "Plant", "life", "human"));
        for (int i = 0; i < selected_genres.size(); i++) {
            this.wordlists.put(selected_genres.get(i), getWordlist.getWordlist(selected_genres.get(i)));
            this.wordList.addAll(getWordlist.getWordlist(selected_genres.get(i)));
            //listlengths.add(getWordlist.getWordlist(genres.get(i)).size());
        }
        Collections.shuffle(wordList);
        //System.out.println(listlengths);
        int nr_words = this.maxRounds*this.playersOriginally*3;
        //this.nr_genres = nr_words/40;

        //List<String> wordlist1 = getWordlist.getWordlist(genre);
        //Collections.shuffle(wordlist1);//list was ordered in relevance to genre, so shuffling induces unrelated words...
        //List<String> wordlist2 = wordlist1.subList(0,nr);
        List<String> wordlist2 = wordList.subList(0,nr_words);
        return wordlists;
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
        this.wordList=shufflewordList();
        this.wordlists = setWordList(this.selected_genres);

        this.gameStarted = true;
        this.players.forEach((id, player) -> {
            this.points.put(player, 0);
            this.pointsOfCurrentTurn.put(player, 0);
            this.drawingOrder.add(id);
            this.playerCorrectGuesses.put(player.getUsername(), false);
        });
        this.Drawer = -1;
        this.currentWordIndex = 1;
        this.currentRound = 1;
        this.currentTurn = 0;
        this.playersOriginally = players.size();
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
        ArrayList<String> threeWords = new ArrayList<>();
        threeWords.add(wordList.get(this.currentWordIndex-1));
        threeWords.add(wordList.get(this.currentWordIndex));
        threeWords.add(wordList.get(this.currentWordIndex+1));
        gameStateDTO.setThreeWords(threeWords);
        //gameStateDTO.setCurrentWordIndex(this.currentWordIndex); //not index (from the list of words) but the actual word
        gameStateDTO.setDrawer(this.Drawer);
        gameStateDTO.setDrawingOrder(this.drawingOrder);
        gameStateDTO.setPlayersOriginally(this.playersOriginally);
        gameStateDTO.setMaxRounds(this.maxRounds);
        //setConnectedPlayers not done yet!
        return gameStateDTO;
    }

    public LeaderBoardDTO calculateLeaderboard() {
        LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
        this.pointsOfCurrentTurn.forEach((key, value) -> {this.points.put(key, this.points.get(key)+value);});

        leaderboardDTO.setUserIdToPlayer(this.players);

        HashMap<Integer, Integer> map = new HashMap<>();
        HashMap<Integer, Integer> map2 = new HashMap<>();
        LinkedHashMap<Integer, Player> map3 = new LinkedHashMap<>();

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

        int i = 1;
        while (i<leaderboardDTO.getPodium().size()+1) {
            Integer finalI = i;
            leaderboardDTO.getPodium().forEach((key, value) -> {
                if (finalI.equals(value)) {
                    map3.put(key,PlayerRepository.findByUserId(key));
                }
            });
            i=i+1;}
        leaderboardDTO.setUserIdToPlayerSorted(map3);

        return leaderboardDTO;

    }
    /*//old
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
*/
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
