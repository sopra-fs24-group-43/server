package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
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

  @Column(nullable = false)
  private Boolean isUser;

  @Column
  private String password;

  @Column
  private int level;

  @Column
  private Date creation_date;

  @Column
  private String birth_date;

  @ElementCollection
  @Column
  private List<String> friends;

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

  public List<String> getFriends() {return friends;}

  public void setFriends(List<String> friends) {this.friends = friends; }

  public Date getCreation_date() { return creation_date; }

  public void setCreation_date(Date creation_date) { this.creation_date = creation_date; }

  public String getBirth_date() { return birth_date; }

  public void setBirth_date(String birth_date) { this.birth_date = birth_date; }

  public int getLevel() {return level;}
  public void setLevel(int level) {
        this.level = level;
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
