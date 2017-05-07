package Model;

import javafx.event.*;
import javafx.scene.input.*;

import java.io.*;
import java.util.*;

/**
 * @author Kevin Ni
 */
public class MapModel implements EventHandler<KeyEvent> {
    private char[][] map;
    private int charx;
    private int chary;

    private List<ModelEventHandler> listeners;

    public MapModel(String fin){
        map = new char[6][6];
        listeners = new ArrayList<ModelEventHandler>();

        //read map from file
        Scanner sc = null;
        try{
            sc = new Scanner(new FileReader(fin));

            int i = 0;
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                if(line.contains("c")){
                    chary = i;
                    charx = line.indexOf('c');
                }
                map[i++] = line.toCharArray();
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
        map[chary][charx] = '.';
        switch (e.getCode()){
            case UP:
                if (chary == 0) break;
                chary--;
                break;
            case DOWN:
                if (chary == 5) break;
                chary++;
                break;
            case LEFT:
                if (charx == 0) break;
                charx--;
                break;
            case RIGHT:
                if (charx == 5) break;
                charx++;
                break;
            default:
                map[chary][charx] = 'c';
                return;
        }

        map[chary][charx] = 'c';
        for(ModelEventHandler listener : listeners) {
            listener.Handle();
        }
    }

    public char getMapAt(int r, int c){
        return map[r][c];
    }

    public int getHeight(){
        return map.length;
    }

    public int getWidth(){
        return getHeight() == 0 ? 0 : map[0].length;
    }

    public void setOnModelUpdate(ModelEventHandler listener){
        listeners.add(listener);
    }
}
