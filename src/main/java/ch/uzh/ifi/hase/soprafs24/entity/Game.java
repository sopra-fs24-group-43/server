package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import java.util.Date;
//import ch.uzh.ifi.hase.soprafs24.entity.Leaderboard;
import java.util.HashMap;
//import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.Answer;
import java.util.Random;

public class Game {

    private Random random;
    private ArrayList<Player> players; //
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
    //private Leaderboard leaderboard;

    //private ArrayList<Answer> answers;
    private Player Drawer;
    private ArrayList<Player> drawingOrder;
    private int currentRound; //incremented once currentturn = connectedPlayers and startturn is called
    private int currentTurn; //incremented on startturn
    private ArrayList<Player> connectedPlayers; //someone might disconnect and then we have to skip his turn (not needed for M3 so just = players)

    private HashMap<Player, Integer> pointsOfCurrentTurn;

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
     }
    public Game(Player admin) {
        this.random = new Random();
        this.admin = admin;
        this.players.add(admin);
        this.gameId = this.random.nextInt(1000); // needs to be unqiue (check with gamerepository)

        this.creationDate = new Date();
        this.wordList = new ArrayList<>();
        this.maxPlayers = 5;
        this.maxRounds = 5;
        this.turnLength = 60;
        //this.gamePassword = generate random unique starting password (8 characters)

        //this.wordLength = new ArrayList<Integer>();
        //this.wordLength.add(2);
        //this.wordLength.add(10);
        this.lobbyName = Integer.toString(this.gameId);
        this.currentRound = 0;
        this.currentTurn = 0;
        this.connectedPlayers = new ArrayList<>();
        this.connectedPlayers.add(admin);
    }
}
