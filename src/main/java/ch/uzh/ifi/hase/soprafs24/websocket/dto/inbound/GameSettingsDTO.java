package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

import java.util.ArrayList;
import java.util.List;

public class GameSettingsDTO { //maxRounds, max Players, turnLength
    private String type; //gameSettings

    private Integer maxPlayers;
    private Integer maxRounds;
    private Integer turnLength;
    private String gamePassword;
    private String lobbyName;
    private List<String> genres;
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    public List<String> getGenres() {
        return this.genres;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    public Integer getMaxPlayers() {
        return this.maxPlayers;
    }
    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }
    public Integer getMaxRounds() {
        return this.maxRounds;
    }
    public void setTurnLength(int turnLength) {
        this.turnLength = turnLength;
    }
    public Integer getTurnLength() {
        return this.turnLength;
    }
    public void setGamePassword(String gamePassword) {
        this.gamePassword = gamePassword;
    }
    public String getGamePassword() {
        return this.gamePassword;
    }
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }
    public String getLobbyName() {
        return this.lobbyName;
    }

}