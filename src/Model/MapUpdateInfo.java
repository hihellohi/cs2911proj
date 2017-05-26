package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to transmit model changes to subscribers.
 *
 * @author Kevin Ni
 */
public class MapUpdateInfo {

    private List<Pair<Position, MapTile>> coordinates;
    private boolean newMap;
    private boolean finished;

    /**
     * Create a new update.
     *
     * @param newMap indicate if the map is new
     * @param finished indicate if the map is in a finished state
     */
    public MapUpdateInfo(boolean newMap, boolean finished){
        coordinates = new ArrayList<>();
        this.newMap = newMap;
        this.finished = finished;
    }

    /**
     * Add changes to the map to the update.
     *
     * @param pos position of the change
     * @param v item at change
     * @pre pos != null, v != null
     */
    public void addChange(Position pos, MapTile v){
        coordinates.add(new Pair<>(pos, v));
    }

    /**
     * Return whether a map is a new map,
     * i.e. the map has been reset or a new game has started.
     *
     * @return newMap
     */
    public boolean isNewMap() {
        return newMap;
    }

    /**
     * Return whether the map is in a finished state,
     * i.e. every goal has been entered by a box.
     *
     * @return finished
     */
    public boolean isFinished(){
        return finished;
    }

    /**
     * Return the list of changes as an iterable.
     *
     * @return coordinates
     */
    public Iterable<Pair<Position, MapTile>> getCoordinates(){
        return coordinates;
    }

    /**
     * Get the change at the ith index.
     *
     * @param index
     * @pre index >= 0
     * @return coordinates.get(i)
     */
    public Pair<Position, MapTile> get(int index) {
        return coordinates.get(index);
    }

    /**
     * Return how many changes have been added to the update.
     *
     * @return coordinates.size()
     */
    public int size(){
        return coordinates.size();
    }
}
