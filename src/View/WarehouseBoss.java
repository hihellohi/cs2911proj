package View;

import Model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WarehouseBoss extends Application implements ModelEventHandler<MapUpdateInfo>{

    private MapModel mapModel;
    private Label[][] labelgrid;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mapModel = new MapModel("input1.txt");
        mapModel.setOnModelUpdate(this);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        labelgrid = new Label[mapModel.getHeight()][mapModel.getWidth()];

        for(int r = 0; r < mapModel.getHeight(); r++){
            for(int c = 0; c < mapModel.getWidth(); c++){
                Label label;
                switch(mapModel.getMapAt(r, c)) {
                    case CHARACTER:
                        label = new Label("c");
                        break;
                    default:
                        label = new Label(".");
                        break;
                }
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

    public void Handle(MapUpdateInfo updateInfo){
        //MODEL UPDATE HANDLER

        for(Triplet<Integer, Integer, MapItem> change: updateInfo.getCoordinates()) {
            int r = change.getItem1().intValue();
            int c = change.getItem2().intValue();
            switch (change.getItem3()){
                case CHARACTER:
                    labelgrid[r][c].setText("c");
                    break;
                default:
                    labelgrid[r][c].setText(".");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
