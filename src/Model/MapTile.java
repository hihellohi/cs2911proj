package Model;

/**
 * Class to represent a tile on the map.
 *
 * @author Kevin Ni
 */
public class MapTile implements Cloneable{

    private boolean isGoal;
    private MapItem item;
    private int player;

    /**
     * Create a new tile.
     *
     * @param isGoal indicate if the tile is a goal tile
     * @param item item that the tile will be
     * @pre item != null
     */
    public MapTile(boolean isGoal, MapItem item){
        this(isGoal, item, -1);
    }

    /**
     * Create a new tile.
     *
     * @param isGoal indicate if the tile is a goal tile
     * @param item item that the tile will be
     * @param player player making the move
     * @pre player >= 0, item != null
     */
    public MapTile(boolean isGoal, MapItem item, int player){
        this.isGoal = isGoal;
        this.item = item;
        this.player = player;
    }

    /**
     * Getter to return the current player.
     *
     * @return player
     */
    public int getPlayer(){
        return player;
    }

    /**
     * Getter to return whether or not a tile is a goal.
     *
     * @return isGoal
     */
    public boolean getIsGoal(){
        return isGoal;
    }

    /**
     * Getter to return the item which the tile represents.
     *
     * @return item
     */
    public MapItem getItem(){
        return item;
    }

    /**
     * Setter to update the tile.
     *
     * @param item item that is going into the tile
     * @param player player making the move
     * @pre player >= 0, item != null
     */
    void setTile(MapItem item, int player){
        this.item = item;
        this.player = player;
    }

    /**
     * Enum which holds the possible tile items.
     *
     */
    public enum MapItem {
        GROUND,
        WALL,
        BOX,
        PLAYER_NORTH,
        PLAYER_EAST,
        PLAYER_SOUTH,
        PLAYER_WEST
    }

    /**
     * Return a deep copy of the tile.
     *
     * @return new MapTile
     */
    @Override public MapTile clone(){
        return new MapTile(getIsGoal(), getItem(), getPlayer());
    }
}

