package agh.simulator;

public class World {
    public static void main(String[] args){
        String[] input = new String[]{"f", "l", "f", "f", "f", "f"};
        MoveDirection[] moves = OptionsParser.parse(input);
        AbstractWorldMap map = new WrappedMap(0.25, 10, 10);
        int animalAmount = 20;
        IEngine engine = new SimulationEngine(moves, map, animalAmount);
        System.out.println(map);
        engine.run();
    }
}
