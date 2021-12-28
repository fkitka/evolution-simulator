package agh.simulator;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngine implements IEngine, Runnable {
    private final AbstractWorldMap map;
    private final List<IMapObserver> observers = new ArrayList<>();
    private boolean isStopRequested = false;
    private int initialEnergy;
    private int moveEnergy;
    public int era = 1;
    public SimulationEngine(AbstractWorldMap map, int animalAmount, int initialEnergy, int moveEnergy){
        this.map = map;
        for(int i = 0; i < animalAmount; i++){
            Animal animal = new Animal(randomPositions(), map, initialEnergy, moveEnergy);
            map.place(animal);
        }
    }

    private Vector2d randomPositions() {
        return new Vector2d(getRandom(map.upperRight.x), getRandom(map.upperRight.y));
    }
    private int getRandom(int max){
        return (int) ((Math.random() * (max)));
    }

    @Override
    public void run() {
        while(map.animalList.size() > 0 && !isStopRequested){
            map.removeDead();
//            System.out.println("SIZE:"+map.animalList.size());
            for (Animal animal : map.animalList) {
                animal.move();
            }
            map.eatPlants();
            map.reproduction();
            map.placePlants();
            era++;
//            System.out.println(map);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (IMapObserver observer: observers) {
                observer.mapChanged();
            }
        }
        System.out.println(era);
    }
    public void addObserver(IMapObserver observer){
        observers.add(observer);
    }

    public void shutdown() {
        isStopRequested = true;
    }
}
