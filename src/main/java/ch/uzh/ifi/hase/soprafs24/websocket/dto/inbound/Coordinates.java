package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class Coordinates {
    private int x;
    private int y;
    private int newX;
    private int newY;
    private String color;
}
