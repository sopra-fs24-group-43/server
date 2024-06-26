package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class LeaderBoardDTO {
    private String type; //leaderboard
    private String reason; //reason why the game ended (only used when endGame = true), "admin left", "too few players", "normal"
    private Boolean endGame;
    private HashMap<Integer, Player> userIdToPlayer;  //<userId, Player>
    private HashMap<Integer, Integer> totalPoints;  //<userId, totalPoints>
    private HashMap<Integer, Integer> newlyEarnedPoints;  //<userId, newlyEarnedPoints>
    private HashMap<Integer, Integer> Podium;  //<userId, Podium>
    private LinkedHashMap<Integer, Player> userIdToPlayerSorted;
}
