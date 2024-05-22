package ch.uzh.ifi.hase.soprafs24.utils;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RandomGenerators {
    public Random random = new Random();
    public String PasswordGenerator() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;


        String generatedString = this.random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public int GameIdGenerator() {
        int gameId = this.random.nextInt(1000);
        return gameId;
    }

    public int GuestIdGenerator() {
        int guestId = this.random.nextInt(10000) - 10010;
        while(PlayerRepository.guestIdtaken(guestId)) {
            guestId = this.random.nextInt(10000) - 10010;
        }
        return guestId;
    }
    public ArrayList<String> DoShuffle(ArrayList<String> wordList) {
        //make sure that the acutal game.wordList is shuffled with a setWordList(return list from this method)
        Collections.shuffle(wordList);
        return wordList;
    }
}
