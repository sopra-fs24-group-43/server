package ch.uzh.ifi.hase.soprafs24.websockets.dto;

import java.util.ArrayList;

public class GameSettingsDTO {
    
    private String admin;
    private int rounds;
    private int timer;
    private String genre;
    private ArrayList<String> wordlist;
    private int wordLength;

    public String getAdmin() {
        return admin;
    }
    
    public void setAdmin(String admin) {
        this.admin = admin;
    }    

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public ArrayList<String> getWordlist() {
        return wordlist;
    }

    public void setWordlist(ArrayList<String> wordlist) {
        this.wordlist = wordlist;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

}
