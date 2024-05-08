package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;

import ch.uzh.ifi.hase.soprafs24.entity.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class QuestionToSend {
    private String type;
    //for creategame
    private int gameId;
    private int userId;
    //additional fields for leavegame type below: (other types that don't need additional fields can just leave them at null

    private Player leaver;
    private boolean wasAdmin;
    private int currentPlayerCount;
    //additional fields for nextturn type below:
    private int Drawer;
    private ArrayList<Integer> drawingOrder;
    private int currentRound;
    private int currentTurn;


    public QuestionToSend(String type){
        this.type = type;
    }
}
