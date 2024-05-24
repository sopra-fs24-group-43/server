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

      if (userInput.getPassword()!=null){
        user.setPassword(userInput.getPassword());
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
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setIsUser(isUser);
    if (isUser) {
        checkIfUserExists(newUser);}

    else {
        newUser.setName(UUID.randomUUID().toString());

    }
    newUser.setCreation_date(LocalDate.now());
    newUser.setLevel(1);
    newUser.setXp(0);
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

    public void deleteUser(User guest) {

        userRepository.delete(guest);
        userRepository.flush();
    }

  public List<String> getFriends(Long id) {
      User userById = this.userRepository.findUserById(id);
      return userById.getFriends();
    }
  public void delete_Friend(User user, String f_username) {

          User userByUsername = userRepository.findByUsername(f_username);
          if (userByUsername==null) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User does not exist");}
          List<String> friends = user.getFriends();
          friends.remove(f_username);
          user.setFriends(friends);


          List<String> friends2 = userByUsername.getFriends();
          friends2.remove(user.getUsername());
          userByUsername.setFriends(friends2);

  }
    public void sendFriendRequest(User user, String friend_username, Boolean d) {
      if (!d){
        User friend = userRepository.findByUsername(friend_username);
        List<String> requests = user.getSentFriendRequests();
        if (!requests.contains(friend_username)) {
            List<String> friendRequests = friend.getOpenFriendRequests();
            friendRequests.add(user.getUsername());
            friend.setOpenFriendRequests(friendRequests);

            List<String> sentFriendRequests = user.getSentFriendRequests();
            sentFriendRequests.add(friend_username);
            user.setSentFriendRequests(sentFriendRequests);}
      } else {
          User friend = userRepository.findByUsername(friend_username);
          List<String> friendRequests = friend.getOpenFriendRequests();
          friendRequests.remove(user.getUsername());
          friend.setOpenFriendRequests(friendRequests);

          List<String> sentFriendRequests = user.getSentFriendRequests();
          sentFriendRequests.remove(friend_username);
          user.setSentFriendRequests(sentFriendRequests);}

    }

    public List<String> getOpenFriendRequests(Long id) {
        User userById = this.userRepository.findUserById(id);
        return userById.getOpenFriendRequests();
    }

    public List<String> getSentFriendRequests(Long id) {
      User userById = this.userRepository.findUserById(id);
        return userById.getSentFriendRequests();
    }

    public User accept_or_deny_Friend_Request_receiver (User user, String friend_username, Boolean b){
        if (b) {
            List<String> friends = user.getFriends();
            friends.add(friend_username);
            user.setFriends(friends);

            List<String> friendsRequests = user.getOpenFriendRequests();
            friendsRequests.remove(friend_username);
            user.setOpenFriendRequests(friendsRequests);

            return user;}

        else {
            List<String> friendsRequests = user.getOpenFriendRequests();
            friendsRequests.remove(friend_username);
            user.setOpenFriendRequests(friendsRequests);
            return user;}
    }

    public void accept_or_deny_Friend_Request_sender (User user, String friend_username, Boolean b){
        User user2 = this.userRepository.findByUsername(friend_username);
        if (b) {
            List<String> friends2 = user2.getFriends();
            friends2.add(user.getUsername());
            user2.setFriends(friends2);

            List<String> sentFriendRequests = user2.getSentFriendRequests();
            sentFriendRequests.remove(user.getUsername());
            user2.setSentFriendRequests(sentFriendRequests);
            }
        else {
            List<String> sentFriendRequests = user2.getSentFriendRequests();
            sentFriendRequests.remove(user.getUsername());
            user2.setSentFriendRequests(sentFriendRequests);
            }
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

  public void updateUserPoints(Long id, Integer points) {
      User user = userRepository.findUserById(id);
      if(user != null) {
          int curXP = user.getXp();
          curXP += points;
          user.setXp(curXP);
          user.setLevel((int) Math.ceil((double) curXP / (double) 3000));
          userRepository.save(user);
          userRepository.flush();
      }
  }
}
