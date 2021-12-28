package agh.simulator;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngine implements IEngine, Runnable {
    private final AbstractWorldMap map;
    private final List<IMapObserver> observers = new ArrayList<>();
    private boolean isStopRequested = false;
    public boolean isRunning = true;
    private int initialEnergy;
    private int moveEnergy;
    public int era = 1;
    public SimulationEngine(AbstractWorldMap map, int animalAmount, int initialEnergy, int moveEnergy){
        this.map = map;
        for(int i = 0; i < animalAmount; i++){
            Animal animal = new Animal(randomPositions(), map, initialEnergy, moveEnergy);
            map.place(animal);
            animal.setBirthEra(era);
        }
    }

    private Vector2d randomPositions() {
        return new Vector2d(getRandom(map.upperRight.x), getRandom(map.upperRight.y));
    }
    private int getRandom(int max){
        return (int) ((Math.random() * (max)));
    }

    @Override
    public synchronized void run() {
        while(map.animalList.size() > 0 && !isStopRequested){
            synchronized (this){
                while (!isRunning) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            map.removeDead();
            for (Animal animal : map.animalList) {
                animal.move();
            }
            map.eatPlants();
            map.reproduction();
            map.placePlants();
            era++;
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
    public void stop() {
        isRunning = false;
    }
    public synchronized void start() {
        this.notify();
        isRunning = true;
    }
}
