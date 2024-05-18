package ch.uzh.ifi.hase.soprafs24.utils;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;

import java.util.Random;
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
}
