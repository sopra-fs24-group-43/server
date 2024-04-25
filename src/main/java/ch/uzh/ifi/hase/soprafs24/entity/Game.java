package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import java.util.HashMap;
import java.util.List;

public class Game {
     public String totalRounds;
     public String totalPlayers;
     public String roundLength;

     public List<Player> players;

     public HashMap<Player, Integer> pointsOfCurrentRound;
     public HashMap<Player, Integer> totalPoints;
     public LeaderBoardDTO calculateLeaderboard() {
         for (Player player : players) {
             this.totalPoints.put(player, this.totalPoints.get(player)+this.pointsOfCurrentRound.get(player));
         }
         LeaderBoardDTO leaderboardDTO = new LeaderBoardDTO();
         leaderboardDTO.setPlayers(this.players);
         leaderboardDTO.setTotalPoints(this.totalPoints);

         return leaderboardDTO;
     }

     public Game (String r, String p, String l) {
         totalRounds = r;
         totalPlayers = p;
         roundLength = l;



     }

}
