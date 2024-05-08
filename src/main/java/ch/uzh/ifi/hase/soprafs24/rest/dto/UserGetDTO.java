package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserGetDTO {


  private Long id;
  private String name;
  private String username;
  private UserStatus status;

  private Boolean isUser;


  private String token;

  private int level;


  private LocalDate creation_date;

  private String birth_date;

  private List<String> friends;
/*
  public Boolean getIsUser() {
        return isUser;
    }

  public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

  public List<String> getFriends() {return friends;}

  public void setFriends(List<String> friends) {this.friends = friends;}

  //public void setCreation_date(LocalDate creation_date) {this.creation_date = creation_date;}

  public String getBirth_date() {return birth_date;}

  public void setBirth_date(String birth_date) {this.birth_date = birth_date;}

  public void setToken(String token) {this.token = token;}

  public int getLevel(int level) {return level;}

  public void setLevel(int level) {this.level = level;}

    //public void setId(Long id) {
    //this.id = id;
  //}

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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }*/
}
