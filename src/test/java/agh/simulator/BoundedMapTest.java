package agh.simulator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundedMapTest {
    @Test
    void testBounds(){
        AbstractWorldMap map = new BoundedMap(0.1, 10, 10);
        Animal animal = new Animal(new Vector2d(8,8), map);
        map.place(animal);

        animal.getGenotype().genes = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for (int i = 0; i < 100; i++) {
            animal.move();
        }
        System.out.println(animal.getPosition());
        assertTrue(animal.getPosition().x == map.lowerLeft.x || animal.getPosition().x == map.upperRight.x ||
                animal.getPosition().y == map.lowerLeft.y || animal.getPosition().y == map.upperRight.y);
    }
}