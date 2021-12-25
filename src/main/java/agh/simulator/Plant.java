package agh.simulator;

public class Plant {
    private final Vector2d position;
    private final int energy = 100;
    public Plant(Vector2d position){
        this.position = position;
    }

    @Override
    public String toString() {
        return "*";
    }

    public int getEnergy() {
        return energy;
    }
}
