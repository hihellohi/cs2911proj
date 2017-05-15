package Model;

import javafx.scene.input.*;

import java.io.*;
import java.util.*;

import static Model.MapTile.MapItem.*;

/**
 * @author Kevin Ni
 */
public class LocalMapModel implements MapModel {
    private MapTile[][] map;
    private MapTile[][] startingMap;
    private Position player;
    private int goalsLeft;
    private int score;
    private long time;
    private List<MapUpdateInfo> moves;

    private List<ModelEventHandler<MapUpdateInfo>> listeners;

    public LocalMapModel(String fin){
        listeners = new ArrayList<>();
        time = 0;
        generateMap();
    }

    private void generateMap() {
        int width = 8;
        int height = 8;
        int g = new Random().nextInt();
//        int g = -895268333;
        System.out.println(g);
        MapGenerator generator = new MapGenerator(g, Settings.getInstance().getDifficulty());
        setUpMap(generator.generateMap(width, height));
    }

    private void setUpMap(MapTile[][] map) {
        moves = new ArrayList<>();
        goalsLeft = 0;
        score = 0;
//        loadFromFile(fin);
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x].getItem() == PLAYER) {
                    player = new Position(x, y);
                }
                if (map[y][x].getIsGoal() && map[y][x].getItem() != BOX) {
                    goalsLeft++;
                }
            }
        }
        this.map = map;
        this.map = copyMap();
        this.startingMap = copyMap();
    }

    private MapTile[][] copyMap() {
        MapTile[][] map = new MapTile[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (this.map[y][x].getIsGoal()) {
                    map[y][x] = new MapTile(true, this.map[y][x].getItem());
                }
                else {
                    map[y][x] = new MapTile(false, this.map[y][x].getItem());
                }
            }
        }
        return map;
    }

    private void loadFromFile(String fin) {
        //read map from file
        Scanner sc = null;
        try{
            sc = new Scanner(new FileReader(fin));

            String[] dim = sc.nextLine().split("\\s");
            int r = Integer.parseInt(dim[0]);
            int c = Integer.parseInt(dim[1]);

            map = new MapTile[r+2][c+2];

            MapTile wallTile = new MapTile(false, WALL);

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
                            map[i][j] = new MapTile(false, PLAYER);
                            player = new Position(j, i);
                            break;
                        case 'b':
                            map[i][j] = new MapTile(false, BOX);
                            break;
                        case 'w':
                            map[i][j] = wallTile;
                            break;
                        case 'g':
                            map[i][j] = new MapTile(true, GROUND);
                            goalsLeft++;
                            break;
                        case 'd':
                            map[i][j] = new MapTile(true, BOX);
                            break;
                        default:
                            map[i][j] = new MapTile(false, GROUND);
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
        switch (e.getCode()) {
            case U:
                undo();
                break;
            case R:
                reset();
                break;
            case N:
                generateNewMap();
                break;
            default:
                processInput(e.getCode());
        }
    }

    public void undo() {

    }

    public synchronized void generateNewMap() {
        generateMap();
        broadcastReset();
    }

    private synchronized void broadcastReset() {
        MapUpdateInfo info = new MapUpdateInfo(goalsLeft == 0);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                Position pos = new Position(x, y);
                info.addChange(pos, getMapAt(pos));
            }
        }

        for(ModelEventHandler<MapUpdateInfo> listener : listeners) {
            listener.handle(info);
        }
    }
    public void reset() {
        setUpMap(startingMap);
        broadcastReset();
        System.out.println("Reset map");
    }

    public void processInput(KeyCode k){
        if(goalsLeft == 0){
            return;
        }

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

            boolean pushedBox = getMapAt(newPosition).getItem() == BOX;

            setMapAt(oldPosition, GROUND);
            if(pushedBox){
                setMapAt(lookAhead, BOX);
            }
            setMapAt(newPosition, PLAYER);

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
                return getMapAt(lookAhead).getItem() == GROUND;
            default:
                return true;
        }
    }

    private void setMapAt(Position pos, MapTile.MapItem item){
        MapTile tile = map[pos.getY()][pos.getX()];

        if(tile.getIsGoal() && tile.getItem() == BOX) {
            goalsLeft++;
        }
        tile.setItem(item);

        if(tile.getIsGoal() && tile.getItem() == BOX) {
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
        this.time = time;
    }

    public long getTime() {
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
