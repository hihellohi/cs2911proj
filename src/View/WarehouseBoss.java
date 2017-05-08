package View;

import Model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WarehouseBoss extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        MapModel mapModel = new MapModel("input1.txt");

        MapView grid = new MapView(mapModel);

        Scene gameScene = new Scene(grid, grid.mapWidth(), grid.mapHeight());
        gameScene.setOnKeyPressed(mapModel);

        primaryStage.setTitle("Warehouse Boss");
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
