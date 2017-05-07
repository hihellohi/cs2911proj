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
    private int charx;
    private int chary;

    private List<ModelEventHandler<MapUpdateInfo>> listeners;

    public MapModel(String fin){
        map = new ArrayList<>();
        listeners = new ArrayList<>();

        //read map from file
        Scanner sc = null;
        try{
            sc = new Scanner(new FileReader(fin));

            int i = 0;
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                MapItem[] row = new MapItem[line.length()];

                int j = 0;
                for(char c: line.toCharArray()){
                    switch(c){
                        case '.':
                            row[j] = MapItem.EMPTY;
                            break;
                        case 'c':
                            row[j] = MapItem.CHARACTER;
                            chary = map.size();
                            charx = j;
                            break;
                    }
                    j++;
                }
                map.add(row);
            }
        }
        catch(FileNotFoundException e)
        {
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

        map.get(chary)[charx] = MapItem.EMPTY;
        info.addCoordinate(chary, charx, MapItem.EMPTY);

        switch (e.getCode()){
            case UP:
                chary--;
                break;
            case DOWN:
                chary++;
                break;
            case LEFT:
                charx--;
                break;
            case RIGHT:
                charx++;
                break;
            default:
                map.get(chary)[charx] = MapItem.CHARACTER;
                return;
        }

        map.get(chary)[charx] = MapItem.CHARACTER;
        info.addCoordinate(chary, charx, MapItem.CHARACTER);

        for(ModelEventHandler listener : listeners) {
            listener.Handle(info);
        }
    }

    public MapItem getMapAt(int r, int c){
        return map.get(r)[c];
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
