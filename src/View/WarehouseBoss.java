package View;

import Model.ModelEventHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Model.MapModel;

public class WarehouseBoss extends Application implements ModelEventHandler{

    private MapModel mapModel;
    private Label[][] labelgrid;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mapModel = new MapModel("input1.txt");
        mapModel.setOnModelUpdate(this);

        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        labelgrid = new Label[mapModel.getHeight()][mapModel.getWidth()];

        for(int r = 0; r < mapModel.getHeight(); r++){
            for(int c = 0; c < mapModel.getWidth(); c++){
                Label label = new Label(Character.toString(mapModel.getMapAt(r, c)));
                labelgrid[r][c] = label;
                grid.add(label, c, r);
            }
        }

        Scene gameScene = new Scene(grid, 1000, 1000);
        gameScene.setOnKeyPressed(mapModel);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    public void Handle(){
        //MODEL UPDATE HANDLER
        for(int r = 0; r < mapModel.getHeight(); r++){
            for(int c = 0; c < mapModel.getWidth(); c++){
                labelgrid[r][c].setText(Character.toString(mapModel.getMapAt(r, c)));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
