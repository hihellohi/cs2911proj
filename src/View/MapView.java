package View;

import Model.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * @author Kevin Ni
 */
class MapView extends GridPane implements ModelEventHandler<MapUpdateInfo>{
    private final static Image GROUND = new Image("images/ground.png");
    private final static Image PLAYER = new Image("images/player.png");
    private final static Image BOX = new Image("images/box.png");
    private final static Image GOAL = new Image("images/goal.png");
    private final static Image GOAL_BOX = new Image("images/goalBox.png");
    private final static Image WALL = new Image("images/wall.png");

    private MapModel model;
    private ImageView[][] tiles;

    MapView(MapModel model){
        super();

        this.model = model;
        super.addEventHandler(KeyEvent.KEY_PRESSED, model);

        model.subscribeModelUpdate(this);

        tiles = new ImageView[model.getHeight()][model.getWidth()];

        for(int r = 0; r < model.getHeight(); r++){
            for(int c = 0; c < model.getWidth(); c++){
                ImageView viewTile = new ImageView();
                MapTile mapTile = model.getMapAt(new Position(c, r));
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

    public void handle(MapUpdateInfo updateInfo){
        if(updateInfo.isFinished()){
            super.removeEventHandler(KeyEvent.KEY_PRESSED, model);
        }

        for(Pair<Position, MapTile> change: updateInfo.getCoordinates()) {
            Position pos = change.first();
            int x = pos.getX();
            int y = pos.getY();
            setTile(tiles[y][x], change.second());
        }
    }

    private void setTile(ImageView viewTile, MapTile mapTile){
        switch (mapTile.getItem()) {
            case PLAYER:
                viewTile.setImage(PLAYER);
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
