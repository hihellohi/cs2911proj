package Model;

import javafx.scene.input.KeyCode;

import java.util.*;

import static javafx.scene.input.KeyCode.*;

/**
 * This class generates all maps (single-player and multi-player).
 * It is deterministic on the seed provided to the constructor.
 */
public class MapGenerator {
    private Random generator;
    private Settings.Difficulty difficulty;

    /**
     * Create a new MapGenerator from a seed and difficulty level
     *
     * @param seed the seed used to seed the internal random number generator
     * @param difficulty one of three difficulty levels
     * @pre difficulty != null
     */
    public MapGenerator(long seed, Settings.Difficulty difficulty) {
        this.generator = new Random(seed);
        this.difficulty = difficulty;
    }

    /** Generates a new map
     *
     * @param players players in the map
     * @pre numPlayers <= 16
     * @return a two-dimensional array of MapTiles representing the map
     */
    public MapTile[][] generateMap(Set<Integer> players) {
        MapTile[][] map;
        do {
            map = makeMap(players);
        } while (!isMapGood(map));
        return map;
    }

    /**
     * Does the bulk of the work of generating a map. Not guaranteed to produce a "good" map, but will
     * always produce a fully solvable map.
     *
     * @param livePlayers
     * @pre livePlayers.size() <= 16
     * @return newly generated map
     */
    private MapTile[][] makeMap(Set<Integer> livePlayers) {
        int walkLength = 0;
        int maxNumberOfBoxes = 0;
        int maxGeneratingPoint = 0;
        int width = 0;
        int height = 0;
        switch (difficulty) {
            case EASY:
                walkLength = 40;
                maxNumberOfBoxes = 10;
                maxGeneratingPoint = 20;
                width = 7;
                height = 7;
                break;
            case MEDIUM:
                walkLength = 40;
                maxNumberOfBoxes = 20;
                maxGeneratingPoint = 20;
                width = 8;
                height = 8;
                break;
            case HARD:
                walkLength = 60;
                maxNumberOfBoxes = 30;
                maxGeneratingPoint = 30;
                width = 10;
                height = 10;
                break;
        }

        System.out.println(difficulty);
        MapTile[][] map = initializeEmptyMap(width, height);

        List<Position> players = placePlayers(map, livePlayers.size());
        // copy over the starting positions of each player
        List<Position> startingPositions = new ArrayList<>(players);

        // generate the points on the path that we will drop the boxes
        List<Integer> all = new ArrayList<>();
        Set<Integer> indexes = new HashSet<>();

        for (int i = 0; i < maxGeneratingPoint; i++) {
            all.add(i);
        }
        for (int i = 0; i < maxNumberOfBoxes; i++) {
            int index = generator.nextInt(all.size());
            indexes.add(all.get(index));
            all.remove(index);
        }

        // walk randomly around and drop boxes randomly in front of player
        List<Position> boxPositions = new ArrayList<>();
        List<Position> path = new ArrayList<>();
        path.addAll(startingPositions);
        KeyCode[] directions = {UP, DOWN, LEFT, RIGHT};
        for (int i = 0; i < walkLength; i++) {
            int playerIndex = generator.nextInt(livePlayers.size());
            Position player = players.get(playerIndex);
            KeyCode move = directions[generator.nextInt(4)];

            Pair<Position, Position> positions = getPositions(move, player);
            Position newPosition = positions.first();
            Position lookAhead = positions.second();

            if (indexes.contains(i) &&
                    (getMapAt(map, newPosition).getItem() == MapTile.MapItem.GROUND) &&
                    !boxPositions.contains(newPosition) &&
                    !path.contains(newPosition)) {
                setMapAt(map, newPosition, MapTile.MapItem.BOX);
                boxPositions.add(newPosition);
            }

            if (isValidMove(map, newPosition, lookAhead)) {
                makeMove(map, player, newPosition, lookAhead);
                players.set(playerIndex, newPosition);
                path.add(newPosition);
            }
        }

        for (Position player: players) {
            setMapAt(map, player, MapTile.MapItem.GROUND);
        }

        // the final position of boxes will be their goal states
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                Position pos = new Position(x, y);
                if (map[y][x].getItem() == MapTile.MapItem.BOX) {
                    map[y][x] = new MapTile(true, MapTile.MapItem.GROUND);
                }
                else if (!path.contains(pos) && !boxPositions.contains(pos)) {
                    setMapAt(map, pos, MapTile.MapItem.WALL);
                }
            }
        }

        int i = 0;
        for(int player : livePlayers){
            setMapAt(map, startingPositions.get(i++), MapTile.MapItem.PLAYER_SOUTH, player);
        }

        // reset the position of boxes at where they were initially placed
        for (Position pos: boxPositions) {
            setMapAt(map, pos, MapTile.MapItem.BOX);
        }
        return map;
    }

    /**
     * A heuristic function used to determine if a map is considered "good".
     *
     * @param map to evaluate
     * @return true if the map is considered "good" else false
     */
    private boolean isMapGood(MapTile[][] map) {
        int height = map.length;
        int width = map[0].length;
        int nonGoalBoxes = 0;
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                Position pos = new Position(x, y);
                MapTile tile = getMapAt(map, pos);
                if (tile.getItem() == MapTile.MapItem.BOX && !tile.getIsGoal()) {
                    nonGoalBoxes++;
                }
            }
        }
        int requiredNonGoalBoxes = 0;
        switch (difficulty) {
            case EASY:
                requiredNonGoalBoxes = 3; break;
            case MEDIUM:
                requiredNonGoalBoxes = 3; break;
            case HARD:
                requiredNonGoalBoxes = 6; break;
        }
        if (nonGoalBoxes < requiredNonGoalBoxes) {
            return false;
        }
        return true;
    }

    /**
     * Helper function to simulate a move of the player on a map that is currently being generated
     *
     * @param map map to work on
     * @param oldPosition position of the player currently
     * @param newPosition requested new position of the player
     * @param lookAhead the new position that a box would be in if this move pushes a box
     */
    private void makeMove(
            MapTile[][] map, Position oldPosition, Position newPosition, Position lookAhead
    ) {
        boolean pushedBox = getMapAt(map, newPosition).getItem() == MapTile.MapItem.BOX;

        setMapAt(map, oldPosition, MapTile.MapItem.GROUND);
        if(pushedBox){
            setMapAt(map, lookAhead, MapTile.MapItem.BOX);
        }
        setMapAt(map, newPosition, MapTile.MapItem.PLAYER_SOUTH, 0);
    }

    /**
     * Helper function to get the new positions of the player when a certain move is made
     *
     * @param move direction of move
     * @param player current position of player
     * @return a Pair consisting of the new player position and the position a box pushed by this move would move to
     */
    private Pair<Position, Position> getPositions(KeyCode move, Position player) {
        int x = 0;
        int y = 0;
        int oldx = player.getX();
        int oldy = player.getY();

        switch (move){
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
        return new Pair<>(newPosition, lookAhead);
    }

    /**
     * Helper function to determine if a move is valid
     *
     * @param map to operate on
     * @param newPosition requested new position of player
     * @param lookAhead position a box would move to if the move was made
     * @return true if the move results in the player actually changing position else false
     */
    private boolean isValidMove(MapTile[][] map, Position newPosition, Position lookAhead) {
        MapTile item = getMapAt(map, newPosition);

        switch (item.getItem()){
            case WALL:
            case PLAYER_SOUTH:
                return false;
            case BOX:
                return getMapAt(map, lookAhead).getItem() == MapTile.MapItem.GROUND;
            default:
                return true;
        }
    }

    /**
     * Create and return a new map that is completely empty, except surrounded by walls.
     *
     * @param width of the new map
     * @param height of the new map
     * @return the new, empty map
     */
    private MapTile[][] initializeEmptyMap(int width, int height) {
        MapTile[][] map = new MapTile[width][height];
        MapTile wallTile = new MapTile(false, MapTile.MapItem.WALL);

        // form walls around the map
        for (int j = 0; j < width; j++) {
            map[0][j] = wallTile;
            map[height - 1][j] = wallTile;
        }
        for (int j = 0; j < height; j++) {
            map[j][0] = wallTile;
            map[j][width - 1] = wallTile;
        }

        // initialize the map to empty ground everywhere
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                map[y][x] = new MapTile(false, MapTile.MapItem.GROUND);
            }
        }
        return map;
    }

    /**
     * Get the contents of the map at a specified position
     *
     * @pre positioon != null
     * @pre map != null
     * @return contents of that cell
     */
    private MapTile getMapAt(MapTile[][] map, Position pos){
        return map[pos.getY()][pos.getX()];
    }

    /**
     * Set the contents of a position in them map
     *
     * @pre map != null
     * @pre pos != null
     * @pre item != null
     */
    private void setMapAt(MapTile[][] map, Position pos, MapTile.MapItem item){
        setMapAt(map, pos, item, -1);
    }

    private void setMapAt(MapTile[][] map, Position pos, MapTile.MapItem item, int player){
        MapTile tile = map[pos.getY()][pos.getX()];
        tile.setTile(item, player);
    }

    /**
     * Place specified number of players at random positions on an empty map
     *
     * @param map empty map to operate on
     * @param numPlayers number of players to place down
     * @pre map is an initialized, but empty map
     * @return list of positions that the players were dropped on
     */
    private List<Position> placePlayers(MapTile[][] map, int numPlayers) {
        List<Position> freePositions = new ArrayList<>();
        for (int y = 1; y < map.length - 1; y++) {
            for (int x = 1; x < map[0].length - 1; x++) {
                freePositions.add(new Position(x, y));
            }
        }
        List<Position> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            int index = generator.nextInt(freePositions.size());
            Position player = freePositions.get(index);
            freePositions.remove(index);
            players.add(player);
            map[player.getY()][player.getX()].setTile(MapTile.MapItem.PLAYER_SOUTH, 0);
        }

        return players;
    }
}
