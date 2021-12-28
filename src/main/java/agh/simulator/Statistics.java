package agh.simulator;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Statistics {
    private final AbstractWorldMap map;
    protected Map<int[], Integer> genotypesOccurencies = new HashMap<>();
    public int[] dominantGenotype;

    public Statistics(AbstractWorldMap map){
        this.map = map;
    }
    public int getAnimalsSize(){
        return map.animalList.size();
    }
    public int getPlantsSize(){
        return map.plants.size();
    }

    public int getAverageNumOfChildren() {
        int childrenSum = 0;
        for (Animal animal :
                map.animalList) {
            childrenSum += animal.getNumOfChildren();
        }
        if (getAnimalsSize() > 0)
        return childrenSum / getAnimalsSize();
        else return 0;
    }

    public int getAverageEnergy() {
        int energySum = 0;
        for (Animal animal :
                map.animalList) {
            energySum += animal.getEnergy();
        }
        if (getAnimalsSize() > 0)
        return energySum / getAnimalsSize();
        else return 0;
    }

    public int getLifespan() {
        int lifeSpanSum = 0;
        for (Animal animal :
                map.deadAnimalsList) {
            lifeSpanSum += animal.getAge();
        }
        if(map.deadAnimalsList.size() > 0)
        return lifeSpanSum / map.deadAnimalsList.size();
        else return 0;
    }
    public Pair<int[], Integer> getDominantGenotype(){
        int maxOccurence = 0;
        int[] maxOccurenceGenes = dominantGenotype;
        for (int[] genes : genotypesOccurencies.keySet()) {
            if (maxOccurence < genotypesOccurencies.get(genes)){
                maxOccurence = genotypesOccurencies.get(genes);
                maxOccurenceGenes = genes;
                dominantGenotype = maxOccurenceGenes;
            }
        }
        return new Pair(maxOccurenceGenes, maxOccurence);
    }

    public void updateGenotypeStatistics(int[] genes) {
        if(this.genotypesOccurencies.get(genes) != null){
            int occurencyNum = this.genotypesOccurencies.get(genes);
            this.genotypesOccurencies.put(genes, occurencyNum + 1);
        }
        else{
            this.genotypesOccurencies.put(genes, 1);
        }
    }
}
