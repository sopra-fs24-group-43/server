package ch.uzh.ifi.hase.soprafs24.rest.dto;

import lombok.*;

import java.util.Comparator;

@Getter
@Setter
public class LeaderboardEntryDTO implements Comparable<LeaderboardEntryDTO> {
    private Long userID;
    private String username;
    private int XP;
    private int level;
    private int rank;

    @Override
    public int compareTo(LeaderboardEntryDTO entry) {
        return Integer.valueOf(this.getXP()).compareTo(Integer.valueOf(entry.getXP()));
    }



}
