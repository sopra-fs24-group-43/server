package ch.uzh.ifi.hase.soprafs24.controller.GlobalLeaderBoardControllerTest;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.controller.GlobalLeaderboardController;
import ch.uzh.ifi.hase.soprafs24.controller.UserController;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GlobalLeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import ch.uzh.ifi.hase.soprafs24.service.GlobalLeaderboardService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalLeaderboardController.class)
public class GlobalLeaderBoardControllerTest {
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
        user2.setLevel(2);
        user.setXp(5);
        user2.setPassword("1");
        List<String> empty2 = new ArrayList<>();
        user2.setOpenFriendRequests(empty2);
        user2.setSentFriendRequests(empty2);
    }
    @Test
    public void test_GlobalLeaderBoardController() throws Exception {
        given(userService.createUser(Mockito.any(), eq(true))).willReturn(user);
        given(userService.getUserById(eq(1L))).willReturn(user);
        given(userService.getUserByUsername(Mockito.any())).willReturn(user2);

        GlobalLeaderboardGetDTO globalLeaderboardGetDTO = new GlobalLeaderboardGetDTO();

        List<User> l3 = new ArrayList<>();
        l3.add(user);
        l3.add(user2);

        given(userRepository.findAll()).willReturn(l3);


        LinkedHashMap<Integer, LeaderboardEntryDTO> entries = new LinkedHashMap<Integer, LeaderboardEntryDTO>();
        ArrayList<LeaderboardEntryDTO> list = new ArrayList<LeaderboardEntryDTO>();

        for (User user : l3) {
            LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
            entry.setUserID(user.getId());
            entry.setLevel(user.getLevel());
            entry.setXP(user.getLevel());
            entry.setUsername(user.getUsername());
            list.add(entry);
        }

        Collections.sort(list);
        Collections.reverse(list);

        for(int i=0; i<list.size(); i++) {
            list.get(i).setRank(i+1);
            entries.put(i+1, list.get(i));
        }

        globalLeaderboardGetDTO.setLeaderboardEntries(entries);


        given(globalLeaderboardService.makeLeaderboard()).willReturn(globalLeaderboardGetDTO);


        MockHttpServletRequestBuilder getRequest = get("/globalLeaderboard")
                .contentType(MediaType.APPLICATION_JSON);


        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
               // .andExpect(jsonPath("$.leaderboardEntries", contains("<{1={userID=2, username=user2, level=2, rank=1, xp=2}, 2={userID=1, username=user1, level=1, rank=2, xp=1}}>")));
    }
}
