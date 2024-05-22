package ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound;

import lombok.*;

import java.util.ArrayList;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class InboundPlayer {

    //type is normally InboundPlayer but when returning for createguestplayer then its createPlayerFromGuest
    private String type;
    private String username;
    private int userId;
    private boolean isGuest;
    private int gameId;
    private ArrayList<Integer> friends; //the userId's of their friends
    private String role; // "admin" or "player"

    //if you leave out a set/get method for a field the websocket wont be able to send that field back!!!
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }
    public void setIsGuest(boolean isguest) {
        this.isGuest = isguest;
    }
    public boolean getIsGuest() {
        return this.isGuest;
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
