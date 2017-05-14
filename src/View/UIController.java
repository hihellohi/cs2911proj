package View;

import Model.*;
import Model.Netcode.LobbyModel;
import Model.Netcode.RemoteMapModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * Created by willi on 9/05/2017.
 */
public class UIController {
    private static final String HOST = "localhost";

    public void switchToGame(ActionEvent actionEvent) {
        LocalMapModel model = new LocalMapModel("input1.txt");
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        startGame(stage, model);
    }

    public void startHost(ActionEvent actionEvent){
        LocalMapModel model = new LocalMapModel("input1.txt");

        Semaphore sem = new Semaphore(0);
        LobbyModel lobby;
        try {
            lobby = new LobbyModel();
            lobby.subscribe((s) -> {
                sem.release();
            });
            lobby.start();
            sem.acquire(2);
        }
        catch (Exception ex){
            return;
        }

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        startGame(stage, model);
        lobby.finish(model);
    }

    public void startClient(ActionEvent actionEvent){
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        RemoteMapModel client = new RemoteMapModel(HOST, 1337);
        client.start();
        startGame(stage, client);
    }

    public void switchToSettings(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("Settings.fxml"));

        Scene settingsScene = new Scene(root);
        settingsScene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

        stage.setScene(settingsScene);
        stage.show();

    }

    private void startGame(Stage stage, MapModel model){
        BorderPane root = new BorderPane();
        MapView grid = new MapView(model);
        ScoreView sv = new ScoreView(model);

        root.setLeft(grid);
        root.setRight(sv);

        Scene gameScene = new Scene(root, grid.mapWidth() + sv.sideWidth(), grid.mapHeight());
        grid.requestFocus();

        stage.setScene(gameScene);
        stage.show();
    }

    public void closeGame(ActionEvent actionEvent) {
        Platform.exit();
    }
}
