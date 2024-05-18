package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TimerRepository;
import ch.uzh.ifi.hase.soprafs24.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.QuestionToSend;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.TimerOut;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.sendback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class TimerService {
    private final WebSocketService webSocketService;

    @Autowired
    public TimerService(WebSocketService webSocketService) {

        this.webSocketService = webSocketService;

    }
    class Command implements Runnable {
        WebSocketService webSocketService;
        TimerOut timerOut;
        String destination;
        ScheduledThreadPoolExecutor threadPool;
        public Command( WebSocketService webSocketService, TimerOut timerOut, String destination, ScheduledThreadPoolExecutor threadPool)
        {
            this.webSocketService = webSocketService;
            this.timerOut = timerOut;
            this.destination = destination;
            this.threadPool = threadPool;
        }
        public void run() {
            Game game = GameRepository.findByGameId(timerOut.getGameId());
            game.setTimeLeftInTurn(timerOut.getTime());
            this.webSocketService.sendMessageToClients(destination, timerOut);
            if (game.getCurrentCorrectGuesses() < game.getPlayers().size() && timerOut.getTime() == 0 && timerOut.getGamePhase() == "drawing"){
                //should be "drawing"
                LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
                leaderboardDTO.setType("leaderboard");
                this.webSocketService.sendMessageToClients("/topic/games/" + timerOut.getGameId() + "/general", leaderboardDTO);

            }
        }
    }
    public void doTimer(int Length, int Interval, int gameId, String destination, String gamePhase){
        doShutDownTimer(gameId);
        ScheduledThreadPoolExecutor threadPool
                = new ScheduledThreadPoolExecutor(2);

        TimerRepository.addTimer(gameId, threadPool);
        HashMap<Integer, ScheduledFuture> tasks = new HashMap<>();
        TimerOut timerOut = new TimerOut();
        timerOut.setType("TimerOut");
        timerOut.setGamePhase(gamePhase);
        timerOut.setGameId(gameId);
        timerOut.setTime(Length);
        timerOut.setInterval(Interval);
        timerOut.setLength(Length);
        Game game = GameRepository.findByGameId(timerOut.getGameId());
        game.setTimeLeftInTurn(timerOut.getTime());
        this.webSocketService.sendMessageToClients(destination, timerOut); //sends out e.g. 60 = Length as the start of the timer
        for (int i = Length-1; i >= 0; i = i - Interval) { //e.g. starts at 59 goes to 0, the 60 would be displayed on client side before the first response (with 59) arrives in the client
            TimerOut timerOut2 = new TimerOut();
            timerOut2.setType("TimerOut");
            timerOut2.setGamePhase(gamePhase);
            timerOut2.setGameId(gameId);
            timerOut2.setTime(i);
            timerOut2.setInterval(Interval);
            timerOut2.setLength(Length);
            int delay = Length - i;
            Runnable task1 = new Command(webSocketService, timerOut2, destination, threadPool);
            ScheduledFuture scheduledTask = threadPool.schedule(task1, delay, TimeUnit.SECONDS);
            tasks.put(i, scheduledTask);
        }
        TimerRepository.addAllTasks(gameId, tasks);
        threadPool.shutdown();
    }

    public void setLowerTime(int Length, int Interval, int gameId, String destination, String gamephase) {
        //for checking whether the timer should be set to a lower Length after a correct guess
        //then deletes the current timer and start a new one with the new shorter Length
        ScheduledThreadPoolExecutor threadPool =  TimerRepository.findTimerByGameId(gameId);
        HashMap<Integer, ScheduledFuture> tasks = TimerRepository.findTasksByGameId(gameId);

        if (tasks.get(Length).isDone() == true) {
            //do nothing because the new Timer would be higher than the one of the currently running Timer
        }
        else {
            List<Runnable> remainingTasks = threadPool.shutdownNow(); //dont know yet if I need to cancel them
            TimerRepository.deleteTimer(gameId);
            doTimer(Length, Interval, gameId, destination, gamephase);
        }
        /*
        tasks.forEach((key, value) -> {
            HashMap<Long, Boolean> okay = new HashMap<>();
            okay.put(value.getDelay(TimeUnit.SECONDS), value.isDone());
            sendback sendback = new sendback(okay);
            this.webSocketService.sendMessageToClients(destination, sendback);
        });
        */
    }
    public void doShutDownTimer(int gameId) {
        //for deleting the current timer because all players guessed correctly before the time ran out or stopping old timer
        ScheduledThreadPoolExecutor threadPool =  TimerRepository.findTimerByGameId(gameId);
        if (threadPool != null) {
            List<Runnable> remainingTasks = threadPool.shutdownNow();
            TimerRepository.deleteTimer(gameId);
        }
    }
}
