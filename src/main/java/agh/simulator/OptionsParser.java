package agh.simulator;

import java.util.ArrayList;


public class OptionsParser {
    public static MoveDirection[] parse(String[] args) throws IllegalArgumentException{
        ArrayList<MoveDirection> resultList = new ArrayList<>();
        for(String arg : args){
            switch (arg) {
                case "f", "forward" -> resultList.add(MoveDirection.FORWARD);
                case "b", "backward" -> resultList.add(MoveDirection.BACKWARD);
                case "r", "right" -> resultList.add(MoveDirection.RIGHT);
                case "l", "left" -> resultList.add(MoveDirection.LEFT);
                default -> throw new IllegalArgumentException(arg + " is not legal move specification");
            }
        }
        MoveDirection[] result = new MoveDirection[0];
        result = resultList.toArray(result);
        return result;
    }
}

