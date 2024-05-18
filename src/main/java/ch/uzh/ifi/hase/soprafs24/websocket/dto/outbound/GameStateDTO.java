package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class GameStateDTO {
    private String type;
    private Boolean endGame;
    private HashMap<Integer, Player> connectedPlayers;
    private int playersOriginally; //how many players where originally in the Game when it started
    private int currentRound;
    private int currentTurn;
    private ArrayList<String> threeWords;
    private int drawer;
    private ArrayList<Integer> drawingOrder; //<userId>
    private int maxRounds;
    private String gamePhase;
    private String actualCurrentWord;
}
