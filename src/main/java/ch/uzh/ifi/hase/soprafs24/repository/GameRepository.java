package ch.uzh.ifi.hase.soprafs24.repository;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class GameRepository {
    private static final HashMap<Integer, Game> gameRepo = new HashMap<>();

    private GameRepository() {
    }

    public static void addGame(int gameId, Game game) {


        gameRepo.put(gameId, game);
    }

    public static void removeGame(int gameId) {
        gameRepo.remove(gameId);
    }

    public static Game findByGameId(int gameId) {
        Game game = gameRepo.get(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This game does not exist!");
        }
        return game;
    }

    public static HashMap<Integer, Game> getallGames() {
        return gameRepo;
    }

    public static boolean gameIdtaken(int gameId) {
        Game game = gameRepo.get(gameId);
        if (game == null) {
            return false;
        }
        else {
            return true;
        }
    }
}