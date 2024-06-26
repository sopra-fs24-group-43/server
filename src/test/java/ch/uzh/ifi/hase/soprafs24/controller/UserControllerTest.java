package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.service.GlobalLeaderboardService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private User user;
  @Mock
  private User user2;

  @MockBean
  private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GlobalLeaderboardService globalLeaderboardService;

  @BeforeEach
  public void setup() {
      user = new User();
      user.setIsUser(true);
      user.setName("user");
      user.setId(1L);
      user.setUsername("user1");
      user.setToken("1");
      user.setBirth_date("01.01.2000");
      //Date date = new Date(2024,Calendar.JANUARY,1);
      user.setCreation_date(LocalDate.now());
      List<String> friends = new ArrayList<String>();
      friends.add("user2");
      //friends.add("3");
      user.setFriends(friends);
      user.setLevel(1);
      user.setXp(1);
      user.setPassword("1");
      user.setStatus(UserStatus.ONLINE);
      List<String> empty = new ArrayList<>();
      user.setOpenFriendRequests(empty);
      user.setSentFriendRequests(empty);

      user2 = new User();
      user2.setIsUser(true);
      user2.setName("user2");
      user2.setId(2L);
      user2.setUsername("user2");
      user2.setToken("2");
      user2.setBirth_date("01.01.2000");
      //Date date = new Date(2024,Calendar.JANUARY,1);
      user2.setCreation_date(LocalDate.now());
      List<String> friends2 = new ArrayList<String>();
      friends2.add("user1");
      //friends.add("3");
      user2.setFriends(friends);
      user2.setLevel(1);
      user2.setPassword("1");
      List<String> empty2 = new ArrayList<>();
      user2.setOpenFriendRequests(empty2);
      user2.setSentFriendRequests(empty2);
  }

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {


    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is(user.getName())))
        .andExpect(jsonPath("$[0].creation_date", is(user.getCreation_date().toString())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())));
  }


  @Test
  public void createUser_validInput_userCreated() throws Exception {


    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("user2");
    userPostDTO.setUsername("user2");

    given(userService.createUser(Mockito.any(),eq(true))).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.name", is(user.getName())))
        .andExpect(jsonPath("$.creation_date", is(user.getCreation_date().toString())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

    @Test
    public void test_create_user_invalid_input() throws Exception {
        given(userService.createUser(Mockito.any(),eq(true))).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("user");
        userPostDTO.setUsername("user1");
        userPostDTO.setPassword("1");

        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void test_user_login() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("user1");
        userPostDTO.setPassword("1");

        given(userService.loginUser(Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.creation_date", is(user.getCreation_date().toString())));
    }

    @Test
    public void test_user_logout() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("user1");


        given(userService.logout(Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void deleteUserTest() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("user1");


        doNothing().when(userService).deleteUser(Mockito.any());

        MockHttpServletRequestBuilder deleteRequest = delete("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(deleteRequest).andExpect(status().isNoContent());

    }

    @Test
    public void test_user_login_invalid_credentials() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("user1");
        userPostDTO.setPassword("3");

        given(userService.loginUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_get_single_user() throws Exception {
        long userId = user.getId();


        given(userService.getUserById(userId)).willReturn(user);

        MockHttpServletRequestBuilder getRequest = get("/users/" + userId).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.creation_date", is(user.getCreation_date().toString())))
                .andExpect(jsonPath("$.level", is(user.getLevel())));
    }

    @Test
    public void test_get_single_user_not_found() throws Exception {
      long userId = 0L;
      given(userService.getUserById(userId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

      MockHttpServletRequestBuilder getRequest = get("/users/" + userId).contentType(MediaType.APPLICATION_JSON);

      mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }


    @Test
    public void test_edit_profile_valid_input() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("newUsername");
        userPutDTO.setName("newUsername");
        userPutDTO.setBirth_date("01/01/2002");

        long userId = user.getId();

        given(userService.editProfile(eq(1L),Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.birth_date", is(user.getBirth_date())));

    }

    @Test
    public void test_edit_profile_invalid_input() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("user2");
        userPutDTO.setName("user2");

        given(userService.editProfile(eq(2L), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        MockHttpServletRequestBuilder putRequest = put("/users/" + 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void test_update_points() throws Exception {
        given(userService.createUser(Mockito.any(), eq(true))).willReturn(user);
        given(userService.getUserById(Mockito.any())).willReturn(user);
        given(userRepository.findByUsername(Mockito.any())).willReturn(user);
        given(userRepository.findUserById(Mockito.any())).willReturn(user);

        long userId = user.getId();

        doNothing().when(userService).updateUserPoints(Mockito.any(),Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/users/" + userId + "/updatePoints")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform((putRequest)
                        .param("points","10"))
                .andExpect(status().isOk());
        verify(userService).updateUserPoints(1L, 10);
    }


    @Test
    public void sentFriendRequestTest() throws Exception{
        given(userService.createUser(eq(user), eq(true))).willReturn(user);
        given(userService.createUser(eq(user2), eq(true))).willReturn(user2);
        given(userService.getUserById(Mockito.any())).willReturn(user);
        given(userRepository.findByUsername(eq("user"))).willReturn(user2);
        given(userRepository.findByUsername(eq("user2"))).willReturn(user);
        doNothing().when(userService).sendFriendRequest(Mockito.any(),eq("user2"),eq(true));


        MockHttpServletRequestBuilder postRequest = post("/users/" + user.getId() + "/openfriendrequests")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform((postRequest)
                        .param("friend_username","user2")
                        .param("delete","false"))
                .andExpect(status().isCreated());
        verify(userService).sendFriendRequest(user, "user2",false);
    }

    @Test
    public void getAllFriendRequestsTest() throws Exception{
        given(userService.createUser(Mockito.any(), eq(true))).willReturn(user);

        long userId = user.getId();

        given(userService.getUserById(eq(1L))).willReturn(user);
        List<String> l1 = new ArrayList<>();
        l1.add("2");
        given(userService.getUserByUsername(Mockito.any())).willReturn(user2);
        given(userService.getOpenFriendRequests(Mockito.any())).willReturn(l1);

        MockHttpServletRequestBuilder getRequest = get("/users/" + userId + "/openfriendrequests")
                .contentType(MediaType.APPLICATION_JSON);
        System.out.println(user.getOpenFriendRequests());
        mockMvc.perform(getRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user2.getUsername())))
                .andExpect(jsonPath("$[0].openFriendRequests", is(user.getOpenFriendRequests())))
                .andExpect(jsonPath("$[0].sentFriendRequests", is(user.getSentFriendRequests())));

    }

        @Test
        public void getFriendsTest() throws Exception{
            given(userService.createUser(Mockito.any(), eq(true))).willReturn(user);

            long userId = user.getId();

            given(userService.getUserById(eq(1L))).willReturn(user);
            List<String> l1 = new ArrayList<>();
            l1.add("user2");
            given(userService.getOpenFriendRequests(userId)).willReturn(l1);
            given(userService.getUserByUsername(Mockito.any())).willReturn(user2);
            given(userService.getSentFriendRequests(Mockito.any())).willReturn(l1);

            List<String> f2 = new ArrayList<>();
            f2.add("user2");

            given(userService.getFriends(Mockito.any())).willReturn(f2);

            MockHttpServletRequestBuilder getRequest = get("/users/" + userId + "/friends")
                    .contentType(MediaType.APPLICATION_JSON);
            mockMvc.perform(getRequest)

                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$[0].username", is("user2")))
                    .andExpect(jsonPath("$[0].friends", is(f2)));
        }

        @Test
        public void DeleteFriendOfUserByIDTest() throws Exception{
            given(userService.createUser(eq(user), eq(true))).willReturn(user);
            given(userService.createUser(eq(user2), eq(true))).willReturn(user2);

            long userId = user.getId();

            given(userService.getUserById(Mockito.any())).willReturn(user);
            given(userService.getUserByUsername(Mockito.any())).willReturn(user2);
            List<String> f2 = new ArrayList<>();
            f2.add("user2");
            List<String> f3 = new ArrayList<>();
            given(userService.getFriends(Mockito.any())).willReturn(f2);

            doNothing().when(userService).delete_Friend(Mockito.any(),Mockito.any());

            MockHttpServletRequestBuilder putRequest = put("/users/" + userId + "/friends")
                    .contentType(MediaType.APPLICATION_JSON);
            mockMvc.perform((putRequest)
                    .param("friend_username","user2")
                    .param("delete","true"))
                    .andExpect(status().isCreated());
            verify(userService).delete_Friend(user, "user2");
        }

    @Test
    public void getAllSentFriendRequestsTest() throws Exception{
        given(userService.createUser(Mockito.any(), eq(true))).willReturn(user);

        long userId = user.getId();

        given(userService.getUserById(eq(1L))).willReturn(user);
        List<String> l1 = new ArrayList<>();
        l1.add("user2");

        given(userService.getUserByUsername(Mockito.any())).willReturn(user2);

        given(userService.getSentFriendRequests(Mockito.any())).willReturn(l1);

        MockHttpServletRequestBuilder getRequest = get("/users/" + userId + "/sentfriendrequests")
                .contentType(MediaType.APPLICATION_JSON);
        System.out.println(user.getSentFriendRequests());
        mockMvc.perform(getRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user2.getUsername())))
                .andExpect(jsonPath("$[0].openFriendRequests", is(user.getOpenFriendRequests())))
                .andExpect(jsonPath("$[0].sentFriendRequests", is(user.getSentFriendRequests())));

        System.out.println(user.getSentFriendRequests());
    }

    @Test
    public void acceptOrDenyFriendRequestsTest() throws Exception{
        given(userService.createUser(Mockito.any(), eq(true))).willReturn(user);

        long userId = user.getId();


        given(userService.getUserById(Mockito.any())).willReturn(user);
        List<String> l1 = new ArrayList<>();
        List<String> empty = new ArrayList<>();
        l1.add("user2");
        given(userService.getUserByUsername(Mockito.any())).willReturn(user2);
        given(userService.getUserById(Mockito.any())).willReturn(user);
        given(userService.getOpenFriendRequests(Mockito.any())).willReturn(l1);
        given(userService.getSentFriendRequests(Mockito.any())).willReturn(empty);
        given(userService.accept_or_deny_Friend_Request_receiver(Mockito.any(),Mockito.any(),eq(true))).willReturn(user);
        doNothing().when(userService).accept_or_deny_Friend_Request_sender(Mockito.any(),Mockito.any(),eq(true));

        MockHttpServletRequestBuilder putRequest = put("/users/" + userId + "/openfriendrequests")
                .contentType(MediaType.APPLICATION_JSON);
        System.out.println(user.getOpenFriendRequests());
        mockMvc.perform((putRequest)
                .param("friend_username","user2")
                .param("false_for_deny_true_for_accept","true"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.friends", is(l1)))
                .andExpect(jsonPath("$.openFriendRequests", is(user.getOpenFriendRequests())))
                .andExpect(jsonPath("$.openFriendRequests", is(empty)))
                .andExpect(jsonPath("$.sentFriendRequests", is(empty)))
                .andExpect(jsonPath("$.sentFriendRequests", is(user.getSentFriendRequests())));

        System.out.println(user.getSentFriendRequests());
    }


    /**
         * Helper Method to convert userPostDTO into a JSON string such that the input
         * can be processed
         * Input will look like this: {"name": "Test User", "username": "testUsername"}
         *
         * @param object
         * @return string
         */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}