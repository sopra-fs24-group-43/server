package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;



public class Lobby{
    private final long lobbyID;

    public Lobby(long lobbyID) {
        this.lobbyID = lobbyID;
    }


    @Id
    @GeneratedValue
    private Long id;

}
