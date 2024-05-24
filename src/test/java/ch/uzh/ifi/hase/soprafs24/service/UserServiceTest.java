package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Mock
  private User testUser2;
  @Mock
  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setName("testName");
    testUser.setUsername("testuser");
    testUser.setPassword("testuser");
    List<String> l = new ArrayList<>();
    l.add("2");
    testUser.setFriends(l);


    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);


  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser, true);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser, true);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser, true));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser, true);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser, true));
  }

  @Test
  public void editProfileTest() {
      userService.createUser(testUser, true);
      //userRepository.save(testUser);
      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setUsername("testuser2");
      userPostDTO.setName("testuser2");
      userPostDTO.setHotkeyInputDraw("Q");
      userPostDTO.setHotkeyInputFill("W");
      userPostDTO.setHotkeyInputEraser("E");
      userPostDTO.setHotkeyInputClear("R");
      userPostDTO.setBirth_date("01.01.1990");
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);


      //Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
      //Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
      Mockito.when(userRepository.findUserById(Mockito.any())).thenReturn(testUser);

      User user = userService.editProfile(1L, userInput);

      assertEquals(user.getId(), testUser.getId());
      assertEquals(testUser.getName(), userInput.getName());
      assertEquals(testUser.getUsername(), userInput.getUsername());
      assertEquals(testUser.getHotkeyInputDraw(), userInput.getHotkeyInputDraw());
      assertEquals(testUser.getHotkeyInputFill(), userInput.getHotkeyInputFill());
      assertEquals(testUser.getHotkeyInputEraser(), userInput.getHotkeyInputEraser());
      assertEquals(testUser.getHotkeyInputClear(), userInput.getHotkeyInputClear());
      assertNotNull(user.getToken());
      assertEquals(UserStatus.ONLINE, user.getStatus());
  }


}
