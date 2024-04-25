package ch.uzh.ifi.hase.soprafs24.entity;
import java.lang.reflect.Array;
import java.util.ArrayList;
public class Player {
    private String username;
    private int userId;
    private int gameId;
    private ArrayList<Integer> friends; //the userId's of their friends
    private String role; // "admin" or "player"

    public  Player (String username, int userId, int gameId, ArrayList<Integer> friends,String role) {
        this.username = username;
        this.userId = userId;
        this.gameId = gameId;
        this.friends = friends;
        this.role = role;
    }

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
