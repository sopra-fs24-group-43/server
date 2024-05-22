package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import java.util.HashMap;

import lombok.*;

@Getter
@Setter
public class GlobalLeaderboardGetDTO {
    private HashMap<Integer, LeaderboardEntryDTO> leaderboardEntries;
}
