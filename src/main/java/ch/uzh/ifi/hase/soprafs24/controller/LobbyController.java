package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;
    private final UserService userService;
    private final UserRepository userRepository;


    LobbyController(LobbyService lobbyService, UserService userService, UserRepository userRepository) {
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.userRepository = userRepository;

    }

    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyDTO createLobby(@RequestParam int lobbyID) {
        LobbyDTO lobbyDTO = new LobbyDTO();
        lobbyDTO.setLobbyID(lobbyID);
        lobbyService.createLobby(lobbyDTO);
        return lobbyDTO;
    }
}
