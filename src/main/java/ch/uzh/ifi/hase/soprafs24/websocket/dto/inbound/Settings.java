package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class Settings {
    private String type;
    private String totalRounds;
    private String totalPlayers;
    private String roundLength;
}


//public class GameSettings {

//}