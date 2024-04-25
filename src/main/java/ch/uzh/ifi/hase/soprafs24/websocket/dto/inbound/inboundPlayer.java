package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

import ch.uzh.ifi.hase.soprafs24.entity.Player;

import java.util.ArrayList;

public class inboundPlayer {
    private String username;
    private int userId;
    private int gameId;
    private ArrayList<Integer> friends; //the userId's of their friends
    private String role; // "admin" or "player"


    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return this.username;
    }
    public void setuserId(int userId) {
        this.userId = userId;
    }
    public int getUserId() {
        return this.userId;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    public int getGameId() {
        return this.gameId;
    }
    public void setFriends(ArrayList<Integer> friends) {
        this.friends = friends;
    }
    public ArrayList<Integer> getFriends() {
        return this.friends;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return this.role;
    }
}
