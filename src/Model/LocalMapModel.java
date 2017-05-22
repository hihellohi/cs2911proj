package Model;

import javafx.scene.input.*;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import static Model.MapTile.MapItem.*;

/**
 * @author Kevin Ni
 */
public class LocalMapModel implements MapModel {
    private MapTile[][] map;
    private MapTile[][] startingMap;
    private Position[] players;
    private Set<Integer> livePlayers;
    private int goalsLeft;
    private boolean tutorial;
    private Stack<MapUpdateInfo> history;
    private Stack<Pair<Integer, Position>> playerHistory;
    private Set<Consumer<MapUpdateInfo>> listeners;
    private boolean localMulti;

    public LocalMapModel(int nPlayers, boolean localMulti){
        this.localMulti = localMulti;
        tutorial = false;
        players = new Position[nPlayers];
        listeners = new HashSet<>();

        livePlayers = new HashSet<>();
        for(int i = 0; i < nPlayers; i++){
            livePlayers.add(i);
        }
        generateMap(new Random().nextInt());
    }

    /** Used solely for the tutorial
     *
     * @param path path to file with the map
     */
    public LocalMapModel(String path) throws FileNotFoundException {
        listeners = new HashSet<>();
        tutorial = true;
        loadFromFile(path);
    }

    private void generateMap() {
        generateMap(new Random().nextInt());
    }

    private void generateMap(int seed) {
        System.out.println(seed);
        MapGenerator generator = new MapGenerator(seed, Settings.getInstance().getDifficulty());
        history = new Stack<>();
        playerHistory = new Stack<>();
        setUpMap(generator.generateMap(livePlayers));
    }

