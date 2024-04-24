package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;


public class LobbyRepository {
    static final HashMap<Integer, Lobby> lobbyRepository = new HashMap<>();

    public static void addLobby(long id, Lobby lobby) {
        lobbyRepository.put((Math.toIntExact(id)),lobby);
    }
}