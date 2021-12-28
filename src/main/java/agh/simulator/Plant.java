package agh.simulator;

public class Plant {
    private final int energy;
    public Plant(int energy){
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "*";
    }

    public int getEnergy() {
        return energy;
    }
}
