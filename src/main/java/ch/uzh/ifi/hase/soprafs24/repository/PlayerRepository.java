package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.InboundPlayer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerRepository {
    private static final RandomGenerators randomgenerators = new RandomGenerators();
    private static final HashMap<Integer, Player> playerRepouserId = new HashMap<>(); //<userId, Player>
    private static final HashMap<Integer, HashMap<Integer, Player>> playerRepogameId = new HashMap<>(); //<gameId, <userId, Player>>
    private static final HashMap<Integer, Player>  playerRepoguest = new HashMap<>(); //<guestId, Playe>
    private PlayerRepository() {
    }
    //you can and should only use public methods with game.examplemethod();
    public static InboundPlayer createPlayerFromGuest() {
        // gameId = null, role = null and only added to playerRepoguest
        int guestId = randomgenerators.GuestIdGenerator();
        String guestusername = "guestuser" + guestId;
        boolean isGuest = true;
        Integer gameId = null;
        ArrayList<Integer> friends = new ArrayList<>();
        String role = null;
        Player player = new Player(guestusername, guestId, isGuest, gameId, friends, role);
        playerRepoguest.put(guestId, player);

        InboundPlayer inboundPlayer = new InboundPlayer();
        inboundPlayer.setType("createPlayerFromGuest");
        inboundPlayer.setUsername(guestusername);
        inboundPlayer.setuserId(guestId);
        inboundPlayer.setIsGuest(isGuest);
        inboundPlayer.setGameId(gameId);
        inboundPlayer.setFriends(friends);
        inboundPlayer.setRole(role);
        return inboundPlayer;
    }
    public static void addPlayer(int playerId, int gameId, Player player) {
        if (player.getIsGuest()) {
            addGuest(playerId, gameId, player);
        }
        else {
            addPlayer1(playerId, gameId, player);
        }
    }
    private static void addGuest(int guestId, int gameId, Player player) { //dont use for player

        playerRepouserId.put(guestId, player);
        playerRepoguest.put(guestId, player);
        HashMap<Integer, Player> playersinsamegame = findUsersByGameId(gameId);
        if (playersinsamegame.isEmpty()){
            HashMap<Integer, Player> playersinsamegame2 = new HashMap<>();
            playersinsamegame2.put(guestId, player);
            playerRepogameId.put(gameId, playersinsamegame2);
        }
        else {
            playersinsamegame.put(guestId, player);
            playerRepogameId.put(gameId, playersinsamegame);
        }
    }
    private static void addPlayer1(int userId, int gameId, Player player) { //dont use for guest


        playerRepouserId.put(userId, player);
        HashMap<Integer, Player> playersinsamegame = findUsersByGameId(gameId);
        if (playersinsamegame.isEmpty()){
            HashMap<Integer, Player> playersinsamegame2 = new HashMap<>();
            playersinsamegame2.put(userId, player);
            playerRepogameId.put(gameId, playersinsamegame2);
        }
        else {
            playersinsamegame.put(userId, player);
            playerRepogameId.put(gameId, playersinsamegame);
        }
    }
    public static void removePlayer(int playerId, int gameId) {
        Player player = findByUserId(playerId);
        if (player.getIsGuest()) {
            removeGuest(playerId, gameId);
        }
        else {
            removePlayer1(playerId, gameId);
        }
    }
    private static void removeGuest(int guestId, int gameId) { //works for guests and players
        playerRepouserId.remove(guestId);
        playerRepoguest.remove(guestId);
        playerRepogameId.forEach((key, value) -> {
            if (key == gameId) {
                value.remove(guestId);
            }
        });
    }
    private static void removePlayer1(int playerId, int gameId) { //dont use for guest
        playerRepouserId.remove(playerId);
        playerRepogameId.forEach((key, value) -> {
            if (key == gameId) {
                value.remove(playerId);
            }
        });
    }
    public static void removeGameId(int gameId) {
        playerRepogameId.remove(gameId);
    }

    private static Player findByGuestId(int guestId) { //works only for guests
        return playerRepoguest.get(guestId);
    }

    public static Player findByUserId(int userId) { //works for guests and players
        Player player = playerRepouserId.get(userId);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This player does not exist!");
        }
        return player;
    }
    public static HashMap<Integer, Player> findUsersByGameId(int gameId) { //works for guests and players
        HashMap<Integer, Player> players = new HashMap<>();
        playerRepogameId.forEach((key, value) -> {
            if (key == gameId) {
                players.putAll(value);
            }
        });
        return players;
    }
    public static boolean guestIdtaken(int guestId) {
        Player player = playerRepoguest.get(guestId);
        if (player == null) {
            return false;
        }
        else {
            return true;
        }
    }
}
