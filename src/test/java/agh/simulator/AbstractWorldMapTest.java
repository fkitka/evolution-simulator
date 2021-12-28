package agh.simulator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractWorldMapTest {

    @Test
    void removeDead() {
        AbstractWorldMap map = new WrappedMap(0.1 ,10, 10, 10, 100);
        Animal animal1 = new Animal(new Vector2d(5,5), map, 100, 20);
        Animal animal2 = new Animal(new Vector2d(5,5), map, 100, 20);
        Animal animal3 = new Animal(new Vector2d(1,2), map, 100, 20);
        for (int i = 0; i < 10; i++) {
            animal1.move();
            animal2.move();
            animal3.move();
        }
        map.removeDead();

        assertEquals(0, map.animalList.size());
        assertEquals(0, map.animals.size());
    }

    @Test
    void eatPlants() {
        AbstractWorldMap map = new WrappedMap(0.1, 10, 10, 10, 100);
        Animal animal1 = new Animal(new Vector2d(5,5), map, 100, 20);
        Animal animal2 = new Animal(new Vector2d(5, 5), map, 100, 20);
        Plant plant = new Plant(10);
        int startEnergy = 100;
        int plantEnergy = 100;

        map.place(animal1);
        map.place(animal2);
        map.plants.put(new Vector2d(5,5), plant);
        map.eatPlants();

        assertEquals(startEnergy + plantEnergy/2, animal1.getEnergy());
        assertEquals(startEnergy + plantEnergy/2, animal2.getEnergy());
    }

    @Test
    void reproduction() {
        AbstractWorldMap map = new WrappedMap(0.1, 10, 10, 20, 100);
        Animal animal1 = new Animal(new Vector2d(5,5), map, 100, 20);
        Animal animal2 = new Animal(new Vector2d(5, 5), map, 100, 20);
        Animal animal3 = new Animal(new Vector2d(5, 5), map, 100, 20);
        Animal animal4 = new Animal(new Vector2d(5, 5), map, 100, 20);

        map.place(animal1);
        map.place(animal2);
        map.place(animal3);
        map.place(animal4);
        System.out.println(map.animalList.size());
        animal1.eat(100);
        animal2.eat(100);
        animal3.eat(100);
        animal4.eat(50);
        map.reproduction();

        assertEquals(5, map.animalList.size());
        assertTrue(animal1.getNumOfChildren() == 1 || animal2.getNumOfChildren() == 1
                || animal3.getNumOfChildren() == 1);
        assertEquals(0, animal4.getNumOfChildren());
    }
}