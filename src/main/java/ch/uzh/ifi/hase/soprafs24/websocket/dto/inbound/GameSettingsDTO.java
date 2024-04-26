package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

import java.util.ArrayList;

public class GameSettingsDTO {
    private String type;

    private int maxPlayers;
    private int maxRounds;
    private int turnLength;
    private String gamePassword;
    private String lobbyName;

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }
    public int getMaxRounds() {
        return this.maxRounds;
    }
    public void setTurnLength(int turnLength) {
        this.turnLength = turnLength;
    }
    public int getTurnLength() {
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