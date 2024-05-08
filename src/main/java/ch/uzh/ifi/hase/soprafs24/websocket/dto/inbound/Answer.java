package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

import lombok.*;

@Getter
@Setter

public class Answer {
    private String username;
    private int userId;
    private int gameId;

    private int secTimeLeft;
    private String answerString;

    private boolean IsCorrect;
}
