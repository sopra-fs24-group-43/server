package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.util.List;

public class UserPutDTO {
    private String name;

    private String username;

    private String token;

    private String password;

    private Long id;

    private String birth_date;

    private List<String> friends;

    private int level;

    private String creation_date;
    private UserStatus status;

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getCreation_date() {return creation_date;}

    public void setCreation_date(String creation_date) {this.creation_date = creation_date;}

    public int getLevel() {return level;}
    public void setLevel(int level) {this.level = level;}

    public List<String> getFriends() {return friends;}

    public void setFriends(List<String> friends) {this.friends = friends;}


    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public Long getId() {return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public String getBirth_date() {return birth_date;}

    public void setBirth_date(String birth_date) {this.birth_date = birth_date;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

