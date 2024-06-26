package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column
  private Boolean isUser;

  @Column
  private String password;

  @Column
  private int level;

  @Column
  private int xp;

  @Column
  private LocalDate creation_date;

  @Column
  private String birth_date;

  @ElementCollection
  @Column
  private List<String> friends;

  @ElementCollection
  @Column
  private List<String> openFriendRequests;

    @ElementCollection
    @Column
    private List<String> sentFriendRequests;

    @Getter
    @Setter
    private String hotkeyInputDraw = "D";
    @Getter
    @Setter
    private String hotkeyInputFill = "F";
    @Getter
    @Setter
    private String hotkeyInputEraser = "E";
    @Getter
    @Setter
    private String hotkeyInputClear = "C";


    public List<String> getOpenFriendRequests() {return openFriendRequests;}
  public void setOpenFriendRequests(List<String> openFriendRequests) {this.openFriendRequests = openFriendRequests; }

    public List<String> getSentFriendRequests() {return sentFriendRequests;}
    public void setSentFriendRequests(List<String> sentFriendRequests) {this.sentFriendRequests = sentFriendRequests; }

  public Boolean getIsUser() {
        return isUser;
    }

  public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

  public List<String> getFriends() {return friends;}

  public void setFriends(List<String> friends) {this.friends = friends; }

  public LocalDate getCreation_date() { return creation_date; }

  public void setCreation_date(LocalDate creation_date) { this.creation_date = creation_date; }

  public String getBirth_date() { return birth_date; }

  public void setBirth_date(String birth_date) { this.birth_date = birth_date; }

  public int getLevel() {return level;}
  public void setLevel(int level) {
        this.level = level;
    }

  public int getXp(){
        return xp;
  }
  public void setXp(int xp){
        this.xp = xp;
  }

  public String getPassword() {return password;}
  public void setPassword(String password) {
        this.password = password;
    }

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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }
}
