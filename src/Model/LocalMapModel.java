package Model;

import javafx.scene.input.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Kevin Ni
 */
public class LocalMapModel implements MapModel {
    private final static DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");

    private MapTile[][] map;
    private Position player;
    private int goalsLeft;
    private int score;
    private String time;

    private List<ModelEventHandler<MapUpdateInfo>> listeners;

    public LocalMapModel(String fin){
        listeners = new ArrayList<>();
        time = "0:00";
        goalsLeft = 0;
        score = 0;

        //read map from file
        Scanner sc = null;
        try{
            sc = new Scanner(new FileReader(fin));

            String[] dim = sc.nextLine().split("\\s");
            int r = Integer.parseInt(dim[0]);
            int c = Integer.parseInt(dim[1]);

            map = new MapTile[r+2][c+2];

            MapTile wallTile = new MapTile(false, MapTile.MapItem.WALL);

            for(int j = 0; j <= c + 1; j++){
                map[0][j] = wallTile;
                map[r+1][j] = wallTile;
            }

            for(int i = 1; i <= r; i++){
                map[i][0] = wallTile;
                map[i][c + 1] = wallTile;
                String line = sc.nextLine();

                for(int j = 1; j <= c; j++){
                    switch(line.charAt(j - 1)){
                        case 'p':
                            map[i][j] = new MapTile(false, MapTile.MapItem.PLAYER);
                            player = new Position(j, i);
                            break;
                        case 'b':
                            map[i][j] = new MapTile(false, MapTile.MapItem.BOX);
                            break;
                        case 'w':
                            map[i][j] = wallTile;
                            break;
                        case 'g':
                            map[i][j] = new MapTile(true, MapTile.MapItem.GROUND);
                            goalsLeft++;
                            break;
                        case 'd':
                            map[i][j] = new MapTile(true, MapTile.MapItem.BOX);
                            break;
                        default:
                            map[i][j] = new MapTile(false, MapTile.MapItem.GROUND);
                            break;
                    }
                }
            }
        }
        catch(FileNotFoundException e) {
            System.err.println(String.format("File %s/%s not found", System.getProperty("user.dir"), fin));
        }
        finally{
            if (sc != null){
                sc.close();
            }
        }
    }

    public void handle(KeyEvent e) {
        //KEY EVENT HANDLER
        processInput(e.getCode());
    }

    public void processInput(KeyCode k){
        int x = 0;
        int y = 0;
        int oldx = player.getX();
        int oldy = player.getY();
        Position oldPosition = player;

        switch (k){
            case UP:
                y--; break;
            case DOWN:
                y++; break;
            case LEFT:
                x--; break;
            case RIGHT:
                x++; break;
        }
        Position newPosition = new Position(oldx + x, oldy + y);
        Position lookAhead = new Position(oldx + x + x, oldy + y + y);
        broadcast(oldPosition, newPosition, lookAhead);
    }

    private synchronized void broadcast(Position oldPosition, Position newPosition, Position lookAhead){
        if (validMove(newPosition, lookAhead)) {
            player = newPosition;
            score++;

            boolean pushedBox = getMapAt(newPosition).getItem() == MapTile.MapItem.BOX;

            setMapAt(oldPosition, MapTile.MapItem.GROUND);
            if(pushedBox){
                setMapAt(lookAhead, MapTile.MapItem.BOX);
            }
            setMapAt(newPosition, MapTile.MapItem.PLAYER);

            MapUpdateInfo info = new MapUpdateInfo(goalsLeft == 0);
            info.addChange(newPosition, getMapAt(newPosition));
            if(pushedBox) {
                info.addChange(lookAhead, getMapAt(lookAhead));
            }
            info.addChange(oldPosition, getMapAt(oldPosition));

            for(ModelEventHandler<MapUpdateInfo> listener : listeners) {
                listener.handle(info);
            }
        }
    }

    private synchronized boolean validMove(Position newPos, Position lookAhead) {
        MapTile item = getMapAt(newPos);

        switch (item.getItem()){
            case WALL:
            case PLAYER:
                return false;
            case BOX:
                return getMapAt(lookAhead).getItem() == MapTile.MapItem.GROUND;
            default:
                return true;
        }
    }

    private void setMapAt(Position pos, MapTile.MapItem item){
        MapTile tile = map[pos.getY()][pos.getX()];

        if(tile.getIsGoal() && tile.getItem() == MapTile.MapItem.BOX) {
            goalsLeft++;
        }
        tile.setItem(item);

        if(tile.getIsGoal() && tile.getItem() == MapTile.MapItem.BOX) {
            goalsLeft--;
        }
    }

    public MapTile getMapAt(Position pos){
        return map[pos.getY()][pos.getX()];
    }

    public int getScore() {
        return score;
    }

    public void setTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        this.time = TIME_FORMAT.format(cal.getTime());
    }

    public String getTime() {
        return time;
    }

    public int getHeight(){
        return map.length;
    }

    public int getWidth(){
        return getHeight() == 0 ? 0 : map[0].length;
    }

    public void subscribeModelUpdate(ModelEventHandler<MapUpdateInfo> listener){
        listeners.add(listener);
    }
}
