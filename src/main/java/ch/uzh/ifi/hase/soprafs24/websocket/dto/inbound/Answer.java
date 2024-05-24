package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

import lombok.*;

@Getter
@Setter
public class Answer {
    private String type; //Answer
    private String username;
    private String answerString; // chat message

    public Boolean IsCorrect; // to display that the guess has been guessed
    private Boolean playerHasGuessedCorrectly; // if it's true it should not display messages from the guessers
}
