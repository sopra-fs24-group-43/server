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
  private String hotkeyInputDraw;
  private String hotkeyInputFill;
  private String hotkeyInputEraser;
  private String hotkeyInputClear;

  private Long id;
  private String name;
  private String username;
  private String password;
  private UserStatus status;

  private Boolean isUser;

  private String token;

  private int level;

  private int xp;

  private LocalDate creation_date;

  private String birth_date;

  private List<String> friends;

  private List<String> openFriendRequests;
  private List<String> sentFriendRequests;

}
