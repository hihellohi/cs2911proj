package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Ni
 */
public class MapUpdateInfo {

    private List<Pair<Position, MapTile>> coordinates;
    private boolean finished;

    public MapUpdateInfo(boolean finished){
        coordinates = new ArrayList<>();
        this.finished = finished;
    }

    void addChange(Position pos, MapTile v){
        coordinates.add(new Pair<>(pos, v));
    }

    public boolean isFinished(){
        return finished;
    }

    public Iterable<Pair<Position, MapTile>> getCoordinates(){
        return coordinates;
    }
}
