package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerRepository {
    private static final HashMap<Integer, Player> playerRepouserId = new HashMap<>(); //<userId, Player>
    private static final HashMap<Integer, HashMap<Integer, Player>> playerRepogameId = new HashMap<>(); //<gameId, <userId, Player>>
    private PlayerRepository() {
    }

    public static void addPlayer(int userId, int gameId, Player player) {


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
        playerRepouserId.remove(playerId);
        playerRepogameId.forEach((key, value) -> {
            if (key == gameId) {
                value.remove(playerId);
            }
        });
    }

    public static Player findByUserId(int userId) {
        Player player = playerRepouserId.get(userId);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This player does not exist!");
        }
        return player;
    }
    public static HashMap<Integer, Player> findUsersByGameId(int gameId) {
        HashMap<Integer, Player> players = new HashMap<>();
        playerRepogameId.forEach((key, value) -> {
            if (key == gameId) {
                players.putAll(value);
            }
        });
        return players;
    }
}
