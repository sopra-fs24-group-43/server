package ch.uzh.ifi.hase.soprafs24.entity;
import java.util.*;

import ch.uzh.ifi.hase.soprafs24.external_api.getWordlist;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.GameStateDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Answer;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;


import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;
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
    private HashMap<String,List<String>> wordlists;

    public Game(Player admin) {
        this.endGame = false;
        this.random = new RandomGenerators();
        this.admin = admin;
        this.players = new HashMap<Integer, Player>();
        this.players.put(admin.getUserId(), admin);
        //this.gameId = this.random.random.nextInt(1000); // needs to be unqiue (check with gamerepository)
        this.genre = genre;
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
        this.drawingOrder = new ArrayList<>();
        this.wordlists = new HashMap<>();
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
        this.connectedPlayers.remove(userId);
    }

    public void updateGameSettings(GameSettingsDTO gameSettingsDTO) {
        this.maxPlayers = gameSettingsDTO.getMaxPlayers();
        this.maxRounds = gameSettingsDTO.getMaxRounds();
        this.turnLength = gameSettingsDTO.getTurnLength();
        this.gamePassword = gameSettingsDTO.getGamePassword();
        this.lobbyName = gameSettingsDTO.getLobbyName();
    }

    public HashMap<String,List<String>> setWordList() {
        //ArrayList<String> wordlist = new ArrayList<>();
        ArrayList<String> genres = new ArrayList<>();
        genres.addAll(Arrays.asList("Science","Philosophy","Nature","Sport","Animal","Plant","life"));
        for (int i = 0;i<genres.size();i++) {
            this.wordlists.put(genres.get(i),getWordlist.getWordlist(genres.get(i)));
        }

        //int nr = this.maxRounds*this.connectedPlayers.size();
        //List<String> wordlist1 = getWordlist.getWordlist(genre);
        //Collections.shuffle(wordlist1);//list was ordered in relevance to genre, so shuffling induces unrelated words...
        //List<String> wordlist2 = wordlist1.subList(0,nr);
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
        this.genre = "Science";//input
        this.wordList=shufflewordList();
        this.wordlists = setWordList();
        this.players.forEach((id, player) -> {
            this.points.put(player, 0);
            this.pointsOfCurrentTurn.put(player, 0);
            this.drawingOrder.add(id);
        });
        this.Drawer = 0;
        this.currentWordIndex = 0;
        this.currentRound = 0;
        this.currentTurn = 0;
    }

    public void chooseNewDrawer() {
        this.Drawer = this.Drawer+1;
        this.Drawer = this.Drawer % this.players.size();
    }
    public String getCurrentWord(){
        return this.wordList.get(this.currentWordIndex);
    }

    public GameStateDTO gameStateDTO() {
        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setCurrentRound(this.currentRound);
        gameStateDTO.setCurrentTurn(this.currentTurn);
        gameStateDTO.setCurrentWordIndex(this.currentWordIndex);
        gameStateDTO.setDrawer(this.Drawer);
        return gameStateDTO;
    }


/*//old
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
*/

    public LeaderBoardDTO calculateLeaderboard() {
        LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
        this.pointsOfCurrentTurn.forEach((player, points) -> {this.points.put(player, this.points.get(player)+points);});

        leaderboardDTO.setUserIdToPlayer(this.players);

        HashMap<Integer, Integer> map = new HashMap<>();
        HashMap<Integer, Integer> map2 = new HashMap<>();

        this.points.forEach((player, points) -> {map.put(player.getUserId(), points);});
        leaderboardDTO.setTotalPoints(map);
        this.points.forEach((player, points) -> {player.setTotalPoints(points);});

        this.pointsOfCurrentTurn.forEach((player, points) -> {map2.put(player.getUserId(), points);});
        leaderboardDTO.setNewlyEarnedPoints(map2);
        this.pointsOfCurrentTurn.forEach((player, points) -> {player.setNewlyEarnedPoints(points);});


        leaderboardDTO.setPodium(assignPodiumPosition());
        this.assignPodiumPosition().forEach((player, points) -> {
            PlayerRepository.findByUserId(player).setPodiumPosition(points);
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



}
