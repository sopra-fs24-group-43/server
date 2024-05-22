package ch.uzh.ifi.hase.soprafs24.utils;

public class PointCalculatorDrawer {

    public static int calculate(int duration, int answerTime, int currentCorrectGuesses){
        double maxPoints = 1000;

        System.out.println("********************************************************************************");
        System.out.println("Calculating points for drawer:");
        System.out.println("round duration: " + duration);
        System.out.println("Time remaining: " + answerTime);
        System.out.println("Current correct guesses: " + currentCorrectGuesses);

        double Points = (((double) answerTime/ (double) duration) * maxPoints) / Math.pow(2, currentCorrectGuesses + 1);
        System.out.println("Points: " + Points);
        System.out.println("********************************************************************************");

        return  (int) Points;
    }
}