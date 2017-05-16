package View;

import Model.MapModel;
import Model.MapUpdateInfo;
import Model.ModelEventHandler;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author Kevin Ni
 */
public class GameView extends BorderPane implements ModelEventHandler<MapUpdateInfo> {

    private Stage stage;
    private MapView grid;
    private ScoreView sv;
    private GameMenuBar menuBar;
    private PauseMenu pauseMenu;

    public GameView(MapModel model) {
        super();

        grid = new MapView(model);
        sv = new ScoreView(model);
        menuBar = new GameMenuBar(model);
        pauseMenu = new PauseMenu(model);

        model.subscribeModelUpdate(this);

        super.setLeft(grid);
        super.setRight(sv);
        super.setTop(menuBar);
    }

    public void switchHere(Stage stage){
        this.stage = stage;
        menuBar.setStage(stage);
        pauseMenu.setStage(stage);
        Scene gameScene = new Scene(this, grid.mapWidth() + sv.sideWidth(), grid.mapHeight() + menuBar.getHeight());

        grid.requestFocus();

        stage.setScene(gameScene);
        stage.show();
    }

    public void handle(MapUpdateInfo updateInfo) {
        if (updateInfo.isPaused()) {
            pauseMenu.show();
            stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    sv.startTimer();
            });
        }
        else if (updateInfo.isFinished()) {
            new EndGameDialog(sv).showAndWait();
            pauseMenu.show();
        }
    }
}
