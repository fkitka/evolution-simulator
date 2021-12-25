package agh.simulator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WrappedMapTest {
    @Test
    void wrappedBounds(){
        AbstractWorldMap map = new WrappedMap(0.1, 10, 10);
        Animal animal = new Animal(new Vector2d(8,8), map);
        map.place(animal);

        animal.getGenotype().genes = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for (int i = 0; i < 100; i++) {
            animal.move();
        }
        assertTrue(animal.getPosition().follows(map.getLowerLeft()) && animal.getPosition().precedes(map.getUpperRight()));
    }
}