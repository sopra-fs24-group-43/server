package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerRepository {
    private static final HashMap<Integer, Player> playerRepo = new HashMap<>();

    private PlayerRepository() {
    }

    public static void addPlayer(int playerId, int gameId, Player player) {


        playerRepo.put(playerId, player);
        playerRepo.put(gameId, player);
    }

    public static void removePlayer(int playerId, int gameId) {
        playerRepo.remove(playerId);
        playerRepo.remove(gameId);
    }

    public static Player findByPlayerId(int playerId) {
        Player player = playerRepo.get(playerId);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This player does not exist!");
        }
        return player;
    }
    public static ArrayList<Player> findUserByGameId(int gameId) {
        ArrayList<Player> players = new ArrayList<>();
        playerRepo.forEach((key, value) -> {
            if (key == gameId) {
                players.add(value);
            }
        });
        return players;
    }
}
