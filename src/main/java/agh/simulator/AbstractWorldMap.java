package agh.simulator;

import java.util.*;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    public Statistics statistics;
    protected Vector2d lowerLeft;
    protected Vector2d upperRight;
    protected final Map<Vector2d, ArrayList<Animal>> animals = new HashMap<>();
    protected final List<Animal> animalList = new ArrayList<>();
    protected final Map<Vector2d, Plant> plants = new HashMap<>();
    protected final List<Vector2d> emptyJunglePlantPositionsList = new ArrayList<>();
    protected final List<Vector2d> emptySteppePlantPositionsList = new ArrayList<>();
    protected final List<Animal> deadAnimalsList = new LinkedList<>();
    private final int plantEnergy;
    protected final Jungle jungle;
    private final int initialEnergy;
    public boolean isMagic = false;
    public int magicCounter = 3;
    public Animal trackedAnimal = null;

    public AbstractWorldMap(double jungleRatio, int width, int height, int plantEnergy, int initialEnergy){
        this.upperRight = new Vector2d(width, height);
        this.lowerLeft = new Vector2d(0,0);
        this.jungle = new Jungle(jungleRatio, width, height);
        this.plantEnergy = plantEnergy;
        this.initialEnergy = initialEnergy;
        this.statistics = new Statistics(this);
        fillEmptyPositionsList();
        placePlants();
    }

    private void fillEmptyPositionsList() {
        for (int i = 0; i <= upperRight.x; i++) {
            for (int j = 0; j <= upperRight.y; j++) {
                if (jungle.isPositionInJungle(new Vector2d(i, j))){
                    this.emptyJunglePlantPositionsList.add(new Vector2d(i, j));
                }
                else{
                    this.emptySteppePlantPositionsList.add(new Vector2d(i, j));
                }
            }
        }
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
    private Vector2d getRandomPosition() {
        return new Vector2d(getRandom(this.upperRight.x), getRandom(this.upperRight.y));
    }


    public void placePlants() {
        Vector2d newPosition;
        int randomIndex;

        if (emptySteppePlantPositionsList.size() > 0) {
            randomIndex = getRandom(emptySteppePlantPositionsList.size());
            newPosition = emptySteppePlantPositionsList.get(randomIndex);
            plants.put(newPosition, new Plant(plantEnergy));
            emptySteppePlantPositionsList.remove(newPosition);
        }
        if (emptyJunglePlantPositionsList.size() > 0) {
            randomIndex = getRandom(emptyJunglePlantPositionsList.size());
            newPosition = emptyJunglePlantPositionsList.get(randomIndex);
            plants.put(newPosition, new Plant(plantEnergy));
            emptyJunglePlantPositionsList.remove(newPosition);
        }
    }

    public boolean isInBounds(Vector2d position){
        return position.precedes(getUpperRight()) && position.follows(getLowerLeft());
    }

    public boolean place(Animal animal) {
        if (isInBounds(animal.getPosition())){
            addAnimal(animal, animal.getPosition());
            animalList.add(animal);
            animal.addObserver(this);
            this.statistics.updateGenotypeStatistics(animal.getGenotype().genes);
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
    public void removeDead(int era){
        List<Animal> iterateList = animalList;
        for (int i = 0; i < iterateList.size(); i++) {
            Animal animal = iterateList.get(i);
            if(animal.getEnergy() <= 0){
                removeAnimal(animal, animal.getPosition());
                animal.removeObserver(this);
                animalList.remove(animal);
                deadAnimalsList.add(animal);
                if(animal.isTracked){
                    animal.setDeathEra(era);
                }
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
                if (jungle.isPositionInJungle(position)){
                    emptyJunglePlantPositionsList.add(position);
                }
                else{
                    emptySteppePlantPositionsList.add(position);
                }
            }
        }
    }
    public void reproduction(){
        List<Animal> animalsToPlace = new LinkedList<>();
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
                if (animal1.getEnergy() > 0.5*initialEnergy && animal2.getEnergy() > 0.5*initialEnergy) {
                    Animal child = animal1.reproduce(animal2);
                    animalsToPlace.add(child);
                    if (trackedAnimal != null) {
                        updateTrackedAnimal(animal1, child);
                        updateTrackedAnimal(animal2, child);
                    }
                }
            }
        }
        for (Animal animal : animalsToPlace){
            this.place(animal);
        }
    }

    private void updateTrackedAnimal(Animal animal, Animal child) {
        if(animal.isTracked){
            trackedAnimal.trackedChildrenNum += 1;
            trackedAnimal.descendants.add(child);
        }
        if(isDescendantOfTracked(animal)){
            trackedAnimal.descendants.add(child);
        }
    }

    private boolean isDescendantOfTracked(Animal animal) {
        for(Animal descendant : trackedAnimal.descendants){
            if (animal == descendant){
                return true;
            }
        }
        return false;
    }

    public boolean magicStrategy(){
        if (magicCounter == 0) {
            isMagic = false;
        }
        if (animalList.size() == 5) {
            List<Animal> toPlace = new LinkedList<>();
            for (Animal animal : animalList) {
                Animal animal1 = animal;
                animal1.setPosition(getRandomPosition());
                animal1.refillEnergy();
                toPlace.add(animal);
            }
            for (Animal animal : toPlace){
                this.place(animal);
            }
            magicCounter--;
            return true;
        }
        return false;
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

    public Jungle getJungle() {
        return jungle;
    }

    public void track(Animal animal) {
        trackedAnimal = animal;
        animal.isTracked = true;
        animal.descendants = new LinkedList<>();
        animal.trackedChildrenNum = 0;
    }
}

