package ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InviteFriendDTO {
    String type; //invitefriend
    int userId;
    int gameId;
    String username;
}
