package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
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

  @BeforeEach
  public void setup() {
      user = new User();
      user.setIsUser(true);
      user.setName("1");
      user.setId(1L);
      user.setUsername("1");
      user.setToken("1");
      user.setBirth_date("01.01.2000");
      //Date date = new Date(2024,Calendar.JANUARY,1);
      user.setCreation_date(LocalDate.now());
      List<String> friends = new ArrayList<String>();
      friends.add("user2");
      //friends.add("3");
      //user.setFriends(friends);
      user.setLevel(1);
      user.setPassword("1");
      user.setStatus(UserStatus.ONLINE);

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
      friends2.add("1");
      //friends.add("3");
      //user2.setFriends(friends);
      user2.setLevel(1);
      user2.setPassword("1");
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

    given(userService.createUser(Mockito.any(),eq(true))).willReturn(user2);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user2.getId().intValue())))
        .andExpect(jsonPath("$.name", is(user2.getName())))
        .andExpect(jsonPath("$.creation_date", is(user2.getCreation_date().toString())))
        .andExpect(jsonPath("$.username", is(user2.getUsername())));
        //.andExpect(jsonPath("$.status", is(user2.getStatus().toString())));
  }

    @Test
    public void test_create_user_invalid_input() throws Exception {
        given(userService.createUser(Mockito.any(),eq(true))).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("1");
        userPostDTO.setUsername("1");
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
        userPostDTO.setUsername("1");
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
        userPostDTO.setUsername("1");


        given(userService.logout(Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }
    /*//todo
    @Test
    public void delete_guest_user() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("user1");


        given(userService.logout(Mockito.any()));

        MockHttpServletRequestBuilder putRequest = delete("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());

    }
    */
    @Test
    public void test_user_login_invalid_credentials() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("1");
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
      long userId = 4L;
      given(userService.getUserById(userId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

      MockHttpServletRequestBuilder getRequest = get("/users/" + userId).contentType(MediaType.APPLICATION_JSON);

      mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }


    @Test
    public void test_edit_profile_valid_input() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("user");
        userPutDTO.setName("user");
        userPutDTO.setBirth_date("01/01/2002");

        long userId = user.getId();

        given(userService.editProfile(eq(1L),Mockito.any())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/users/" + 1L)
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

        given(userService.editProfile(eq(3L), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        MockHttpServletRequestBuilder putRequest = put("/users/" + 3L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void sentFriendRequestTest() throws Exception{

        doNothing().when(userService).sendFriendRequest(Mockito.any(),eq("user2"),eq(true));

        MockHttpServletRequestBuilder postRequest = post("/users/" + user.getId() + "/openfriendrequests")
                .contentType(MediaType.APPLICATION_JSON);
               // .param("f_username", f_username)
                //.param("delete", delete);
        mockMvc.perform((postRequest)
                .param("friend_username","user2")
                .param("delete","false"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());

        System.out.println(user.getSentFriendRequests());
        System.out.println(user2.getOpenFriendRequests());
        System.out.println(user.getUsername());
    }
/*
    @Test
    public void getFriendsTest() throws Exception{

        given(userService.getFriends(eq(1L))).willReturn(user.getFriends());

        MockHttpServletRequestBuilder getRequest = get("/users/" + 1L + "/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(1L));
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friends", is(user.getFriends())));
    }

    @Test
    public void DeleteFriendOfUserByIDTest() throws Exception{
        doNothing().when(userService).delete_Friend(Mockito.any(),eq("2"));

        MockHttpServletRequestBuilder putRequest = put("/users/" + 1L + "/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(1L));
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

    }
*/
    /*//TODO!
    @Test
    public void test_add_friends() throws Exception {

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.add_or_delete_Friend(Mockito.any(),eq("user2"),eq(true))).willReturn(user);

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId() + "/friends").contentType(MediaType.APPLICATION_JSON)
                .param("f_username", "user2")
                .param("false_for_delete_true_for_add", "true");


        // then
        mockMvc.perform(putRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.friends", hasSize(3)))
                .andExpect(jsonPath("$.friends", is(user.getFriends())));
    }


    @Test
    public void test_get_friends() throws Exception {

        // String friend_3 = user.getFriends();
        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getFriends(eq(1L))).willReturn(user.getFriends());

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId() + "/friends").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                //.andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.friends.[0].username", is(user.getFriends().get(0))))
                .andExpect(jsonPath("$[0]", is(user.getFriends())));
    }
//test_delete_friends

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