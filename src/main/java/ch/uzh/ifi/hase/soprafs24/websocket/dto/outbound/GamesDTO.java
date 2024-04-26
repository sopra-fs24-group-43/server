package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;
import java.util.HashMap;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class GamesDTO {
    private List<Integer> number;
    private List<Integer> gameId;
    private List<String> lobbyName;
    private List<List<Player>> players;
    private List<Integer> maxPlayers;
    private List<String> gamePassword;
}
