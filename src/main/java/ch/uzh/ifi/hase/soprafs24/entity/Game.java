package ch.uzh.ifi.hase.soprafs24.entity;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import java.util.Date;
import ch.uzh.ifi.hase.soprafs24.entity.Leaderboard;
import java.util.HashMap;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Answer;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;


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
    private HashMap<Integer, Player> players; //
    private Player admin; //
    private int gameId; //not done yet
    private Date creationDate; //
    private ArrayList<String> wordList; //init null
    //Settings (all accessible to admin, the ones we dont implement yet can just be a default value )
    private int maxPlayers; //
    private int maxRounds; //
    private int turnLength; //in seconds
    private String gamePassword; //not done yet, can be left for changesettings
    private String genre; //
    private ArrayList<Integer> wordLength; //not sure if necessary
    private String lobbyName; //

    //variables used to keep track of the game state
    private HashMap<Player, Integer> points;
    private HashMap<Player, Integer> pointsOfCurrentRound;
    private Leaderboard leaderboard;

    private ArrayList<Answer> answers;
    private Player Drawer;
    private ArrayList<Player> drawingOrder;
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

        //this.wordLength = new ArrayList<Integer>();
        //this.wordLength.add(2);
        //this.wordLength.add(10);
        this.lobbyName = Integer.toString(this.gameId);
        this.currentRound = 0;
        this.currentTurn = 0;
        this.connectedPlayers = new ArrayList<>();
        this.connectedPlayers.add(admin);
    }
    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
    }
    public void setGameId(int gameId) {this.gameId = gameId;}

    public void removePlayer(int userId){
        this.players.remove(userId);
    }


}
