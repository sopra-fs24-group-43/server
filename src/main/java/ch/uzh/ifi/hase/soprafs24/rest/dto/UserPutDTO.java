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

    private int xp;

    private Boolean isUser;

    private LocalDate creation_date;
    private UserStatus status;
    private List<String> openFriendRequests;
    private List<String> sentFriendRequests;

}

