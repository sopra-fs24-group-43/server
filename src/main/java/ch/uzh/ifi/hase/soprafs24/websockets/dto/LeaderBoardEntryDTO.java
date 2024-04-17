package ch.uzh.ifi.hase.soprafs24.websockets.dto;

public class LeaderBoardEntryDTO {
    
    private Long ID;
    private String name;
    private int totalScore;
    private int roundScore;
    private boolean guessedCorrectly;

    public Long getID() {
        return ID;
    }

    public void setID(Long iD) {
        ID = iD;
    }

    public void setRoundScore(int roundScore) {
        this.roundScore = roundScore;
    }

    public String getName() {
        return name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }    

    public int getRoundScore() {
        return roundScore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGuessedCorrectly() {
        return guessedCorrectly;
    }

    public void setGuessedCorrectly(boolean guessedCorrectly) {
        this.guessedCorrectly = guessedCorrectly;
    }
}
