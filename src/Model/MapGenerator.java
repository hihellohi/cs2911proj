package Model;

import javafx.scene.input.KeyCode;

import java.util.*;

import static javafx.scene.input.KeyCode.*;

/**
 * Created by adley on 13/05/17.
 */
public class MapGenerator {
    private Random generator;

    public MapGenerator(long seed) {
        this.generator = new Random(seed);
    }

    public MapTile[][] generateMap(int width, int height) {
        MapTile[][] map;
        do {
            map = makeMap(width, height);
        } while (!isMapGood(map, width, height));
        return map;
    }

    private MapTile[][] makeMap(int width, int height) {
        MapTile[][] map = new MapTile[width][height];
        MapTile wallTile = new MapTile(false, MapTile.MapItem.WALL);

        for (int j = 0; j < width; j++) {
            map[0][j] = wallTile;
            map[height - 1][j] = wallTile;
        }
        for (int j = 0; j < height; j++) {
            map[j][0] = wallTile;
            map[j][width - 1] = wallTile;
        }
        List<Position> freePositions = new ArrayList<>();
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                freePositions.add(new Position(x, y));
                map[y][x] = new MapTile(false, MapTile.MapItem.GROUND);
            }
        }
        Position player = placePlayer(map, freePositions);
        Position start = new Position(player.getX(), player.getY());
        KeyCode[] directions = {UP, DOWN, LEFT, RIGHT};
        List<Integer> all = new ArrayList<>();
        Set<Integer> indexes = new HashSet<>();
        // easy (40, 10) harder (40, 30)
        for (int i = 0; i < 40; i++) {
            all.add(i);
        }
        for (int i = 0; i < 30; i++) {
            int index = generator.nextInt(all.size());
            indexes.add(all.get(index));
            all.remove(index);
        }
        List<Position> boxPositions = new ArrayList<>();
        List<Position> path = new ArrayList<>();
        path.add(start);
        for (int i = 0; i < 40; i++) {
            KeyCode move = directions[generator.nextInt(4)];
            Pair<Position, Position> positions = getPositions(move, player);
            Position newPosition = positions.first();
            Position lookAhead = positions.second();
            if (indexes.contains(i) &&
                    (getMapAt(map, newPosition).getItem() == MapTile.MapItem.GROUND) &&
                    (!newPosition.equals(start)) && (!boxPositions.contains(newPosition)) &&
                    !path.contains(newPosition)) {
                setMapAt(map, newPosition, MapTile.MapItem.BOX);
                boxPositions.add(newPosition);
            }

            if (isValidMove(map, newPosition, lookAhead)) {
                makeMove(map, player, newPosition, lookAhead);
                player = newPosition;
                path.add(newPosition);
            }
        }
        setMapAt(map, player, MapTile.MapItem.GROUND);
        setMapAt(map, start, MapTile.MapItem.PLAYER);
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
        for (Position pos: boxPositions) {
            setMapAt(map, pos, MapTile.MapItem.BOX);
        }
        return map;
    }

    private boolean isMapGood(MapTile[][] map, int width, int height) {
        boolean nonGoalBox = false;
        int numBoxes = 0;
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                Position pos = new Position(x, y);
                MapTile tile = getMapAt(map, pos);
                if (tile.getItem() == MapTile.MapItem.BOX) {
                    numBoxes++;
                    if (!tile.getIsGoal()) {
                        nonGoalBox = true;
                    }
                }
            }
        }
        if (!nonGoalBox || numBoxes < 3) {
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
        setMapAt(map, newPosition, MapTile.MapItem.PLAYER);
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
            case PLAYER:
                return false;
            case BOX:
                return getMapAt(map, lookAhead).getItem() == MapTile.MapItem.GROUND;
            default:
                return true;
        }
    }

    private MapTile getMapAt(MapTile[][] map, Position pos){
        return map[pos.getY()][pos.getX()];
    }

    private void setMapAt(MapTile[][] map, Position pos, MapTile.MapItem item){
        MapTile tile = map[pos.getY()][pos.getX()];
        tile.setItem(item);
    }

    private void placeBox(MapTile[][] map, List<Position> freePositions) {
        Position box = freePositions.get(generator.nextInt(freePositions.size()));
        freePositions.remove(box);
        Position goal = freePositions.get(generator.nextInt(freePositions.size()));
        freePositions.remove(goal);

        map[box.getY()][box.getX()].setItem(MapTile.MapItem.BOX);
        map[goal.getY()][goal.getX()].setGoal(true);
    }

    private Position placePlayer(MapTile[][] map, List<Position> freePositions) {
        Position player = freePositions.get(generator.nextInt(freePositions.size()));
        freePositions.remove(player);

        map[player.getY()][player.getX()].setItem(MapTile.MapItem.PLAYER);
        return player;
    }
}
