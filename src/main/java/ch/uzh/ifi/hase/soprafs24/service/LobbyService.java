package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LobbyService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public LobbyService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public void createLobby(LobbyDTO lobbyDTO){
        Lobby newLobby = new Lobby(lobbyDTO.getLobbyID());
        LobbyRepository.addLobby(lobbyDTO.getLobbyID(),newLobby);
    }
}
