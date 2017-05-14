package View;

import Model.MapModel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author Kevin Ni
 */
public class GameView extends BorderPane {

    MapView grid;
    ScoreView sv;

    public GameView(MapModel model) {
        super();

        grid = new MapView(model);
        sv = new ScoreView(model);
        super.setLeft(grid);
        super.setRight(sv);
    }

    public void switchScene(Stage stage){
        Scene gameScene = new Scene(this, grid.mapWidth() + sv.sideWidth(), grid.mapHeight());

        grid.requestFocus();

        stage.setScene(gameScene);
        stage.show();
    }
}
