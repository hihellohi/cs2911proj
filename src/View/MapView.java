package View;

import Model.*;
import javafx.application.Platform;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * This class generates a view of the map using a provided model.
 *
 * @author Kevin Ni
 */
class MapView extends GridPane {
    final static Image BOX = new Image("images/box.png", 100, 100, false, false);
    private final static Image GROUND = new Image("images/ground.png", 100, 100, false, false);
    private final static Image GOAL = new Image("images/goal.png", 100, 100, false, false);
    private final static Image GOAL_BOX = new Image("images/goalBox.png", 100, 100, false, false);
    private final static Image WALL = new Image("images/wall.png", 100, 100, false, false);

    private final static Image GOAL_PLAYER_N = new Image("images/goalPlayerU.png", 100, 100, false, false);
    private final static Image GOAL_PLAYER_E = new Image("images/goalPlayerR.png", 100, 100, false, false);
    private final static Image GOAL_PLAYER_S = new Image("images/goalPlayer.png", 100, 100, false, false);
    private final static Image GOAL_PLAYER_W = new Image("images/goalPlayerL.png", 100, 100, false, false);

    private final static Image PLAYER_N = new Image("images/playerU.png", 100, 100, false, false);
    private final static Image PLAYER_E = new Image("images/playerR.png", 100, 100, false, false);
    private final static Image PLAYER_S = new Image("images/player.png", 100, 100, false, false);
    private final static Image PLAYER_W = new Image("images/playerL.png", 100, 100, false, false);

    private final static Image GOAL_PLAYER2_N = new Image("images/goalPlayer2U.png", 100, 100, false, false);
    private final static Image GOAL_PLAYER2_E = new Image("images/goalPlayer2R.png", 100, 100, false, false);
    private final static Image GOAL_PLAYER2_S = new Image("images/goalPlayer2.png", 100, 100, false, false);
    private final static Image GOAL_PLAYER2_W = new Image("images/goalPlayer2L.png", 100, 100, false, false);

    private final static Image PLAYER_N2 = new Image("images/player2U.png", 100, 100, false, false);
    private final static Image PLAYER_E2 = new Image("images/player2R.png", 100, 100, false, false);
    private final static Image PLAYER_S2 = new Image("images/player2.png", 100, 100, false, false);
    private final static Image PLAYER_W2 = new Image("images/player2L.png", 100, 100, false, false);

    private final static Image[] PLAYER1 = new Image[] {GOAL_PLAYER_N, GOAL_PLAYER_E, GOAL_PLAYER_S, GOAL_PLAYER_W, PLAYER_N, PLAYER_E, PLAYER_S, PLAYER_W};
    private final static Image[] PLAYER2 = new Image[] {GOAL_PLAYER2_N, GOAL_PLAYER2_E, GOAL_PLAYER2_S, GOAL_PLAYER2_W, PLAYER_N2, PLAYER_E2, PLAYER_S2, PLAYER_W2};

    private MapModel model;
    private ImageView[][] tiles;

    /**
     * Create a view of the map from the model.
     *
     * @param model model of the map
     * @pre model != null
     */
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

    /**
     * Getter to return the height of the map.
     *
     * @return mapHeight
     */
    int mapHeight(){
        return (int) (GROUND.getHeight() * model.getHeight());
    }

    /**
     * Getter to return the width of the map.
     *
     * @return mapWidth
     */
    int mapWidth(){
        return (int) (GROUND.getWidth() * model.getWidth());
    }

    /**
     * Update the map view from update info.
     *
     * @param updateInfo info on changes made to the model
     * @pre updateInfo != null
     */
    private void onMapChange (MapUpdateInfo updateInfo) {
        if(updateInfo == null){
            return;
        }

        for(Pair<Position, MapTile> change: updateInfo.getCoordinates()) {
            Position pos = change.first();
            int x = pos.getX();
            int y = pos.getY();
            Platform.runLater(() -> setTile(tiles[y][x], change.second()));
        }
    }

    /**
     * Set a tile in map view.
     *
     * @param viewTile tile to be set
     * @param mapTile item of tile to be set
     * @pre viewTile != null, mapTile != null
     */
    private void setTile(ImageView viewTile, MapTile mapTile){
        switch (mapTile.getItem()) {
            case WALL:
                viewTile.setImage(WALL);
                break;
            case GROUND:
                viewTile.setImage(mapTile.getIsGoal() ? GOAL : GROUND);
                break;
            case BOX:
                viewTile.setImage(mapTile.getIsGoal() ? GOAL_BOX : BOX);
                break;
            default:
                Image[] player = mapTile.getPlayer() == model.getPlayer() ? PLAYER2 : PLAYER1;
                int index = mapTile.getItem().ordinal() - MapTile.MapItem.PLAYER_NORTH.ordinal();
                viewTile.setImage(player[index + (mapTile.getIsGoal() ? 0 : 4)]);
        }
    }
}
