package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ni on 7/05/2017.
 */
public class MapUpdateInfo {
    private List<Pair<Position, MapTile>> coordinates;

    public MapUpdateInfo(){
        coordinates = new ArrayList<>();
    }

    void addChange(Position pos, MapTile v){
        coordinates.add(new Pair<>(pos, v));
    }

    public Iterable<Pair<Position, MapTile>> getCoordinates(){
        return coordinates;
    }
}
