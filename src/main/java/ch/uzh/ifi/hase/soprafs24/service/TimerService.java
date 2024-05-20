package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TimerRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.LeaderBoardDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.QuestionToSend;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.outbound.TimerOut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class TimerService {
    WebSocketService webSocketService;

    public TimerService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
    class Command implements Runnable {
        WebSocketService webSocketService;
        TimerOut timerOut;
        String destination;
        ScheduledThreadPoolExecutor threadPool;

        public Command( TimerOut timerOut, String destination, ScheduledThreadPoolExecutor threadPool)
        {
            this.webSocketService = TimerService.this.webSocketService;
            this.timerOut = timerOut;
            this.destination = destination;
            this.threadPool = threadPool;
        }
        public void run() {
            Game game = GameRepository.findByGameId(timerOut.getGameId());
            if (timerOut.getGamePhase().equals("drawing")) { //only if timer has gamePhase drawing then he should update TimeLeftInTurn
                game.setTimeLeftInTurn(timerOut.getTime());
            }
            if (timerOut.getGamePhase().equals("leaderboard")  && timerOut.getTime() == 0) {
                doShutDownTimer(timerOut.getGameId());
            }
            this.webSocketService.sendMessageToClients(destination, timerOut);
            if (game.getCurrentCorrectGuesses() < game.getPlayers().size() && timerOut.getTime() == 0 && timerOut.getGamePhase().equals("drawing")){
                System.out.println("endturn");
                if (game.getCurrentRound()==game.getMaxRounds() && game.getCurrentTurn()== game.getPlayersOriginally()) {
                    game.setEndGame(true);
                }
                LeaderBoardDTO leaderboardDTO = game.calculateLeaderboard();
                game.setGamePhase("leaderboard");
                if (game.getEndGame()){
                    leaderboardDTO.setReason("normal");
                }
                this.webSocketService.sendMessageToClients("/topic/games/" + timerOut.getGameId() + "/general", leaderboardDTO); //endturn
                doShutDownTimer(timerOut.getGameId());
                doTimer(5,1, timerOut.getGameId(), "/topic/games/" + timerOut.getGameId() + "/general", "leaderboard"); //timer to look at leaderboard
            }

        }
    }
    class Command2 implements Runnable {
        WebSocketService webSocketService;
        int gameId;
        int userId;
        String action;
        public Command2(int gameId, int userId, String action){
            this.webSocketService = TimerService.this.webSocketService;
            this.gameId = gameId;
            this.userId = userId;
            this.action = action;
        }
        public void run() {
            if (action.equals("inLobbyAdmin")) {
                System.out.println("Runnable for ReloadDisc executed with action: "+"inLobbyAdmin" + " = "+action);
                Game game = GameRepository.findByGameId(gameId);
                if (game != null) {
                    game.deletegame(gameId);  //should the lines below also be in the if statement?
                }
                QuestionToSend questionToSend = new QuestionToSend("deletegame");
                this.webSocketService.sendMessageToClients("/topic/games/" + gameId + "/general", questionToSend);  //for the players in the Lobby
                this.webSocketService.sendMessageToClients("/topic/landing", questionToSend);  //for the Landingpage to update List of Lobbies, will trigger a getallgames

            }
            if (action.equals("inLobbyPlayer")) {
                System.out.println("Runnable for ReloadDisc executed with action: "+"inLobbyPlayer"+ " = "+action);
                Game game = GameRepository.findByGameId(gameId);
                if (game != null) {
                    game.leavegame(userId, gameId);
                }

            }
            if (action.equals("inGame")) {
                System.out.println("Runnable for ReloadDisc executed with action: "+"inGame"+ " = "+action);

                Game game = GameRepository.findByGameId(gameId);
                if (game != null) {
                    game.lostConnectionToPlayer(userId, gameId);

                }
            }
            stopDiscTimer(userId);
        }
    }
    public void doTimerForReloadDisc(int gameId, int userId, String action){
        System.out.println("doTimerForReloadDisc with action: " + action);
        ScheduledThreadPoolExecutor threadPool
                = new ScheduledThreadPoolExecutor(2);
        TimerRepository.addTimerDisconnected(userId, threadPool);
        HashMap<Integer, ScheduledFuture> tasks = new HashMap<>();
        Runnable task = new Command2(gameId, userId, action);
        int delay = 2;
        ScheduledFuture scheduledTask = threadPool.schedule(task, delay, TimeUnit.SECONDS);
        tasks.put(delay, scheduledTask);
        TimerRepository.addAllTasksDisconnected(userId, tasks);
    }
    public void stopDiscTimer(int userId) {
        ScheduledThreadPoolExecutor threadPool =  TimerRepository.findTimerDiscByUserId(userId);
        if (threadPool != null) {
            List<Runnable> remainingTasks = threadPool.shutdownNow(); //dont know yet if I need to cancel them
            TimerRepository.deleteDiscTimer(userId);
        }

    }
    public void doTimer(int Length, int Interval, int gameId, String destination, String gamePhase) {
        System.out.println("doTimer with gamePhase: "+ gamePhase);
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
        if (gamePhase.equals("drawing")) { //only if timer has gamePhase drawing then he should update TimeLeftInTurn
            Game game = GameRepository.findByGameId(timerOut.getGameId());
            game.setTimeLeftInTurn(timerOut.getTime());
        }
        this.webSocketService.sendMessageToClients(destination, timerOut); //sends out e.g. 60 = Length as the start of the timer
        System.out.println("Length, Interval, gamePhase: "+Length+ ", "+ Interval+ ", "+ gamePhase);
        for (int i = Length-1; i >= 0; i = i - Interval) { //e.g. starts at 59 goes to 0, the 60 would be displayed on client side before the first response (with 59) arrives in the client
            TimerOut timerOut2 = new TimerOut();
            timerOut2.setType("TimerOut");
            timerOut2.setGamePhase(gamePhase);
            timerOut2.setGameId(gameId);
            timerOut2.setTime(i);
            timerOut2.setInterval(Interval);
            timerOut2.setLength(Length);
            int delay = Length - i;
            Runnable task1 = new Command(timerOut2, destination, threadPool);
            ScheduledFuture scheduledTask = threadPool.schedule(task1, delay, TimeUnit.SECONDS);
            tasks.put(i, scheduledTask);
        }
        TimerRepository.addAllTasks(gameId, tasks);
        //threadPool.shutdown();
    }
    public void setLowerTime(int Length, int Interval, int gameId, String destination, String gamephase) {
        //for checking whether the timer should be set to a lower Length after a correct guess
        //then deletes the current timer and start a new one with the new shorter Length
        ScheduledThreadPoolExecutor threadPool =  TimerRepository.findTimerByGameId(gameId);
        HashMap<Integer, ScheduledFuture> tasks = TimerRepository.findTasksByGameId(gameId);

        if (tasks.get(Length).isDone()) {
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
