package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ni on 7/05/2017.
 */
public class MapUpdateInfo {
    private List<Pair<Position, MapItem>> coordinates;

    public MapUpdateInfo(){
        coordinates = new ArrayList<>();
    }

    void addChange(Position pos, MapItem v){
        coordinates.add(new Pair<>(pos, v));
    }

    public Iterable<Pair<Position, MapItem>> getCoordinates(){
        return coordinates;
    }
}
