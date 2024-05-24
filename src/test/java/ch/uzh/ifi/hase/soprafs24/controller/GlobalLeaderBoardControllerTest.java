package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GlobalLeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.GlobalLeaderboardService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalLeaderBoardControllerTest.class)
public class GlobalLeaderBoardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    GlobalLeaderboardService globalLeaderboardService;
    @Mock
    private User user;
    @Mock
    private User user2;
    @MockBean
    private UserService userService;
    private UserService userService1;

    @Mock
    private UserRepository userRepository;
private UserRepository userRepository1;

    @Test
    public void test_GlobalLeaderBoardController() throws Exception {/*
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
        friends.add("2");
        friends.add("3");
        user.setFriends(friends);
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
        user2.setLevel(2);
        user2.setPassword("1");


        userService.createUser(user, true);
        userService.createUser(user2, true);

        GlobalLeaderboardGetDTO globalLeaderboardGetDTO = new GlobalLeaderboardGetDTO();

        List<User> users = userRepository.findAll();
        HashMap<Integer, LeaderboardEntryDTO> entries = new HashMap<Integer, LeaderboardEntryDTO>();
        ArrayList<LeaderboardEntryDTO> list = new ArrayList<LeaderboardEntryDTO>();

        for (User user : users) {
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
    }*/
}
