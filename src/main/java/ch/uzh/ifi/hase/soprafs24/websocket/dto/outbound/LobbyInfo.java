package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.GameSettingsDTO;
import lombok.Getter;
import lombok.Setter;


import java.util.HashMap;

@Setter
@Getter
public class LobbyInfo {
    private int gameId;
    private HashMap<Integer, Player> Players; // <userId, Player>
    private GameSettingsDTO gameSettingsDTO;
}
