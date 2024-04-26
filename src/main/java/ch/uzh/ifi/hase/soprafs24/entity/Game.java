package ch.uzh.ifi.hase.soprafs24.entity;
import java.util.Date;

import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import java.util.HashMap;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Answer;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
<<<<<<< HEAD
import lombok.Getter;
=======
import java.util.concurrent.atomic.AtomicInteger;
>>>>>>> 02cafe028778afbe431deb6c178f54507695104b


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
public class Game {

    private RandomGenerators random;
    @Getter
    private HashMap<Integer, Player> players; //
    private Player admin; //
    @Getter
    private int gameId; //not done yet
    private Date creationDate; //
    private List<String> wordList; //init null, is a List not a ArrayList!!!
    //Settings (all accessible to admin, the ones we dont implement yet can just be a default value )
    private int currentWordIndex;
    @Getter
    private int maxPlayers; //
    private int maxRounds; //
    private int turnLength; //in seconds
    @Getter
    private String gamePassword; //not done yet, can be left for changesettings
    private String genre; //
    private ArrayList<Integer> wordLength; //not sure if necessary
    @Getter
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

    public Game(Player admin) {
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
        this.lobbyName = Integer.toString(this.gameId);
        this.points = new HashMap<Player, Integer>();
        this.pointsOfCurrentTurn = new HashMap<Player, Integer>();
        this.answersReceived = 0;
        this.currentRound = 0;
        this.currentTurn = 0;
        this.connectedPlayers = new ArrayList<>();
        this.connectedPlayers.add(admin);
    }
    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
    }
    public void setGameId(int gameId) {this.gameId = gameId;}
    public void addAnswer(Answer answer) {
        this.answers.add(answer);
        this.answersReceived++;
    }

    public void removePlayer(int userId){
        this.players.remove(userId);
    }

    public void updateGameSettings(GameSettingsDTO gameSettingsDTO) {
        this.maxPlayers = gameSettingsDTO.getMaxPlayers();
        this.maxRounds = gameSettingsDTO.getMaxRounds();
        this.turnLength = gameSettingsDTO.getTurnLength();
        this.gamePassword = gameSettingsDTO.getGamePassword();
        this.lobbyName = gameSettingsDTO.getLobbyName();
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
        this.genre = "Everything";
        this.wordList = shufflewordList();
        this.players.forEach((id, player) -> {
            this.points.put(player, 0);
            this.pointsOfCurrentTurn.put(player, 0);
            this.drawingOrder.add(id);
        });
        this.Drawer = 0;
        this.currentWordIndex = 0;
    }

    public void chooseNewDrawer() {
        this.Drawer = this.Drawer+1;
        this.Drawer = this.Drawer % this.players.size();
    }
    public String getCurrentWord(){
        return this.wordList.get(this.currentWordIndex);
    }
/*
    public LeaderBoardDTO calculateLeaderboard() {
        if (endgame){
            for (Player player : players) {
                this.points.put(player, this.points.get(player));
            }
            LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
            leaderboardDTO.setPlayers(this.players);
            leaderboardDTO.setTotalPoints(this.points);

<<<<<<< HEAD
            return leaderboardDTO;
         } else {
            for (Player player : players) {
                this.points.put(player, this.points.get(player) + this.pointsOfCurrentTurn.get(player));
            }
            LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
            leaderboardDTO.setPlayers(this.players);
            leaderboardDTO.setTotalPoints(this.points);
=======
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
            PlayerRepository.findByPlayerId(key).setPodiumPosition(value);
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



/*
    private Boolean endgame;

     public LeaderBoardDTO calculateLeaderboard() {
         if (endgame){
             for (Player player : players) {
                 this.points.put(player, this.points.get(player));
             }
             LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
             leaderboardDTO.setPlayers(this.players);
             leaderboardDTO.setTotalPoints(this.points);

             return leaderboardDTO;
         } else {
             for (Player player : players) {
                 this.points.put(player, this.points.get(player) + this.pointsOfCurrentTurn.get(player));
             }
             LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
             leaderboardDTO.setPlayers(this.players);
             leaderboardDTO.setTotalPoints(this.points);

             return leaderboardDTO;
         }
*/


>>>>>>> 02cafe028778afbe431deb6c178f54507695104b

            return leaderboardDTO;
        }
    }
*/
}
