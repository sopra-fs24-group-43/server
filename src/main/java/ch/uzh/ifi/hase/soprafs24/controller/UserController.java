package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;

import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @PutMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO loginUser(@RequestBody UserPutDTO userPutDTO) {

      User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
      User userInDB = userService.loginUser(userInput);
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userInDB);
  }

  @PutMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO logoutUser(@RequestBody UserPutDTO userPutDTO) {
      User userToLogOut = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
      User userInDB = userService.logout(userToLogOut);
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userInDB);

    }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO, @RequestParam(defaultValue = "true") Boolean isUser) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput, isUser);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

    @DeleteMapping("/users") //delete guest
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteUser(@RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        userService.deleteUser(userInput);

    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserByID(@PathVariable Long id) {

        User user = userService.getUserById(id);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public UserGetDTO editProfile(@PathVariable Long id, @RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User editedUser = userService.editProfile(id, userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(editedUser);
    }

    @GetMapping("/users/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllFriendsOfUserByID(@PathVariable Long id) {
        List<String> friends = userService.getFriends(id);
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        for (String friend : friends) {
            User friendByUsername = userService.getUserByUsername(friend);
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(friendByUsername));
        }
        return userGetDTOs;
    }
    @PutMapping("/users/{id}/friends")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void DeleteFriendOfUserByID (@PathVariable Long id, @RequestParam String friend_username) {
        User user = userService.getUserById(id);
        userService.delete_Friend(user, friend_username);

    }


    @PostMapping("/users/{id}/openfriendrequests")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void sendFriendRequest (@PathVariable Long id, @RequestParam String friend_username, @RequestParam Boolean delete) {
        User user = userService.getUserById(id);
        userService.sendFriendRequest(user, friend_username, delete);
    }
    @GetMapping("/users/{id}/openfriendrequests")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllFriendRequestsOfUserByID(@PathVariable Long id) {
        List<String> friendRequests = userService.getOpenFriendRequests(id);
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        for (String friend : friendRequests) {
            User friendByUsername = userService.getUserByUsername(friend);
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(friendByUsername));
        }
        return userGetDTOs;
    }
    @PutMapping("/users/{id}/openfriendrequests")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO acceptOrDenyFriendRequest (@PathVariable Long id, @RequestParam String friend_username, @RequestParam Boolean false_for_deny_true_for_accept) {
        User user = userService.getUserById(id);
        User editedUser = userService.accept_or_deny_Friend_Request_receiver(user, friend_username, false_for_deny_true_for_accept);
        userService.accept_or_deny_Friend_Request_sender(user,friend_username, false_for_deny_true_for_accept);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(editedUser);
    }

    @GetMapping("/users/{id}/sentfriendrequests")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllSentFriendRequestsOfUserByID(@PathVariable Long id) {
        List<String> friendRequests = userService.getSentFriendRequests(id);
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        for (String friend : friendRequests) {
            User friendByUsername = userService.getUserByUsername(friend);
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(friendByUsername));
        }
        return userGetDTOs;
    }

    @PutMapping("users/{id}/updatePoints")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updatePoints(@PathVariable Long id, @RequestParam Integer points){
      userService.updateUserPoints(id, points);
    }
}
