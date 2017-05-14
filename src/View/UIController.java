package View;

import Model.*;
import Model.Netcode.RemoteMapModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by willi on 9/05/2017.
 */
public class UIController {
    private static final String HOST = "localhost";
    private Stage stage;

    public void switchToGame(ActionEvent actionEvent) {
        LocalMapModel model = new LocalMapModel("input1.txt");
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        startGame(stage, model);
    }

    public void startHost(ActionEvent actionEvent){

        try {
            LobbyController controller = new LobbyController();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(controller.getScene());
            stage.show();
        }
        catch (IOException ex){
            System.out.println("port already occupied");
        }
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
        GameView view = new GameView(model);
        view.switchScene(stage);
    }

    public void closeGame(ActionEvent actionEvent) {
        Platform.exit();
    }
}
