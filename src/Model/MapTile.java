package Model;

/**
 * @author Kevin Ni
 */

public class MapTile{

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

    void setGoal(boolean goal){
        this.isGoal = goal;
    }

    void setItem(MapItem item){
        this.item = item;
    }

    public enum MapItem {
        GROUND,
        PLAYER,
        WALL,
        BOX,
    }
}

