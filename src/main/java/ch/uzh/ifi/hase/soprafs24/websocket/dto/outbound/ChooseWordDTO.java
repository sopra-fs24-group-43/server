package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChooseWordDTO {
    private String type; //chooseword or startdrawing
    private int wordIndex; //0 to 2
    private String word; //the chosen word
}
