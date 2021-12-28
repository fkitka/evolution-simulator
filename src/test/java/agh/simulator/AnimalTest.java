package agh.simulator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    @Test
    void reproduce() {
        AbstractWorldMap map = new BoundedMap(0.5, 10, 10, 100, 100);
        Animal animal1 = new Animal(new Vector2d(5, 5), map,100, 0);
        Animal animal2 = new Animal(new Vector2d(5, 5), map, 100, 0);
        map.place(animal1);
        map.place(animal2);
        animal1.getGenotype().genes = new int[]{0, 0, 1, 1, 1, 1, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7};
        animal2.getGenotype().genes = new int[]{0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7};

        animal2.move(); // animal2.energy = 75;
        Animal child = animal1.reproduce(animal2);

        assertTrue(Arrays.equals(new int[]{0, 0, 1, 1, 1, 1, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7},
                child.getGenotype().genes) ||
                Arrays.equals(new int[]{-0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7},
                child.getGenotype().genes));
    }

}