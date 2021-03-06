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
 * Class to control the main menu.
 *
 */
public class UIController {

    @FXML private Button playGameBtn;
    @FXML private Button hostBtn;
    @FXML private Button clientBtn;
    @FXML private Button tutorialBtn;
    @FXML private Button settingsBtn;
    @FXML private Button closeGameBtn;
    @FXML private Button localBtn;

    private Scene scene;
    private static final String HOST = "localhost";
    private Stage stage;

    /**
     * Load UIController as the controller for the menu.
     *
     * @throws IOException
     */
    public UIController() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    }

    /**
     * Bind the buttons to perform their functionality.
     *
     */
    @FXML
    public void initialize(){
        playGameBtn.setOnAction(this::switchToGame);
        hostBtn.setOnAction(this::startHost);
        clientBtn.setOnAction(this::startClient);
        tutorialBtn.setOnAction(this::startTutorial);
        settingsBtn.setOnAction(this::switchToSettings);
        closeGameBtn.setOnAction(this::closeGame);
        localBtn.setOnAction(this::switchToMultiplayer);
    }

    /**
     * Switch the current stage to the main menu.
     *
     * @param stage current stage
     * @pre stage != null
     */
    public void switchHere(Stage stage){
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Switch from the main menu to a single playergame.
     *
     * @param e button click event
     */
    private void switchToGame (ActionEvent e) {
        LocalMapModel model = new LocalMapModel(1, false);
        new GameView(model).switchHere(stage);
        model.broadcastMap();
    }

    /**
     * Switch from the main menu to a local multiplayer game.
     *
     * @param e button click event
     */
    private void switchToMultiplayer(ActionEvent e) {
        LocalMapModel model = new LocalMapModel(2, true);
        new GameView(model).switchHere(stage);
        model.broadcastMap();
    }

    /**
     * Switch to lobby screen as a host.
     *
     * @param e button click event
     */
    private void startHost (ActionEvent e) {
        try {
            new LobbyController().switchHere(stage);
        }
        catch (BindException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Port already in use! Kill the process occupying it or change to another port");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Switch to lobby screen as a client.
     *
     * @param e button click event
     */
    private void startClient (ActionEvent e) {
        try {
            new JoinGameController().switchHere(stage);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Switch from main menu to the tutorial.
     *
     * @param e button click event
     */
    private void startTutorial (ActionEvent e) {
        try {
            new TutorialController().switchHere(stage);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Switch from main menu to the settings.
     *
     * @param e button click event
     */
    private void switchToSettings  (ActionEvent e) {
        try {
            new SettingsController().switchHere(stage);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Exit the game entirely.
     *
     * @param e button click event
     */
    private void closeGame (ActionEvent e){
        Platform.exit();
    }
}
