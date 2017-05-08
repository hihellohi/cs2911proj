package Model;

import View.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by willi on 9/05/2017.
 */
public class MenuController {
    public void switchToGame(ActionEvent actionEvent) {
        MapView grid = new MapView();

        Scene gameScene = new Scene(grid, grid.mapWidth(), grid.mapHeight());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        grid.requestFocus();

        stage.setScene(gameScene);
        stage.show();

    }

    public void closeGame(ActionEvent actionEvent) {
        Platform.exit();
    }
}
