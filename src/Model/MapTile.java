package Model;

/**
 * @author Kevin Ni
 */

public class MapTile implements Cloneable{

    private boolean isGoal;
    private MapItem item;

    public MapTile(boolean isGoal, MapItem item){
        this.isGoal = isGoal;
        this.item = item;
    }

    public boolean getIsGoal(){
        return isGoal;
    }

    public MapItem getItem(){
        return item;
    }

    void setItem(MapItem item){
        this.item = item;
    }

    public enum MapItem {
        GROUND,
        WALL,
        BOX,
        PLAYER_NORTH,
        PLAYER_EAST,
        PLAYER_SOUTH,
        PLAYER_WEST
    }

    @Override public MapTile clone(){
        return new MapTile(getIsGoal(), getItem());
    }
}

