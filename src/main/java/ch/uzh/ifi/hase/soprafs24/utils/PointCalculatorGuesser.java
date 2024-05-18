package ch.uzh.ifi.hase.soprafs24.utils;

public class PointCalculatorGuesser {

    public static int calculate(int duration, int answerTime, int answerPosition){
        double maxPointsTime = 500;
        double maxPointsPos = 500;

        double PointsTime = (answerTime/duration) * maxPointsTime;
        double PointsPos = (1/(Math.pow((answerPosition - 0), 1.5))) * maxPointsPos;

        double result = PointsTime + PointsPos;

        return  (int) result;
    }

}