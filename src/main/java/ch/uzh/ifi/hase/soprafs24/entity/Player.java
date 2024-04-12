package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private List<User> friends;

    @Column(nullable = false)
    private int level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void addFriend(Player friend){
        this.friends.add(friend);
    }

    public List<Player> getFriends(){
        return friends;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}