package agh.simulator;

public class WrappedMap extends AbstractWorldMap implements IPositionChangeObserver{
    public WrappedMap(double jungleRatio, int width, int height) {
        super(jungleRatio, width, height);
    }
    @Override
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
        if (animal != null) {
            newPosition.x = (newPosition.x + (upperRight.x+1)) % (upperRight.x+1);
            newPosition.y = (newPosition.y + (upperRight.y+1)) % (upperRight.y+1);
            removeAnimal(animal, oldPosition);
            addAnimal(animal, newPosition);
        }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return true;
    }
}
