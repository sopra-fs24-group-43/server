package ch.uzh.ifi.hase.soprafs24.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
import ch.uzh.ifi.hase.soprafs24.service.TimerService;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.utils.RandomGenerators;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class TimerRepositoryTest {

    @MockBean
    private TimerRepository timerRepository;


    @MockBean
    private TimerService timerService;


    @BeforeEach
    void setup() {

    }
    @AfterEach
    void teardown() {

    }

    @Test
    public void addTimerTest() {
        ScheduledThreadPoolExecutor threadPool = null;
        timerRepository.addTimer(1, threadPool);

        assertEquals(timerRepository.findTimerByGameId(1), threadPool);
    }
}
