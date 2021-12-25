package agh.simulator;

import java.lang.reflect.Array;
import java.util.*;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    protected Vector2d lowerLeft;
    protected Vector2d upperRight;
    protected final Map<Vector2d, ArrayList<Animal>> animals = new HashMap<>();
    protected final List<Animal> animalList = new ArrayList<>();
    protected final Map<Vector2d, Plant> plants = new HashMap<>();
    protected final Jungle jungle;
    private final int startEnergy = 100;

    public AbstractWorldMap(double jungleRatio, int width, int height){
        this.upperRight = new Vector2d(width, height);
        this.lowerLeft = new Vector2d(0,0);
        this.jungle = new Jungle(jungleRatio, width, height);
        placePlants();
    }
    public Vector2d getLowerLeft(){
        return lowerLeft;
    }
    public Vector2d getUpperRight(){
        return upperRight;
    }

    @Override
    public String toString() {
        MapVisualizer visualizer = new MapVisualizer(this);
        return visualizer.draw(getLowerLeft(), getUpperRight());
    }

    private int getRandom(int max){
        return (int) ((Math.random() * (max)));
    }
    private Vector2d getRandomPosition(int xMinBound, int yMinBound, int xMaxBound, int yMaxBound) {
        int x = getRandom(getUpperRight().x);
        int y = getRandom(getUpperRight().y);
        return new Vector2d(x, y);
    }
//TODO: optymalizacja -> losowanie z wolnych przestrzeni
//TODO: kiedy wszystkie pozycje pelne to nie rosnie wiecej trawy
    public void placePlants() {
        Vector2d newPosition = getRandomPosition(0, 0, getUpperRight().x, getUpperRight().y);
        while (isOccupied(newPosition)) {
            newPosition = getRandomPosition(0, 0, getUpperRight().x, getUpperRight().y);
        }
        plants.put(newPosition, new Plant(newPosition));

        newPosition = getRandomPosition(jungle.getLowerLeft().x, jungle.getLowerLeft().y, jungle.getUpperRight().x, jungle.getUpperRight().y);
        while (isOccupied(newPosition)) {
            newPosition = getRandomPosition(jungle.getLowerLeft().x, jungle.getLowerLeft().y, jungle.getUpperRight().x, jungle.getUpperRight().y);
        }
        plants.put(newPosition, new Plant(newPosition));
    }

    public boolean isInBounds(Vector2d position){
        return position.precedes(getUpperRight()) && position.follows(getLowerLeft());
    }

    public boolean place(Animal animal) {
        if (isInBounds(animal.getPosition())){
            addAnimal(animal, animal.getPosition());
            animalList.add(animal);
            animal.addObserver(this);
            return true;
        }
        return false;
    }

    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    public Object objectAt(Vector2d position) {
        ArrayList<Animal> list = animals.get(position);
        Object object = null;
        if (list == null || list.size() == 0){
            object = plants.get(position);
        }
        else{
            object = list.get(0);
        }
        return object;
    }

    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition){
        if (animal != null) {
            removeAnimal(animal, oldPosition);
            addAnimal(animal, newPosition);
        }
    }
    protected void removeAnimal(Animal animal, Vector2d position){
        ArrayList<Animal> list = animals.get(position);
        if(list != null){
            list.remove(animal);
            animals.put(position, list);
            if (list.size() == 0){
                animals.remove(position);
            }
        }
    }
    protected void addAnimal(Animal animal, Vector2d position){
        ArrayList<Animal> list = animals.get(position);
        if (list == null) {
            ArrayList<Animal> newList = new ArrayList<>();
            newList.add(animal);
            animals.put(position, newList);
        }
        else{
            list.add(animal);
            animals.put(position, list);
        }
    }
    public void removeDead(){
        List<Animal> iterateList = animalList;
        for (int i = 0; i < iterateList.size(); i++) {
            Animal animal = iterateList.get(i);
            if(animal.getEnergy() <= 0){
                removeAnimal(animal, animal.getPosition());
                animal.removeObserver(this);
                animalList.remove(animal);
            }
        }
    }
    public void eatPlants(){
        for (Vector2d position : animals.keySet()){
            Plant plant = plants.get(position);
            if (plant != null){
                List<Animal> topAnimals = getTopAnimals(animals.get(position));
                for (Animal animal : topAnimals) {
                    int numOfAnimals = topAnimals.size();
                    animal.eat(plant.getEnergy()/numOfAnimals);
                }
                plants.remove(position, plant);
            }
        }
    }
    public void reproduction(){
        for (ArrayList<Animal> list : animals.values()) {
                int topAnimalsSize = getTopAnimals(list).size();
                int index1 = 0;
                int index2 = 1;

            // get random top animals
            if (topAnimalsSize > 2){
                index1 = getRandom(topAnimalsSize);
                index2 = getRandom(topAnimalsSize);
                while(index2 == index1){
                    index2 = getRandom(topAnimalsSize);
                }
            }
            // choose animals to reproduce
            if (list.size() >= 2) {
                list.sort(Comparator.comparing(Animal::getEnergy));
                Collections.reverse(list);
                Animal animal1 = list.get(index1);
                Animal animal2 = list.get(index2);
                if (animal1.getEnergy() > 0.5*startEnergy && animal2.getEnergy() > 0.5*startEnergy) {
                    Animal child = animal1.reproduce(animal2);
                    this.place(child);
                    System.out.println("THERE IS NEW LIFE");
                }
            }
        }
    }
    private ArrayList<Animal> getTopAnimals(ArrayList<Animal> animals){
        animals.sort(Comparator.comparing(Animal::getEnergy));
        Collections.reverse(animals);
        ArrayList<Animal> resultAnimals = new ArrayList<>();
        int topEnergy = animals.get(0).getEnergy();
        for (Animal animal : animals) {
            if (animal.getEnergy() == topEnergy) {
                resultAnimals.add(animal);
            } else {
                break;
            }
        }
        return resultAnimals;
    }
}

