package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class sendback {
    //this is for trying out only
    HashMap<Long, Boolean> tasks;
    public sendback(HashMap<Long, Boolean> tasks){
        this.tasks = tasks;
    }
}