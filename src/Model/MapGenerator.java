package Model;

import javafx.scene.input.KeyCode;

import java.util.*;

import static javafx.scene.input.KeyCode.*;

/**
 * Created by adley on 13/05/17.
 */
public class MapGenerator {
    private Random generator;
    private Settings.Difficulty difficulty;

    public MapGenerator(long seed, Settings.Difficulty difficulty) {
        this.generator = new Random(seed);
        this.difficulty = difficulty;
    }

    public MapTile[][] generateMap(int numPlayers) {
        MapTile[][] map;
        do {
            map = makeMap(numPlayers);
        } while (!isMapGood(map));
        return map;
    }

    private MapTile[][] makeMap(int numPlayers) {
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

        List<Position> players = placePlayers(map, numPlayers);
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
            int playerIndex = generator.nextInt(numPlayers);
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

        for (Position start: startingPositions) {
            setMapAt(map, start, MapTile.MapItem.PLAYER_SOUTH);
        }

        // reset the position of boxes at where they were initially placed
        for (Position pos: boxPositions) {
            setMapAt(map, pos, MapTile.MapItem.BOX);
        }
        return map;
    }

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
    private void makeMove(
            MapTile[][] map, Position oldPosition, Position newPosition, Position lookAhead
    ) {
        boolean pushedBox = getMapAt(map, newPosition).getItem() == MapTile.MapItem.BOX;

        setMapAt(map, oldPosition, MapTile.MapItem.GROUND);
        if(pushedBox){
            setMapAt(map, lookAhead, MapTile.MapItem.BOX);
        }
        setMapAt(map, newPosition, MapTile.MapItem.PLAYER_SOUTH);
    }

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

    private MapTile getMapAt(MapTile[][] map, Position pos){
        return map[pos.getY()][pos.getX()];
    }

    private void setMapAt(MapTile[][] map, Position pos, MapTile.MapItem item){
        MapTile tile = map[pos.getY()][pos.getX()];
        tile.setItem(item);
    }

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
            map[player.getY()][player.getX()].setItem(MapTile.MapItem.PLAYER_SOUTH);
        }

        return players;
    }
}
