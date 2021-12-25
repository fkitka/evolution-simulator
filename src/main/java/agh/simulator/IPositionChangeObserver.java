package agh.simulator;

public interface IPositionChangeObserver {
    /**
     *
     * @param animal
     *                  animal changing position
     * @param oldPosition
     *                  old position of an object
     * @param newPosition
     *                  new position of an object
     */
    void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition);
}
