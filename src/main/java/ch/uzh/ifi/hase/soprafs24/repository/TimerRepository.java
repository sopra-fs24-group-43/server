package ch.uzh.ifi.hase.soprafs24.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TimerRepository {
    private static final HashMap<Integer, ScheduledThreadPoolExecutor> timerRepo = new HashMap<>(); //<gameId, ScheduledThreadPoolExecutor>
    private static final HashMap<Integer, HashMap<Integer, ScheduledFuture>> timerTasksRepo= new HashMap<>(); //<gameId, <time, ScheduledFuture>>
    private TimerRepository() {

    }
    public static void addTimer(int gameId, ScheduledThreadPoolExecutor threadPool) {
        timerRepo.put(gameId, threadPool);
        HashMap<Integer, ScheduledFuture> Tasks = new HashMap<>();
        timerTasksRepo.put(gameId, Tasks);
    }
    public static void addAllTasks(int gameId, HashMap<Integer, ScheduledFuture> tasks) {
        timerTasksRepo.forEach((key, value) -> {
            if (key == gameId) {
                value.putAll(tasks);
            }
        });
    }
    public static void deleteTimer(int gameId) {
        timerRepo.remove(gameId);
        timerTasksRepo.remove(gameId);
    }

    //doesnt work yet
    public static void haltTimer(int gameId) { //after halting the Timer still exists so if it
                                               //has no more use, it should also be removed from Repo
                                               //and even be destroyed
        ScheduledThreadPoolExecutor threadPool = findTimerByGameId(gameId);
    }

    public static ScheduledThreadPoolExecutor findTimerByGameId(int gameId) {
        return timerRepo.get(gameId);
    }
    public static HashMap<Integer, ScheduledFuture> findTasksByGameId(int gameId) {
        return timerTasksRepo.get(gameId);
    }
}
