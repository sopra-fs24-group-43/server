package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LeaderBoardDTO {
    private String type;
    private String totalRounds;
    private String totalPlayers;
    private String roundLength;
    private List<Player> players;
    private HashMap<Player, Integer> totalPoints;
}


