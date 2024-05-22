package ch.uzh.ifi.hase.soprafs24.service;


import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GlobalLeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LeaderboardEntryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Service
@Transactional
public class GlobalLeaderboardService {

    private final UserRepository userRepository;

    @Autowired
    public GlobalLeaderboardService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public GlobalLeaderboardGetDTO makeLeaderboard() {
        GlobalLeaderboardGetDTO globalLeaderboardGetDTO = new GlobalLeaderboardGetDTO();

        List<User> users = this.userRepository.findAll();
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
        return globalLeaderboardGetDTO;
    }

}
