package ch.uzh.ifi.hase.soprafs24.utils;

public class PointCalculatorGuesser {

    public static int calculate(int duration, int answerTime, int answerPosition){
        double maxPointsTime = 500;
        double maxPointsPos = 500;

        System.out.println("********************************************************************************");
        System.out.println("Calculating points for drawer:");
        System.out.println("round duration: " + duration);
        System.out.println("Time remaining: " + answerTime);
        System.out.println("Current correct guesses (including this guess): " + answerPosition);
        double PointsTime = ((double) answerTime / (double) duration) * maxPointsTime;
        double PointsPos = (1/(Math.pow((answerPosition), 1.5))) * maxPointsPos;

        double result = PointsTime + PointsPos;
        System.out.println("Points: " + result);
        System.out.println("********************************************************************************");

        return  (int) result;
    }

}