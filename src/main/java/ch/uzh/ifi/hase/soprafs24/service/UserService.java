package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User editProfile(Long id, User userInput) {

      User user = getUserById(id);
      if (userInput.getUsername()!=null&& !userInput.getUsername().equals(user.getUsername())){
          checkIfUserExists(userInput);
          user.setUsername(userInput.getUsername());
      }

      if (userInput.getBirth_date() != null ) {
          user.setBirth_date(userInput.getBirth_date());
      }
      if (userInput.getName()!=null){
          user.setName(userInput.getName());
      }

      if (userInput.getHotkeyInputDraw()!=null){
          user.setHotkeyInputDraw(userInput.getHotkeyInputDraw());
      }
      if (userInput.getHotkeyInputFill()!=null){
          user.setHotkeyInputFill(userInput.getHotkeyInputFill());
      }
      if (userInput.getHotkeyInputEraser()!=null){
          user.setHotkeyInputEraser(userInput.getHotkeyInputEraser());
      }
      if (userInput.getHotkeyInputClear()!=null){
          user.setHotkeyInputClear(userInput.getHotkeyInputClear());
      }


      userRepository.save(user);
      userRepository.flush();

      return user;

  }

  public User getUserById(Long userId) {

      User userById = userRepository.findUserById(userId);

      if (userById == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not correct or does not exist");
      }

      return userById;
  }

    public User getUserByUsername(String username) {

        User userByUsername = userRepository.findUserByUsername(username);

        if (userByUsername == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "username not correct or does not exist");
        }

        return userByUsername;
    }

  public User loginUser(User userToBeLoggedIn){

      User userInDB = userRepository.findByUsername(userToBeLoggedIn.getUsername());
      if(userInDB == null) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no user by this username exists");
      } else if (!userInDB.getPassword().equals(userToBeLoggedIn.getPassword())) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong password");
      }

      userInDB.setStatus(UserStatus.ONLINE);
      userRepository.save(userInDB);
      userRepository.flush();
      return userInDB;
  }

  public User logout(User user) {
      User userToLogOut = userRepository.findUserByToken(user.getToken());

      userToLogOut.setStatus(UserStatus.OFFLINE);
      userRepository.save(userToLogOut);
      userRepository.flush();
      return userToLogOut;

  }

  public User createUser(User newUser, Boolean isUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    newUser.setIsUser(isUser);
    if (isUser) {
        checkIfUserExists(newUser);}

    else {
        newUser.setName(UUID.randomUUID().toString());

    }
    newUser.setCreation_date(LocalDate.now());
    newUser.setLevel(1);
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

    public void deleteUser(User guest) {
        //check if not isUser?
        userRepository.delete(guest);
        userRepository.flush();
    }

  public List<String> getFriends(Long id) {
      User userById = this.userRepository.findUserById(id);
      return userById.getFriends();
    }
  public User add_or_delete_Friend(User user, String f_username, Boolean b) {//should only delete
      if (b) {
          User userByUsername = userRepository.findByUsername(f_username);
          if (userByUsername==null) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User does not exist");}
          List<String> friends = user.getFriends();
          friends.add(f_username);
          user.setFriends(friends);
          return user;}

      else {
          User userByUsername = userRepository.findByUsername(f_username);
          if (userByUsername==null) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User does not exist");}
          List<String> friends = user.getFriends();
          friends.remove(f_username);
          user.setFriends(friends);
          return user;}
  }
    public void sendFriendRequest(User user, String friend_username) {
        User friend = userRepository.findByUsername(friend_username);
        List<String> friendRequests = friend.getOpenFriendRequests();
        friendRequests.add(user.getUsername());
        friend.setOpenFriendRequests(friendRequests);
    }

    public List<String> getOpenFriendRequests(Long id) {
        User userById = this.userRepository.findUserById(id);
        return userById.getOpenFriendRequests();
    }

    public User accept_or_deny_Friend_Request (User user, String friend_username, Boolean b){
        if (b) {
            List<String> friends = user.getFriends();
            System.out.println(friends);
            friends.add(friend_username);
            System.out.println(friends);
            user.setFriends(friends);
            return user;}

        else {
            return user;}
    }


  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByName = userRepository.findByName(userToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null && userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username and the name", "are"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    } else if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
    }
  }
}
