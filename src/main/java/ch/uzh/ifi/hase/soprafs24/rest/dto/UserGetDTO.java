package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserGetDTO {

  private Long id;
  private String name;
  private String username;
  private UserStatus status;

  private String token;

  private int level;

  private String creation_date;

  private String birth_date;

  private List<String> friends;

  public List<String> getFriends() {return friends;}

  public void setFriends(List<String> friends) {this.friends = friends;}

  public String getToken() {return token;}

  public String getCreation_date() {return creation_date;}

  public void setCreation_date(String creation_date) {this.creation_date = creation_date;}

  public String getBirth_date() {return birth_date;}

  public void setBirth_date(String birth_date) {this.birth_date = birth_date;}

  public void setToken(String token) {this.token = token;}

  public int getLevel(int level) {return level;}

  public void setLevel(int level) {this.level = level;}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }
}
