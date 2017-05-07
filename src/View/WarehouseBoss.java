package View;

import Model.ModelEventHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Model.MapModel;
import sun.awt.image.ImageAccessException;

public class WarehouseBoss extends Application implements ModelEventHandler{

    private MapModel mapModel;
    private ImageView[][] tiles;
    private final static Image black = new Image("images/black_tile.png");
    private final static Image white = new Image("images/white_tile.png");
    // size is (image_size * num_tiles) + (gap_size * (num_tiles - 1))
    private final static int width = (int) (black.getWidth() * 6) + 10 * 5;
    private final static int height = (int) (black.getHeight() * 6) + 10 * 5;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mapModel = new MapModel("input1.txt");
        mapModel.setOnModelUpdate(this);

        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        tiles = new ImageView[mapModel.getHeight()][mapModel.getWidth()];

        for(int r = 0; r < mapModel.getHeight(); r++){
            for(int c = 0; c < mapModel.getWidth(); c++){
                ImageView tile = new ImageView();
                if (mapModel.getMapAt(r, c) == '.')
                    tile.setImage(black);
                else
                    tile.setImage(white);
                tiles[r][c] = tile;
                grid.add(tile, c, r);
            }
        }

        Scene gameScene = new Scene(grid, width, height);
        gameScene.setOnKeyPressed(mapModel);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    public void Handle(){
        //MODEL UPDATE HANDLER
        for(int r = 0; r < mapModel.getHeight(); r++){
            for(int c = 0; c < mapModel.getWidth(); c++){
                if (mapModel.getMapAt(r, c) == '.')
                    tiles[r][c].setImage(black);
                else
                    tiles[r][c].setImage(white);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
