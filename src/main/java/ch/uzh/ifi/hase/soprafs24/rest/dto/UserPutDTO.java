package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class UserPutDTO {
    private String hotkeyInputDraw;
    private String hotkeyInputFill;
    private String hotkeyInputEraser;
    private String hotkeyInputClear;

    private String name;

    private String username;

    private String token;

    private String password;

    private Long id;

    private String birth_date;

    private List<String> friends;

    private int level;

    private Boolean isUser;

    private LocalDate creation_date;
    private UserStatus status;
    private List<String> openFriendRequests;
    private List<String> sentFriendRequests;
/*
    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDate getCreation_date() {return creation_date;}

    public void setCreation_date(LocalDate creation_date) {this.creation_date = creation_date;}

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

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
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
    }*/
}

