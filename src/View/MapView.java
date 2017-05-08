package View;

import Model.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;

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

        model.setOnModelUpdate(this);

        tiles = new ImageView[model.getHeight()][model.getWidth()];

        for(int r = 0; r < model.getHeight(); r++){
            for(int c = 0; c < model.getWidth(); c++){
                ImageView tile = new ImageView();
                switch (model.getMapAt(new Position(c, r))) {
                    case PLAYER:
                        tile.setImage(player);
                        break;
                    case WALL:
                        tile.setImage(wall);
                        break;
                    case GROUND:
                        tile.setImage(ground);
                        break;
                    case BOX:
                        tile.setImage(box);
                        break;
                    case GOALBOX:
                        tile.setImage(goalBox);
                        break;
                    case GOAL:
                        tile.setImage(goal);
                        break;
                }
                tiles[r][c] = tile;
                super.add(tile, c, r);
            }
        }
    }

    public int mapHeight(){
        return (int) (ground.getHeight() * model.getHeight());
    }

    public int mapWidth(){
        return (int) (ground.getWidth() * model.getHeight());
    }

    public void handle(MapUpdateInfo updateInfo){
        for(Pair<Position, MapItem> change: updateInfo.getCoordinates()) {
            Position pos = change.first();
            int x = pos.getX();
            int y = pos.getY();
            switch (change.second()){
                case PLAYER:
                    tiles[y][x].setImage(player);
                    break;
                default:
                    tiles[y][x].setImage(ground);
                    break;
            }
        }
    }
}
