package ch.uzh.ifi.hase.soprafs24.utils;

public class PointCalculatorDrawer {

    public static int calculate(int duration, int answerTime, int currentCorrectGuesses){
        double maxPoints = 1000;

        double Points = ((answerTime/duration) * maxPoints) / Math.pow(2, currentCorrectGuesses + 1);

        return  (int) Points;
    }
}
