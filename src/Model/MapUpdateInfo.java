package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Ni
 */
public class MapUpdateInfo {

    private List<Pair<Position, MapTile>> coordinates;
    private boolean newMap;
    private boolean finished;

    public MapUpdateInfo(boolean newMap, boolean finished){
        coordinates = new ArrayList<>();
        this.newMap = newMap;
        this.finished = finished;
    }

    public void addChange(Position pos, MapTile v){
        coordinates.add(new Pair<>(pos, v));
    }

    public boolean isNewMap() {
        return newMap;
    }

    public boolean isFinished(){
        return finished;
    }

    public Iterable<Pair<Position, MapTile>> getCoordinates(){
        return coordinates;
    }

    public Pair<Position, MapTile> get(int index) {
        return coordinates.get(index);
    }

    public int size(){
        return coordinates.size();
    }
}
