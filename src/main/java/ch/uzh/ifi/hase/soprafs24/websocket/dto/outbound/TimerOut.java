package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class TimerOut {
    private String type; //TimerOut
    private String gamePhase; //drawing, choosing
    private int gameId;
    private int time; //1 to game.TurnLength
    private int interval; //Interval in which these get sent out
    private  int length; //Length of the Timer (how it ticks e.g. 60s)

}
