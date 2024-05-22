package ch.uzh.ifi.hase.soprafs24.entity;
import lombok.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Player {
    private String username;
    private int userId; //can be a userId or a guestId
    private boolean isGuest;
    private int gameId;
    private ArrayList<Integer> friends; //the userId's of their friends
    private String role; // "admin" or "player"

    private  int totalPoints;

    private  int  newlyEarnedPoints;

    private int podiumPosition;

    public  Player (String username, int userId, boolean isguest, int gameId, ArrayList<Integer> friends,String role) {
        this.username = username;
        this.userId = userId;
        this.isGuest = isguest;
        this.gameId = gameId;
        this.friends = friends;
        this.role = role;
        /*
        when the player is a geust:
        username = "guestplayer" + guestId
        userId = random negative guestid
        isGuest = false
        gameId = gameId
        friends = [] (I think)
        role = role
        */
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
    public void setIsGuest(boolean isGuest) {
        this.isGuest = isGuest;
    }
    public boolean getIsGuest() {
        return this.isGuest;
    }
}
