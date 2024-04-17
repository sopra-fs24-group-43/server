package ch.uzh.ifi.hase.soprafs24.websockets.dto;

import java.util.ArrayList;

public class LeaderBoardDTO {
    
    private boolean gameEnd;
    private ArrayList<LeaderBoardEntryDTO> entries;

    public boolean isGameEnd() {
        return gameEnd;
    }
    
    public void setGameEnd(boolean gameEnd) {
        this.gameEnd = gameEnd;
    }    

    public ArrayList<LeaderBoardEntryDTO> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<LeaderBoardEntryDTO> entries) {
        this.entries = entries;
    }
}