    private void setUpMap(MapTile[][] map) {
        goalsLeft = 0;
        int p = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                MapTile tile = map[y][x];
                if (tile.getItem() == PLAYER_SOUTH) {
                    players[tile.getPlayer()] = new Position(x, y);
                }
                if (tile.getIsGoal() && tile.getItem() != BOX) {
                    goalsLeft++;
                }
            }
        }
        this.map = map;
        this.startingMap = copyMap();
    }

    private MapTile[][] copyMap() {
        MapTile[][] map = new MapTile[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                map[y][x] = this.map[y][x].clone();
            }
        }
        return map;
    }

    private void loadFromFile(String fin) throws FileNotFoundException {
        history = new Stack<>();
        playerHistory = new Stack<>();
        Scanner sc = null;
        try{
            int p = 0;
            sc = new Scanner(new FileReader(fin));

            String[] dim = sc.nextLine().split("\\s");
            int r = Integer.parseInt(dim[0]);
            int c = Integer.parseInt(dim[1]);
            int numPlayers = Integer.parseInt(dim[2]);
            players = new Position[numPlayers];

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
                            map[i][j] = new MapTile(false, PLAYER_SOUTH, getPlayer());
                            players[p++] = new Position(j, i);
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

            this.startingMap = copyMap();
        }
        finally{
            if (sc != null){
                sc.close();
            }
        }
    }

    public synchronized void killPlayer(int p){
        setMapAt(players[p], GROUND, -1);
        livePlayers.remove(p);

        MapUpdateInfo info = new MapUpdateInfo(false, false);
        info.addChange(players[p], getMapAt(players[p]));

        broadcast(info);

        players[p] = null;
    }

    public int getPlayer(){
        return 0;
    }

    public void handle(KeyEvent e) {
        //KEY EVENT HANDLER
        KeyCode p2 = localPlayer2(e.getCode());
        if(p2 != null){
            processInput(p2, 1);
            return;
        }

        switch (e.getCode()) {
            case U:
                undo();
                break;
            case R:
                reset();
                break;
            case N:
                if (!tutorial) {
                    generateNewMap();
                }
                break;
            default:
                processInput(e.getCode(), getPlayer());
        }
    }

    private KeyCode localPlayer2(KeyCode k){

        if(localMulti){
            return null;
        }

        switch (k) {
            case W:
                return KeyCode.UP;
            case A:
                return KeyCode.LEFT;
            case S:
                return KeyCode.DOWN;
            case D:
                return KeyCode.RIGHT;
            default:
                return null;
        }
    }

    private void setUpPlayers() {
        Pair<Integer, Position> prevPlayers = playerHistory.pop();
        players[prevPlayers.first()] = prevPlayers.second();
    }

    private void broadcastPrevMove() {
        MapUpdateInfo prev = history.pop();
        for (Pair<Position, MapTile> prevTile : prev.getCoordinates()) {
            setMapAt(prevTile.first(), prevTile.second().getItem(), prevTile.second().getPlayer());
        }

        broadcast(prev);
    }

    public synchronized void undo() {
        if (history.size() < 1) {
            return;
        }
        setUpPlayers();
        broadcastPrevMove();
    }

    public synchronized void generateNewMap() {
        generateMap();
        broadcastMap();
    }

    public synchronized void broadcastMap() {
        MapUpdateInfo info = new MapUpdateInfo(true, goalsLeft == 0);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                Position pos = new Position(x, y);
                info.addChange(pos, getMapAt(pos));
            }
        }

        broadcast(info);
    }

    public synchronized void reset() {
        setUpMap(startingMap);
        broadcastMap();
        history.removeAllElements();
        playerHistory.removeAllElements();
    }

    public synchronized void processInput(KeyCode k, int p){
        if(goalsLeft == 0){
            return;
        }

        int x = 0;
        int y = 0;
        int oldx = players[p].getX();
        int oldy = players[p].getY();
        Position oldPosition = players[p];
        MapTile.MapItem newDirection ;

        switch (k){
            case UP:
                newDirection = PLAYER_NORTH;
                y--; break;
            case DOWN:
                newDirection = PLAYER_SOUTH;
                y++; break;
            case LEFT:
                newDirection = PLAYER_WEST;
                x--; break;
            case RIGHT:
                newDirection = PLAYER_EAST;
                x++; break;
            default:
                return;
        }
        Position newPosition = new Position(oldx + x, oldy + y);
        Position lookAhead = new Position(oldx + x + x, oldy + y + y);
        makeMove(oldPosition, newPosition, lookAhead, newDirection, p);
    }


    private synchronized void makeMove(Position oldPosition,
                                       Position newPosition,
                                       Position lookAhead,
                                       MapTile.MapItem newDirection,
                                       int p){

        if (validMove(newPosition, lookAhead)) {
            recordHistory(oldPosition, newPosition, lookAhead, p);
            players[p] = newPosition;

            boolean pushedBox = getMapAt(newPosition).getItem() == BOX;

            setMapAt(oldPosition, GROUND, -1);
            setMapAt(newPosition, newDirection, p);
            if(pushedBox){
                setMapAt(lookAhead, BOX, -1);
            }

            MapUpdateInfo info = new MapUpdateInfo(false, goalsLeft == 0);

            info.addChange(oldPosition, getMapAt(oldPosition));
            info.addChange(newPosition, getMapAt(newPosition));
            if(pushedBox) {
                info.addChange(lookAhead, getMapAt(lookAhead));
            }

            broadcast(info);
        }
        else{
            MapUpdateInfo info = new MapUpdateInfo(false, false);
            setMapAt(oldPosition, newDirection, p);
            info.addChange(oldPosition, getMapAt(oldPosition));

            broadcast(info);
        }
    }

    private synchronized void broadcast(MapUpdateInfo info){
        for(Consumer<MapUpdateInfo> listener : listeners) {
            listener.accept(info);
        }
    }

    private synchronized void recordHistory(Position oldPosition,
                                            Position newPosition,
                                            Position lookAhead,
                                            int p){

        playerHistory.push(new Pair<>(p, players[p]));
        MapUpdateInfo prevInfo = new MapUpdateInfo(false, goalsLeft == 0);
        prevInfo.addChange(oldPosition, getMapAt(oldPosition).clone());
        prevInfo.addChange(newPosition, getMapAt(newPosition).clone());
        prevInfo.addChange(lookAhead, getMapAt(lookAhead).clone());
        history.push(prevInfo);
    }

    private synchronized boolean validMove(Position newPos, Position lookAhead) {
        MapTile item = getMapAt(newPos);

        switch (item.getItem()){
            case WALL:
            case PLAYER_NORTH:
            case PLAYER_EAST:
            case PLAYER_SOUTH:
            case PLAYER_WEST:
                return false;
            case BOX:
                return getMapAt(lookAhead).getItem() == GROUND;
            default:
                return true;
        }
    }

    private synchronized void setMapAt(Position pos, MapTile.MapItem item, int player){
        MapTile tile = map[pos.getY()][pos.getX()];

        if(tile.getIsGoal() && tile.getItem() == BOX) {
            goalsLeft++;
        }
        tile.setTile(item, player);

        if(tile.getIsGoal() && tile.getItem() == BOX) {
            goalsLeft--;
        }
    }

    private MapTile getMapAt(Position pos){
        return map[pos.getY()][pos.getX()];
    }

    public int getHeight(){
        return map.length;
    }

    public int getWidth(){
        return getHeight() == 0 ? 0 : map[0].length;
    }

    public synchronized void subscribeModelUpdate(Consumer<MapUpdateInfo> listener){
        listeners.add(listener);
    }

    public synchronized void unSubscribeModelUpdate(Consumer<MapUpdateInfo> listener){
        assert listeners.contains(listener);
        listeners.remove(listener);
    }

    public void close(){
        broadcast(null);
    }
}
