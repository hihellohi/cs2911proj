package View;

import Model.*;
import javafx.application.Platform;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.util.function.Consumer;

/**
 * @author Kevin Ni
 */
class MapView extends GridPane {
    private final static Image GROUND = new Image("images/ground.png", 100, 100, false, false);
    private final static Image BOX = new Image("images/box.png", 100, 100, false, false);
    private final static Image GOAL = new Image("images/goal.png", 100, 100, false, false);
    private final static Image GOAL_BOX = new Image("images/goalBox.png", 100, 100, false, false);
    private final static Image GOAL_PLAYER = new Image("images/goalPlayer.png", 100, 100, false, false);
    private final static Image WALL = new Image("images/wall.png", 100, 100, false, false);
    private final static Image PLAYER_NORTH = new Image("images/playerU.png", 100, 100, false, false);
    private final static Image PLAYER_EAST = new Image("images/playerR.png", 100, 100, false, false);
    private final static Image PLAYER_SOUTH = new Image("images/player.png", 100, 100, false, false);
    private final static Image PLAYER_WEST = new Image("images/playerL.png", 100, 100, false, false);

    private MapModel model;
    private ImageView[][] tiles;
    private Direction dirn;

    MapView(MapModel model){
        super();

        this.model = model;
        super.addEventHandler(KeyEvent.KEY_PRESSED, model);

        model.subscribeModelUpdate(onMapChange);

        tiles = new ImageView[model.getHeight()][model.getWidth()];
        dirn = Direction.SOUTH;

        MapTile mapTile = new MapTile(false, MapTile.MapItem.WALL);
        for(int r = 0; r < model.getHeight(); r++){
            for(int c = 0; c < model.getWidth(); c++){
                ImageView viewTile = new ImageView();
                setTile(viewTile, mapTile);
                tiles[r][c] = viewTile;
                super.add(viewTile, c, r);
            }
        }
    }

    int mapHeight(){
        return (int) (GROUND.getHeight() * model.getHeight());
    }

    int mapWidth(){
        return (int) (GROUND.getWidth() * model.getWidth());
    }

    private Consumer<MapUpdateInfo> onMapChange = (updateInfo) -> {
        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            dirn = dirn.changeDirection(event.getCode());
        });
        Platform.runLater(() ->{
            for(Pair<Position, MapTile> change: updateInfo.getCoordinates()) {
                Position pos = change.first();
                int x = pos.getX();
                int y = pos.getY();
                setTile(tiles[y][x], change.second());
            }
        });
    };

    private void setTile(ImageView viewTile, MapTile mapTile){
        switch (mapTile.getItem()) {
            case PLAYER:
                Image playerDirn = PLAYER_SOUTH;
                switch (dirn) {
                    case NORTH:
                        playerDirn = PLAYER_NORTH;
                        break;
                    case EAST:
                        playerDirn = PLAYER_EAST;
                        break;
                    case WEST:
                        playerDirn = PLAYER_WEST;
                        break;
                }
                viewTile.setImage(mapTile.getIsGoal() ? GOAL_PLAYER: playerDirn);
                break;
            case WALL:
                viewTile.setImage(WALL);
                break;
            case GROUND:
                viewTile.setImage(mapTile.getIsGoal() ? GOAL : GROUND);
                break;
            case BOX:
                viewTile.setImage(mapTile.getIsGoal() ? GOAL_BOX : BOX);
                break;
        }
    }
}
