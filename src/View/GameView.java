package View;

import Model.MapModel;
import Model.MapUpdateInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;


/**
 * This class creates a pane which contains all the elements of the game.
 *
 * @author Kevin Ni
 */
public class GameView extends StackPane {

    private Stage stage;
    private MapView grid;
    private ScoreView sv;
    private GameMenuBar menuBar;
    private PauseMenu pauseMenu;
    private MapModel model;
    private Region region;
    private boolean tutorial;

    /**
     * Create a new game view.
     *
     * @param model model of the map
     * @param tutorial indicate if in tutorial mode
     * @pre model != null
     */
    public GameView(MapModel model, boolean tutorial) {
        super();
        this.tutorial = tutorial;
        BorderPane bp = new BorderPane();

        grid = new MapView(model);
        sv = new ScoreView();
        menuBar = new GameMenuBar(model, this::onMapEnd, tutorial);
        this.model = model;

        region = new Region();
        region.setPrefHeight(20);
        region.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7)");
        region.setVisible(false);

        model.subscribeModelUpdate(this::onMapChange);

        bp.setLeft(grid);
        bp.setRight(sv);
        bp.setTop(menuBar);
        super.getChildren().addAll(bp, region);
    }

    /**
     * Create a new game view that is not the tutorial.
     *
     * @param model model of the map
     * @pre model != null
     */
    public GameView(MapModel model) {
        this(model, false);
    }

    /**
     * Switch the current stage to the game view.
     *
     * @param stage current stage
     * @pre stage != null
     */
    public void switchHere(Stage stage){
        this.stage = stage;
        menuBar.setStage(stage);

        pauseMenu = new PauseMenu(model, this::onMapEnd, tutorial);
        pauseMenu.setStage(stage);
        region.visibleProperty().bind(pauseMenu.showingProperty());

        Scene gameScene = new Scene(this, grid.mapWidth() + sv.sideWidth(), grid.mapHeight() + menuBar.getHeight() + region.getPrefHeight());

        grid.setOnKeyPressed(this::handleKey);
        grid.requestFocus();

        stage.setOnCloseRequest((e) -> model.close());

        stage.setScene(gameScene);
        stage.show();
    }

    /**
     * Switch stages gracefully.
     *
     * @param e button click/press event
     */
    private void onMapEnd (ActionEvent e){
        try {
            model.close();
            stage.setOnCloseRequest(null);
            new UIController().switchHere(stage);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Show the pause menu.
     *
     * @param e key press event
     */
    private void handleKey (KeyEvent e) {
        KeyCode code = e.getCode();
        if(code == KeyCode.P || code == KeyCode.ESCAPE){
            pauseMenu.show();
        }
    }

    /**
     *
     * Update the game view from update info.
     *
     * @param updateInfo info on changes made to the model
     * @pre updateInfo != null
     */
    private void onMapChange (MapUpdateInfo updateInfo) {
        if (updateInfo != null){
            if(updateInfo.isFinished()) {
                Platform.runLater(() -> {
                    new EndGameDialog(sv).showAndWait();
                    pauseMenu.show();
                });
            }
            sv.onMapChange(updateInfo);
        }
    }
}
