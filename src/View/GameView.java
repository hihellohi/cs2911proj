package View;

import Model.MapModel;
import Model.MapUpdateInfo;
import Model.ModelEventHandler;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Kevin Ni
 */
public class GameView extends StackPane implements ModelEventHandler<MapUpdateInfo> {

    private Stage stage;
    private MapView grid;
    private ScoreView sv;
    private GameMenuBar menuBar;
    private PauseMenu pauseMenu;
    private Region region;

    public GameView(MapModel model) {
        super();
        BorderPane bp = new BorderPane();

        grid = new MapView(model);
        sv = new ScoreView(model);
        menuBar = new GameMenuBar(model);
        pauseMenu = new PauseMenu(model);
        region = new Region();
        region.setPrefHeight(20);
        region.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7)");
        region.setVisible(false);

        model.subscribeModelUpdate(this);

        bp.setLeft(grid);
        bp.setRight(sv);
        bp.setTop(menuBar);
        super.getChildren().addAll(bp, region);
    }

    public void switchHere(Stage stage){
        this.stage = stage;
        menuBar.setStage(stage);
        pauseMenu.setStage(stage);
        Scene gameScene = new Scene(this, grid.mapWidth() + sv.sideWidth(), grid.mapHeight() + menuBar.getHeight() + region.getPrefHeight());

        grid.requestFocus();

        stage.setScene(gameScene);
        stage.show();
    }

    public void handle(MapUpdateInfo updateInfo) {
        if (updateInfo.isPaused()) {
            pauseMenu.show();
            region.visibleProperty().bind(pauseMenu.showingProperty());
        }
        else if (updateInfo.isFinished()) {
            new EndGameDialog(sv).showAndWait();
            pauseMenu.show();
        }
    }
}
