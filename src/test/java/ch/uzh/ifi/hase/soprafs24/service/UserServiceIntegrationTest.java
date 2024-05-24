package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  public void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setName("testName");
    testUser.setUsername("testUsername");

    // when
    User createdUser = userService.createUser(testUser, true);
    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }


  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setName("testName");
    testUser.setUsername("testUsername");
    User createdUser = userService.createUser(testUser,true);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setName("testName2");
    testUser2.setUsername("testUsername");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2, true));
  }

  @Test
    public void loginLogoutUserTest() {
      assertNull(userRepository.findByUsername("testUsername"));
      User testUser = new User();
      testUser.setName("testName");
      testUser.setUsername("testUsername");
      testUser.setPassword("testUsername");
      User createdUser = userService.createUser(testUser,true);

      User loggedOutUser = userService.logout(createdUser);
      assertEquals(loggedOutUser.getStatus(),UserStatus.OFFLINE);
      assertEquals(loggedOutUser.getUsername(),testUser.getUsername());

      User loggedInUser = userService.loginUser(createdUser);
      assertEquals(testUser.getId(), loggedInUser.getId());
      assertEquals(testUser.getName(), loggedInUser.getName());
      assertEquals(testUser.getUsername(), loggedInUser.getUsername());
      assertNotNull(loggedInUser.getToken());
      assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
  }

    @Test
    public void sendFriendRequestsTest() {
        assertNull(userRepository.findByUsername("testUsername"));
        assertNull(userRepository.findByUsername("testUsername2"));
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        List<String> r = new ArrayList<>();
        testUser.setSentFriendRequests(r);

        User createdUser = userService.createUser(testUser,true);


        User testUser2 = new User();
        testUser2.setName("testName2");
        testUser2.setUsername("testUsername2");
        List<String> o = new ArrayList<>();
        testUser.setSentFriendRequests(r);
        testUser2.setOpenFriendRequests(o);

        User createdUser2 = userService.createUser(testUser2,true);

        userService.sendFriendRequest(createdUser,"testUsername2",false);



        List<String> friendRequests = new ArrayList<>();
        friendRequests.add(testUser2.getUsername());
        List<String> openRequests = new ArrayList<>();
        openRequests.add(testUser.getUsername());
        assertEquals(createdUser.getSentFriendRequests(),friendRequests);
        //assertEquals(createdUser2.getOpenFriendRequests(),openRequests);

        userService.sendFriendRequest(createdUser,"testUsername2", true);
        List<String> empty = new ArrayList<>();
        assertEquals(empty,createdUser.getSentFriendRequests());
        assertEquals(empty,createdUser2.getOpenFriendRequests());
    }

    @Test
    public void accept_or_deny_Friend_Request_and_delete_Test() {
        assertNull(userRepository.findByUsername("testUsername"));
        assertNull(userRepository.findByUsername("testUsername2"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        List<String> r = new ArrayList<>();
        testUser.setSentFriendRequests(r);
        List<String> f = new ArrayList<>();
        testUser.setFriends(f);

        User createdUser = userService.createUser(testUser,true);
        User testUser2 = new User();
        testUser2.setName("testName2");
        testUser2.setUsername("testUsername2");
        List<String> o = new ArrayList<>();
        testUser.setSentFriendRequests(r);
        testUser2.setOpenFriendRequests(o);
        List<String> f2 = new ArrayList<>();
        testUser2.setFriends(f2);
        testUser.setFriends(f);

        User createdUser2 = userService.createUser(testUser2,true);

        userService.accept_or_deny_Friend_Request_receiver(createdUser2,"testUsername", true);
        userService.accept_or_deny_Friend_Request_sender(createdUser,"testUsername2",true);

        List<String> empty = new ArrayList<>();
        assertEquals(empty,createdUser.getSentFriendRequests());
        assertEquals(empty,createdUser2.getOpenFriendRequests());

        List<String> friends1 = new ArrayList<>();
        friends1.add(testUser2.getUsername());
        List<String> friends2 = new ArrayList<>();
        friends2.add(testUser.getUsername());
        //assertEquals(createdUser.getFriends(),friends1);
        assertEquals(createdUser2.getFriends().get(0),friends2.get(0));

        userService.delete_Friend(createdUser,"testUsername2");

        assertEquals(empty,createdUser.getFriends());
        //assertEquals(empty,createdUser2.getFriends());

        userService.deleteUser(createdUser);
        assertNull(userRepository.findByUsername("testUsername"));
    }
    }
