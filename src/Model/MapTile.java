package Model;

/**
 * @author Kevin Ni
 */

public class MapTile implements Cloneable{

    private boolean isGoal;
    private MapItem item;
    private int player;

    public MapTile(boolean isGoal, MapItem item){
        this(isGoal, item, -1);
    }

    public MapTile(boolean isGoal, MapItem item, int player){
        this.isGoal = isGoal;
        this.item = item;
        this.player = player;
    }

    public int getPlayer(){
        return player;
    }

    public boolean getIsGoal(){
        return isGoal;
    }

    public MapItem getItem(){
        return item;
    }

    void setTile(MapItem item, int player){
        this.item = item;
        this.player = player;
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
        return new MapTile(getIsGoal(), getItem(), getPlayer());
    }
}

