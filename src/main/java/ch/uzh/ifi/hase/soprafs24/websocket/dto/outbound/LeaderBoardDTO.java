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
    //maybe indicate next turn
    private HashMap<Integer, Player> userIdToPlayer;  //<userId, Player>
    private HashMap<Integer, Integer> totalPoints;  //<userId, totalPoints>
    private HashMap<Integer, Integer> newlyEarnedPoints;  //<userId, newlyEarnedPoints>
    private HashMap<Integer, Integer> Podium;  //<userId, Podium>
    private LinkedHashMap<Integer, Player> userIdToPlayerSorted;
}
