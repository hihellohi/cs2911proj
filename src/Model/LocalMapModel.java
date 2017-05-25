package Model;

import javafx.scene.input.*;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import static Model.MapTile.MapItem.*;

/**
 * Creates a local model of the map.
 *
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

    /**
     * Create a new model of the map.
     *
     * @param nPlayers number of players planning to join the game
     * @param localMulti indicate if the game is local multiplayer
     * @pre nPlayers >= 0
     */
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

    /**
     * Create a new map model from a text file, used solely for the tutorial.
     *
     * @param path path to file with the map
     */
    public LocalMapModel(String path) throws FileNotFoundException {
        listeners = new HashSet<>();
        tutorial = true;
        loadFromFile(path);
    }

    /**
     * Generate a new map using a random seed.
     *
     */
    private void generateMap() {
        generateMap(new Random().nextInt());
    }

    /**
     * Generate a new map using a specified seed.
     *
     * @param seed used to generate a new map
     */
    private void generateMap(int seed) {
        System.out.println(seed);
        MapGenerator generator = new MapGenerator(seed, Settings.getInstance().getDifficulty());
        setUpMap(generator.generateMap(livePlayers));
    }

    /**
     * Set the map to the specified map.
     *
     * @param map a valid map
     */
    private void setUpMap(MapTile[][] map) {
        history = new Stack<>();
        playerHistory = new Stack<>();
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

    /**
     * Create a deep copy of the map.
     *
     * @return map
     */
    private MapTile[][] copyMap() {
        MapTile[][] map = new MapTile[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                map[y][x] = this.map[y][x].clone();
            }
        }
        return map;
    }

    /**
     * Read a map from a file.
     *
     * @param fin map in text file
     * @throws FileNotFoundException
     * @pre fin != "" and file exists
     */
    private void loadFromFile(String fin) throws FileNotFoundException {
        Scanner sc = null;
        try{
            sc = new Scanner(new FileReader(fin));

            String[] dim = sc.nextLine().split("\\s");
            int r = Integer.parseInt(dim[0]);
            int c = Integer.parseInt(dim[1]);
            players = new Position[1];

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
                            break;
                        case 'b':
                            map[i][j] = new MapTile(false, BOX);
                            break;
                        case 'w':
                            map[i][j] = wallTile;
                            break;
                        case 'g':
                            map[i][j] = new MapTile(true, GROUND);
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
            setUpMap(map);
        }
        finally{
            if (sc != null){
                sc.close();
            }
        }
    }

    /**
     * Remove player p from the map.
     *
     * @param p the player to be killed
     * @pre p >= 0
     */
    public synchronized void killPlayer(int p){
        setMapAt(players[p], GROUND, -1);
        livePlayers.remove(p);

        MapUpdateInfo info = new MapUpdateInfo(false, false);
        info.addChange(players[p], getMapAt(players[p]));

        broadcast(info);

        players[p] = null;
    }

    /**
     * Getter to return the local player.
     *
     * @return 0
     */
    public int getPlayer(){
        return 0;
    }

    /**
     * Handle key presses from the user(s).
     *
     * @param e key press event
     */
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

    /**
     * Return the move player 2 in local multiplayer is making.
     *
     * @param k is W, A, S, or D
     * @return directional equivalent
     */
    private KeyCode localPlayer2(KeyCode k){

        if(!localMulti){
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

    /**
     * Update the position of each player.
     *
     */
    private void setUpPlayers() {
        Pair<Integer, Position> prevPlayers = playerHistory.pop();
        players[prevPlayers.first()] = prevPlayers.second();
    }

    /**
     * Update the map to the previous move.
     *
     */
    private void broadcastPrevMove() {
        MapUpdateInfo prev = history.pop();
        for (Pair<Position, MapTile> prevTile : prev.getCoordinates()) {
            setMapAt(prevTile.first(), prevTile.second().getItem(), prevTile.second().getPlayer());
        }

        broadcast(prev);
    }

    /**
     * Undo the current move.
     *
     */
    public synchronized void undo() {
        if (history.size() < 1) {
            return;
        }
        setUpPlayers();
        broadcastPrevMove();
    }

    /**
     * Generate a new map.
     *
     */
    public synchronized void generateNewMap() {
        generateMap();
        broadcastMap();
    }

    /**
     * Broadcast the update of the map to the listeners.
     *
     */
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

    /**
     * Reset the map to the initial state.
     *
     */
    public synchronized void reset() {
        setUpMap(startingMap);
        broadcastMap();
    }

    /**
     * Process input from keypress.
     *
     * @param k UP, LEFT, DOWN, RIGHT
     * @param p the player making a move
     * @pre p >= 0
     */
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

    /**
     * Update the map according to the move made.
     *
     * @param oldPosition the current position of the player
     * @param newPosition the position the player wants to move to
     * @param lookAhead the tile after the new position
     * @param newDirection the direction the player will be facing
     * @param p the player making a move
     * @pre p >= 0
     */
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

    /**
     * Broadcast updates to the subscribed listeners.
     *
     * @param info updates to the map
     */
    private synchronized void broadcast(MapUpdateInfo info){
        for(Consumer<MapUpdateInfo> listener : listeners) {
            listener.accept(info);
        }
    }

    /**
     * Record the previous moves into a stack.
     *
     * @param oldPosition current position of the player
     * @param newPosition the original tile of the position the player wants to move to
     * @param lookAhead the original tile after the new position
     * @param p the player making a move
     * @pre p >= 0
     */
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

    /**
     * Check if a move is valid.
     *
     * @param newPos the position the player wants to move to
     * @param lookAhead the tile after the new position
     * @return isValid
     */
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

    /**
     * Set the tile at specified position.
     *
     * @param pos position of tile to be replaced
     * @param item item of new tile
     * @param player player making the move
     */
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

    /**
     * Return what tile is at specified position.
     *
     * @param pos position that player wants to see
     * @return map[y][x]
     */
    private MapTile getMapAt(Position pos){
        return map[pos.getY()][pos.getX()];
    }

    /**
     * Getter to return the height of the model.
     *
     * @return height
     */
    public int getHeight(){
        return map.length;
    }

    /**
     * Getter to return the width of the model.
     *
     * @return width
     */
    public int getWidth(){
        return getHeight() == 0 ? 0 : map[0].length;
    }

    /**
     * Subscribe a listener to a model's updates.
     *
     * @param listener view that needs information on model
     */
    public synchronized void subscribeModelUpdate(Consumer<MapUpdateInfo> listener){
        listeners.add(listener);
    }

    /**
     * Unsubscribe a listener from the model's updates.
     *
     * @param listener view that no longer needs information on model
     */
    public synchronized void unSubscribeModelUpdate(Consumer<MapUpdateInfo> listener){
        assert listeners.contains(listener);
        listeners.remove(listener);
    }

    /**
     * Broadcast nothing, close the model.
     *
     */
    public void close(){
        broadcast(null);
    }
}
