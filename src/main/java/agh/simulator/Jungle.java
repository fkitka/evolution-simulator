package agh.simulator;

public class Jungle{

    private final double jungleRatio;
    private final int mapHeight;
    private final int mapWidth;

    public Jungle(double jungleRatio, int mapWidth, int mapHeight) {
        this.jungleRatio = jungleRatio;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }
    public Vector2d getUpperRight() {
        int width = (int) (mapWidth * Math.sqrt(jungleRatio));
        int height = (int) (mapHeight * Math.sqrt(jungleRatio));
        int posX =  (mapWidth - width) / 2 + width ;
        int posY = (mapHeight - height) / 2 + height;
        return new Vector2d(posX, posY);
    }
    public Vector2d getLowerLeft() {
        int width = (int) (mapWidth * Math.sqrt(jungleRatio));
        int height = (int) (mapHeight * Math.sqrt(jungleRatio));
        int posX =  (mapWidth - width) / 2 ;
        int posY = (mapHeight - height) / 2;
        return new Vector2d(posX, posY);
    }

    public boolean isPositionInJungle(Vector2d position) {
        return (position.follows(getLowerLeft()) && position.precedes(getUpperRight()));
    }
}
