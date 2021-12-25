package agh.simulator;

import java.util.Arrays;

public class Genotype {
    private int size = 32;
    private int range = 8;
    public int genes[];
    public Genotype(){
        this.genes = fillGenes();
    }

    private int[] fillGenes() {
        int[] randomArray = new int[size];
        for (int i = 0; i < size; i++) {
            randomArray[i] = (int) ((Math.random() * (range)));
        }
        Arrays.sort(randomArray);
        return randomArray;
    }
    public int getRandomGene(){
        return this.genes[(int) ((Math.random() * (size)))];
    }
    public void inheritParentsGenotype(Animal parent1, Animal parent2){
        double energy1 = parent1.getEnergy();
        double energy2 = parent2.getEnergy();
        double genesFract;
        if (energy1 > energy2){
            genesFract = (energy1/(energy1 + energy2)) * size;
        }
        else {
            genesFract = (energy2/(energy1 + energy2)) * size;
        }
        int numOfGenes = (int) genesFract;
        int side = switch(chooseSide()){
            case 0 -> numOfGenes; // left part first
            case 1 -> size; // right part first
            default -> throw new IllegalStateException("Unexpected value: " + chooseSide());
        };
        if (energy1 > energy2){
            copyGenes(parent1.getGenotype(), parent2.getGenotype(), side, numOfGenes);
        }
        else{
            copyGenes(parent2.getGenotype(), parent1.getGenotype(), side, numOfGenes);
        }
        Arrays.sort(this.genes);
    }
    private void copyGenes(Genotype genotype1, Genotype genotype2, int side, int numOfGenes) {
        for (int i = 0; i < size; i++) {
            if (i >= side - numOfGenes && i < side)
                this.genes[i] = genotype1.genes[i];
            else
                this.genes[i] = genotype2.genes[i];
        }
    }
    private int chooseSide() {
        return (int) (Math.random() * (1));
    }
}