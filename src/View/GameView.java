package View;

import Model.MapModel;
import Model.MapUpdateInfo;
import Model.ModelEventHandler;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private MapModel model;
    private Region region;

    public GameView(MapModel model) {
        super();
        BorderPane bp = new BorderPane();

        grid = new MapView(model);
        sv = new ScoreView(model);
        menuBar = new GameMenuBar(model);
        this.model = model;

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

        pauseMenu = new PauseMenu(model);
        grid.setOnKeyPressed(handleKey);
        pauseMenu.setStage(stage);

        Scene gameScene = new Scene(this, grid.mapWidth() + sv.sideWidth(), grid.mapHeight() + menuBar.getHeight() + region.getPrefHeight());

        grid.requestFocus();

        stage.setScene(gameScene);
        stage.show();
    }

    private EventHandler<KeyEvent> handleKey = (e) -> {
        KeyCode code = e.getCode();
        if(code == KeyCode.P || code == KeyCode.ESCAPE){
            pauseMenu.show();
            region.visibleProperty().bind(pauseMenu.showingProperty());
        }
    };

    public void handle(MapUpdateInfo updateInfo) {
        if (updateInfo.isFinished()) {
            Platform.runLater(() -> {
                new EndGameDialog(sv).showAndWait();
                pauseMenu.show();
            });
        }
    }
}
