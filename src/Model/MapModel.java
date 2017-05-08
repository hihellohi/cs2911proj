package Model;

import javafx.event.*;
import javafx.scene.input.*;

import java.io.*;
import java.util.*;

/**
 * @author Kevin Ni
 */
public class MapModel implements EventHandler<KeyEvent> {
    private List<MapItem[]> map;
    private Position player;

    private List<ModelEventHandler<MapUpdateInfo>> listeners;

    public MapModel(String fin){
        map = new ArrayList<>();
        listeners = new ArrayList<>();

        //read map from file
        Scanner sc = null;
        try{
            sc = new Scanner(new FileReader(fin));

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                MapItem[] row = new MapItem[line.length()];

                int j = 0;
                for(char c: line.toCharArray()){
                    switch(c){
                        case '.':
                            row[j] = MapItem.GROUND;
                            break;
                        case 'p':
                            row[j] = MapItem.PLAYER;
                            player = new Position(j, map.size());
                            break;
                        case 'b':
                            row[j] = MapItem.BOX;
                            break;
                        case 'w':
                            row[j] = MapItem.WALL;
                            break;
                        case 'g':
                            row[j] = MapItem.GOAL;
                            break;
                        case 'd':
                            row[j] = MapItem.GOALBOX;
                            break;
                    }
                    j++;
                }
                map.add(row);
            }
        }
        catch(FileNotFoundException e) {
            System.err.println(String.format("File %s/%s not found",System.getProperty("user.dir") ,fin));
        }
        finally{
            if (sc != null){
                sc.close();
            }
        }
    }

    public void handle(KeyEvent e) {
        //KEY EVENT HANDLER
        MapUpdateInfo info = new MapUpdateInfo();

        int x = player.getX();
        int y = player.getY();
        Position oldPosition = new Position(x, y);

        switch (e.getCode()){
            case UP:
                y--; break;
            case DOWN:
                y++; break;
            case LEFT:
                x--; break;
            case RIGHT:
                x++; break;
        }
        Position newPosition = new Position(x, y);
        if (!validMove(newPosition)) {
            return;
        }
        player = newPosition;

        info.addChange(oldPosition, MapItem.GROUND);
        setMapAt(oldPosition, MapItem.GROUND);

        info.addChange(newPosition, MapItem.PLAYER);
        setMapAt(newPosition, MapItem.PLAYER);

        for(ModelEventHandler<MapUpdateInfo> listener : listeners) {
            listener.handle(info);
        }
    }

    public boolean validMove(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        if (y == -1 || y == getHeight() || x == -1 || x == getWidth()) {
            return false;
        }

        MapItem item = getMapAt(pos);
        if (item == MapItem.WALL) {
                return false;
        }

        return true;
    }

    public MapItem getMapAt(Position pos){
        return map.get(pos.getY())[pos.getX()];
    }

    public void setMapAt(Position pos, MapItem item){
        map.get(pos.getY())[pos.getX()] = item;
    }

    public int getHeight(){
        return map.size();
    }

    public int getWidth(){
        return getHeight() == 0 ? 0 : map.get(0).length;
    }

    public void setOnModelUpdate(ModelEventHandler<MapUpdateInfo> listener){
        listeners.add(listener);
    }
}
