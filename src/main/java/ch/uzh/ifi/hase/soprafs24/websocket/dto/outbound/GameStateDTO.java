package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class GameStateDTO {
    private String type;
    private Boolean endGame;
    private ArrayList<Player> connectedPlayers;
    private int currentRound;
    private int currentTurn;
    private int currentWordIndex;
    private int Drawer;

}
