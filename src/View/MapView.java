package View;

import Model.*;
import javafx.application.Platform;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * @author Kevin Ni
 */
class MapView extends GridPane {
    final static Image BOX = new Image("images/box.png", 100, 100, false, false);
    private final static Image GROUND = new Image("images/ground.png", 100, 100, false, false);
    private final static Image GOAL = new Image("images/goal.png", 100, 100, false, false);
    private final static Image GOAL_BOX = new Image("images/goalBox.png", 100, 100, false, false);
    private final static Image GOAL_PLAYER = new Image("images/goalPlayer.png", 100, 100, false, false);
    private final static Image WALL = new Image("images/wall.png", 100, 100, false, false);
    private final static Image PLAYER_N = new Image("images/playerU.png", 100, 100, false, false);
    private final static Image PLAYER_E = new Image("images/playerR.png", 100, 100, false, false);
    private final static Image PLAYER_S = new Image("images/player.png", 100, 100, false, false);
    private final static Image PLAYER_W = new Image("images/playerL.png", 100, 100, false, false);

    private MapModel model;
    private ImageView[][] tiles;

    MapView(MapModel model){
        super();

        this.model = model;
        super.addEventHandler(KeyEvent.KEY_PRESSED, model);

        model.subscribeModelUpdate(this::onMapChange);

        tiles = new ImageView[model.getHeight()][model.getWidth()];

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

    private void onMapChange (MapUpdateInfo updateInfo) {
        for(Pair<Position, MapTile> change: updateInfo.getCoordinates()) {
            Position pos = change.first();
            int x = pos.getX();
            int y = pos.getY();
            Platform.runLater(() -> setTile(tiles[y][x], change.second()));
        }
    }

    private void setTile(ImageView viewTile, MapTile mapTile){
        switch (mapTile.getItem()) {
            case PLAYER_NORTH:
                viewTile.setImage(mapTile.getIsGoal() ? GOAL_PLAYER: PLAYER_N);
                break;
            case PLAYER_EAST:
                viewTile.setImage(mapTile.getIsGoal() ? GOAL_PLAYER: PLAYER_E);
                break;
            case PLAYER_SOUTH:
                viewTile.setImage(mapTile.getIsGoal() ? GOAL_PLAYER: PLAYER_S);
                break;
            case PLAYER_WEST:
                viewTile.setImage(mapTile.getIsGoal() ? GOAL_PLAYER: PLAYER_W);
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
