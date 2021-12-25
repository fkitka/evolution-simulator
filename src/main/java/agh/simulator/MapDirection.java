package agh.simulator;

enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public String toString(){
        return switch (this) {
            case NORTH -> "Północ";
            case NORTHEAST -> "Północny-wschód";
            case EAST -> "Wschód";
            case SOUTHEAST -> "Południowy-wschód";
            case SOUTH -> "Południe";
            case SOUTHWEST -> "Południowy-zachód";
            case WEST -> "Zachód";
            case NORTHWEST -> "Północny-zachód";
        };
    }
    public MapDirection next(){
        return switch(this){
            case NORTH -> MapDirection.NORTHEAST;
            case NORTHEAST -> MapDirection.EAST;
            case EAST -> MapDirection.SOUTHEAST;
            case SOUTHEAST -> MapDirection.SOUTH;
            case SOUTH -> MapDirection.SOUTHWEST;
            case SOUTHWEST -> MapDirection.WEST;
            case WEST -> MapDirection.NORTHWEST;
            case NORTHWEST -> MapDirection.NORTH;
        };
    }
    public MapDirection previous(){
        return switch(this) {
            case NORTH -> MapDirection.NORTHWEST;
            case NORTHWEST -> MapDirection.WEST;
            case WEST -> MapDirection.SOUTHWEST;
            case SOUTHWEST -> MapDirection.SOUTH;
            case SOUTH -> MapDirection.SOUTHEAST;
            case SOUTHEAST -> MapDirection.EAST;
            case EAST -> MapDirection.NORTHEAST;
            case NORTHEAST -> MapDirection.NORTH;
        };
    }
    public Vector2d toUnitVector(){
        return switch(this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTHEAST -> new Vector2d(1,-1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHWEST -> new Vector2d(-1,-1);
            case WEST -> new Vector2d(-1, 0);
            case NORTHWEST -> new Vector2d(-1,1);
        };
    }
}
