package agh.simulator;

public class BoundedMap extends AbstractWorldMap{
    public BoundedMap(double jungleRatio, int width, int height,  int plantEnergy, int initialEnergy) {
        super(jungleRatio, width, height, plantEnergy,  initialEnergy);
    }

    public boolean canMoveTo(Vector2d position) {
        return isInBounds(position);
    }

}
