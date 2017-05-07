package View;

import Model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import static Model.MapItem.CHARACTER;

public class WarehouseBoss extends Application implements ModelEventHandler<MapUpdateInfo>{

    private MapModel mapModel;
    private ImageView[][] tiles;
    private final static Image black = new Image("images/black_tile.png");
    private final static Image white = new Image("images/white_tile.png");
    private final static int width = (int) (black.getWidth() * 6) + 10 * 5;
    private final static int height = (int) (black.getHeight() * 6) + 10 * 5;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mapModel = new MapModel("input1.txt");
        mapModel.setOnModelUpdate(this);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        tiles = new ImageView[mapModel.getHeight()][mapModel.getWidth()];

        for(int r = 0; r < mapModel.getHeight(); r++){
            for(int c = 0; c < mapModel.getWidth(); c++){
                ImageView tile = new ImageView();
                switch(mapModel.getMapAt(new Position(c, r))) {
                    case CHARACTER:
                        tile.setImage(white);
                        break;
                    default:
                        tile.setImage(black);
                        break;
                }
                tiles[r][c] = tile;
                grid.add(tile, c, r);
            }
        }

        Scene gameScene = new Scene(grid, width, height);
        gameScene.setOnKeyPressed(mapModel);

        primaryStage.setTitle("Warehouse Boss");
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    public void Handle(MapUpdateInfo updateInfo){
        //MODEL UPDATE HANDLER

        for(Pair<Position, MapItem> change: updateInfo.getCoordinates()) {
            Position pos = change.first();
            int x = pos.getX();
            int y = pos.getY();
            switch (change.second()){
                case CHARACTER:
                    tiles[y][x].setImage(white);
                    break;
                default:
                    tiles[y][x].setImage(black);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
