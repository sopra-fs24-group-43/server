package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.inbound.InboundPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import ch.uzh.ifi.hase.soprafs24.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PlayerRepositoryTest {



    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private RandomGenerators randomGenerators;

    @Mock
    private Player player;

    @BeforeEach
    void setup() {
        ArrayList<Integer> n = new ArrayList<>();
        n.add(2);
        player = new Player("1", 1, false, 1, n, "Drawer");
        PlayerRepository.addPlayer(1,1,player);
    }

    @Test
    void findByUserIdSuccess() {
        // when
        Player found = playerRepository.findByUserId(player.getUserId());

        // then
        assertNotNull(found.getUserId());
        assertEquals(found.getUsername(), player.getUsername());
        assertEquals(found.getGameId(), player.getGameId());
        assertEquals(found.getFriends(), player.getFriends());
        assertEquals(found.getRole(), player.getRole());

    }

    @Test
    void findByGameIdSuccess() {
        // when
        HashMap<Integer, Player> playersInGame = playerRepository.findUsersByGameId(player.getGameId());

        // then
        for (Player pl:playersInGame.values()) {
            assertNotNull(pl.getUserId());
            assertEquals(pl.getUsername(), player.getUsername());
            assertEquals(pl.getGameId(), player.getGameId());
            assertEquals(pl.getFriends(), player.getFriends());
            assertEquals(pl.getRole(), player.getRole());
        }

    }
/*
    @Test
    void createPlayerFromGuestTest() {
        int guestId = 1111;
        boolean isG = true;
        when(randomGenerators.GuestIdGenerator()).thenReturn(guestId);
        InboundPlayer guest = playerRepository.createPlayerFromGuest();
        assertEquals(guest.getIsGuest(),isG);
        assertEquals(guest.getUserId(),guestId);
        assertEquals(guest.getUsername(),"guestuser" + guestId*-1);
    }
*/
    @Test
    void addPlayerTest() {


        playerRepository.addPlayer(1,1,player);

        HashMap<Integer, Player> playersinsamegame = playerRepository.findUsersByGameId(1);
        HashMap<Integer, Player> m = new HashMap<>();
        m.put(1,player);


        assertEquals(playersinsamegame,m);
    }
}