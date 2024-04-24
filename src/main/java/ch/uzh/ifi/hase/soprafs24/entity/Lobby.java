package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs24.websockets.dto.GameSettingsDTO;

public class Lobby {

    private int roundDuration;
    private int rounds;
    private User admin;
    private String genre;
    private ArrayList<String> wordList;
    private ArrayList<User> players;
    private int currentRound;
    private int maxPlayers;

    public Lobby(User admin){
        this.roundDuration = 60;
        this.rounds = 3;
        this.admin = admin;
        this.genre = null;
        this. wordList = new ArrayList<String>();
        this.players = new ArrayList<User>();
        this.players.add(admin);
        this.currentRound = 0;
        this.maxPlayers = 8;
    }

    public void updateLobbySettings(GameSettingsDTO settings){
        this.roundDuration = settings.getTimer();
        this.rounds = settings.getRounds();
        this.genre = settings.getGenre();
        this.maxPlayers = settings.getMaxPlayers();
    }

    public GameSettingsDTO getLobbySettings(){
        GameSettingsDTO settings = new GameSettingsDTO();

        settings.setAdmin(this.admin.getName());
        settings.setGenre(this.genre);
        settings.setMaxPlayers(this.maxPlayers);
        settings.setTimer(this.roundDuration);
        settings.setRounds(this.rounds);
        settings.setWordlist(this.wordList);

        return settings;
    }
}
