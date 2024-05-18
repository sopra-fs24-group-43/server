package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReconnectionDTO {
    private String type;  //ReconnectionDTO
    private int gameId;
    private String role;
}
