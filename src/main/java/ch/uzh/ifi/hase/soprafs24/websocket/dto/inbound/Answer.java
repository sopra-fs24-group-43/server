package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

import lombok.*;

@Getter
@Setter
public class Answer {
    private String username;
    private String answerString;

    private boolean IsCorrect;
    private boolean playerHasGuessedCorrectly;
}
