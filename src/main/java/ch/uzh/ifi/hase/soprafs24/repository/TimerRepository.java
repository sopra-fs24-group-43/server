package ch.uzh.ifi.hase.soprafs24.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TimerRepository {
    private static final HashMap<Integer, ScheduledThreadPoolExecutor> timerRepoDisconnected = new HashMap<>(); //<userId of disc player, ScheduledThreadPoolExecutor>
    private static final HashMap<Integer, HashMap<Integer, ScheduledFuture>> timerTasksRepoDisconnected = new HashMap<>(); //<userId, <time, ScheduledFuture>>
    private static final HashMap<Integer, ScheduledThreadPoolExecutor> timerRepo = new HashMap<>(); //<gameId, ScheduledThreadPoolExecutor>
    private static final HashMap<Integer, HashMap<Integer, ScheduledFuture>> timerTasksRepo= new HashMap<>(); //<gameId, <time, ScheduledFuture>>
    private TimerRepository() {

    }
    public static void addTimerDisconnected(int userId, ScheduledThreadPoolExecutor threadPool) {
        timerRepoDisconnected.put(userId, threadPool);
        HashMap<Integer, ScheduledFuture> Tasks = new HashMap<>();
        timerTasksRepoDisconnected.put(userId, Tasks);
    }
    public static void addAllTasksDisconnected(int userId, HashMap<Integer, ScheduledFuture> tasks) {
        timerTasksRepoDisconnected.forEach((key, value) -> {
            if (key == userId) {
                value.putAll(tasks);
            }
        });
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
    public static void deleteDiscTimer(int userId) {
        timerRepoDisconnected.remove(userId);
        timerTasksRepoDisconnected.remove(userId);
    }

    //doesnt work yet
    public static void haltTimer(int gameId) {
        ScheduledThreadPoolExecutor threadPool = findTimerByGameId(gameId);
        if (threadPool != null) {
            List<Runnable> remainingTasks = threadPool.shutdownNow();
            TimerRepository.deleteTimer(gameId);
        }
    }

    public static ScheduledThreadPoolExecutor findTimerByGameId(int gameId) {
        return timerRepo.get(gameId);
    }
    public static ScheduledThreadPoolExecutor findTimerDiscByUserId(int userId) {
        return timerRepoDisconnected.get(userId);
    }
    public static HashMap<Integer, ScheduledFuture> findTasksByGameId(int gameId) {
        return timerTasksRepo.get(gameId);
    }
    public static HashMap<Integer, ScheduledFuture> findDiscTasksByUserId(int userId) {
        return timerTasksRepoDisconnected.get(userId);
    }
    public static void printAllTimers() {
        System.out.println("timerRepo: "+timerRepo);
        System.out.println("timerTasksRepo: "+timerTasksRepo);
        System.out.println("timerRepoDisconnected: "+timerRepoDisconnected);
        System.out.println("timerTasksRepoDisconnected: "+timerTasksRepoDisconnected);
    }
    public static void printDiscTimers() {
        System.out.println("timerRepoDisconnected: "+timerRepoDisconnected);
        System.out.println("timerTasksRepoDisconnected: "+timerTasksRepoDisconnected);

    }
}
