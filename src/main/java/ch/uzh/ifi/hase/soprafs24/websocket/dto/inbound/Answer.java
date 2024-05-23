package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

import lombok.*;

@Getter
@Setter
public class Answer {
    private String type; //Answer
    private String username;
    private String answerString;

    private Boolean IsCorrect;
    private Boolean playerHasGuessedCorrectly;
}
