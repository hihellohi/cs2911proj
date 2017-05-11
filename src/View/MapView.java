package View;

import Model.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.security.Key;

/**
 * @author Kevin Ni
 */
public class MapView extends GridPane implements ModelEventHandler<MapUpdateInfo>{
    private final static Image ground = new Image("images/ground.png");
    private final static Image player = new Image("images/player.png");
    private final static Image box = new Image("images/box.png");
    private final static Image goal = new Image("images/goal.png");
    private final static Image goalBox = new Image("images/goalBox.png");
    private final static Image wall = new Image("images/wall.png");

    private MapModel model;
    private ImageView[][] tiles;

    public MapView(MapModel model){
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

    public int mapHeight(){
        return (int) (ground.getHeight() * model.getHeight());
    }

    public int mapWidth(){
        return (int) (ground.getWidth() * model.getWidth());
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
                viewTile.setImage(player);
                break;
            case WALL:
                viewTile.setImage(wall);
                break;
            case GROUND:
                viewTile.setImage(mapTile.getIsGoal() ? goal : ground);
                break;
            case BOX:
                viewTile.setImage(mapTile.getIsGoal() ? goalBox : box);
                break;
        }
    }
}
