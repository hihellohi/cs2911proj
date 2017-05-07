package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ni on 7/05/2017.
 */
public class MapUpdateInfo {
    private List<Triplet<Integer, Integer, MapItem>> coordinates;

    public MapUpdateInfo(){
        coordinates = new ArrayList<>();
    }

    void addCoordinate(int r, int c, MapItem v){
        coordinates.add(new Triplet<>(new Integer(r), new Integer(c), v));
    }

    public Iterable<Triplet<Integer, Integer, MapItem>> getCoordinates(){
        return coordinates;
    }
}
