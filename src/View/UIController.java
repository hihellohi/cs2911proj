package View;

import Model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.BindException;

/**
 * Created by willi on 9/05/2017.
 */
public class UIController {

    @FXML private Button playGameBtn;
    @FXML private Button hostBtn;
    @FXML private Button clientBtn;
    @FXML private Button tutorialBtn;
    @FXML private Button settingsBtn;
    @FXML private Button closeGameBtn;

    private Scene scene;
    private static final String HOST = "localhost";
    private Stage stage;

    public UIController() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    }

    @FXML
    public void initialize(){
        playGameBtn.setOnAction(this::switchToGame);
        hostBtn.setOnAction(this::startHost);
        clientBtn.setOnAction(this::startClient);
        tutorialBtn.setOnAction(this::startTutorial);
        settingsBtn.setOnAction(this::switchToSettings);
        closeGameBtn.setOnAction(this::closeGame);
    }

    public void switchHere(Stage stage){
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }

    private void switchToGame (ActionEvent e) {
        LocalMapModel model = new LocalMapModel(1);
        new GameView(model).switchHere(stage);
        model.broadcastMap();
    }

    private void startHost (ActionEvent e) {
        try {
            new LobbyController().switchHere(stage);
        }
        catch (BindException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Port 1337 already in use");
            alert.showAndWait();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void startClient (ActionEvent e) {
        try {
            new JoinGameController().switchHere(stage);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void startTutorial (ActionEvent e) {
        try {
            new TutorialController().switchHere(stage);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void switchToSettings  (ActionEvent e) {
        try {
            new SettingsController().switchHere(stage);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void closeGame (ActionEvent e){
        Platform.exit();
    }
}
