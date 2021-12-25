package agh.simulator;

public class BoundedMap extends AbstractWorldMap{
    public BoundedMap(double jungleRatio, int width, int height) {
        super(jungleRatio, width, height);
    }

    public boolean canMoveTo(Vector2d position) {
        return isInBounds(position);
    }

}
