package agh.simulator;

public class SimulationEngine implements IEngine {
    private final AbstractWorldMap map;
    public SimulationEngine(MoveDirection[] moves, IWorldMap map, int animalAmount){
        this.map = (AbstractWorldMap) map;
        for(int i = 0; i < animalAmount; i++){
            Animal animal = new Animal(randomPositions(), (AbstractWorldMap) map);
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
        int k = 0;
        while(map.animalList.size() > 0){
            map.removeDead();
//            System.out.println("SIZE:"+map.animalList.size());
            for (Animal animal : map.animalList) {
                animal.move();
//                System.out.println(animal.getEnergy());
            }
            map.eatPlants();
            map.reproduction();
            map.placePlants();
            k++;
            System.out.println(map);
        }
        System.out.println(k);
    }
}
