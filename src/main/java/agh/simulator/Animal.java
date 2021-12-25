package agh.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Animal {
    private final AbstractWorldMap map;
    private MapDirection orientation = getRandomOrientation();
    private Vector2d position;
    private int energy;
    private final int moveEnergy = 25;
    private final Genotype genotype;
    private final List<IPositionChangeObserver> observers = new ArrayList<>();
    private int numOfChildren = 0;

    public Animal(Vector2d position, AbstractWorldMap map){
        this.position = position;
        this.map = map;
        this.energy = 100;
        this.genotype = new Genotype();
    }
    public String toString() {
        return switch (this.orientation) {
            case NORTH -> "n";
            case NORTHEAST -> "ne";
            case EAST -> "e";
            case SOUTHEAST -> "se";
            case SOUTH -> "s";
            case SOUTHWEST -> "sw";
            case WEST -> "w";
            case NORTHWEST -> "nw";
        };
    }
    private MapDirection getRandomOrientation(){
        int sides = 8;
        int orientationNum =  (int) (Math.random() * (sides));
        return switch(orientationNum){
            case 0 -> MapDirection.NORTH;
            case 1 -> MapDirection.NORTHEAST;
            case 2 -> MapDirection.EAST;
            case 3 -> MapDirection.SOUTHEAST;
            case 4 -> MapDirection.SOUTH;
            case 5 -> MapDirection.SOUTHWEST;
            case 6 -> MapDirection.WEST;
            case 7 -> MapDirection.NORTHWEST;
            default -> throw new IllegalStateException("Unexpected value: " + orientationNum);
        };
    }
    private MoveDirection chooseDirection(){
        int turnsNumber = this.genotype.getRandomGene();
        if (turnsNumber == 0){
            return MoveDirection.FORWARD;
        }
        else if (turnsNumber == 4){
            return MoveDirection.BACKWARD;
        }
        for (int i = 0; i < turnsNumber; i++) {
            this.orientation = this.orientation.next();
        }
        return null;
    }
    public void move(){
        Vector2d newPosition;
        MoveDirection direction = chooseDirection();
        if (direction != null) {
            switch (direction) {
                case FORWARD -> {
                    newPosition = this.position.add(this.orientation.toUnitVector());
                    moveToPosition(newPosition);
                }
                case BACKWARD -> {
                    newPosition = this.position.substract(this.orientation.toUnitVector());
                    moveToPosition(newPosition);
                }
//            case LEFT -> this.orientation = this.orientation.previous();
//            case RIGHT -> this.orientation = this.orientation.next();
            }
        }
        this.energy -= moveEnergy;
    }
    private void moveToPosition(Vector2d position){
        if (map.canMoveTo(position)) {
            this.positionChanged(this.position, position);
            this.position = position;
        }
    }
    public Vector2d getPosition() {
        return this.position;
    }
    public int getEnergy() { return this.energy; }
    public Genotype getGenotype() { return this.genotype;}

    void addObserver(IPositionChangeObserver observer){
        observers.add(observer);
    }
    void removeObserver(IPositionChangeObserver observer){
        observers.remove(observer);
    }
    void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for (IPositionChangeObserver observer : observers) {
            observer.positionChanged(this, oldPosition, newPosition);
        }
    }

    public void eat(int energy){
        this.energy += energy;
    }
    public Animal reproduce(Animal other){
        Animal child = new Animal(this.position, this.map);
        child.genotype.inheritParentsGenotype(this, other);
        this.energy *= 0.75;
        other.energy *= 0.75;
        this.numOfChildren += 1;
        other.numOfChildren += 1;
        return child;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public int getNumOfChildren() {
        return numOfChildren;
    }
}

