package View;

import Model.LocalMapModel;
import Model.Netcode.ClientConnection;
import Model.Netcode.LobbyModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Controller for the lobby screen
 *
 * @author Kevin Ni
 */
public class LobbyController {

    private static final int MAX_PLAYERS = 15;

    @FXML private ListView<ClientConnection> listView;
    @FXML private Button startBtn;
    @FXML private Button backBtn;

    private LobbyModel model;
    private Scene scene;
    private Stage stage;

    /**
     * class constructor
     *
     * @throws IOException port already occupied or fxml fails to load
     */
    public LobbyController() throws IOException {

        this.model = new LobbyModel();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Lobby.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    }

    /**
     * initialise fxml objects
     */
    @FXML
    public void initialize(){
        listView.setPlaceholder(new Label("Nobody has joined :("));
        listView.setItems(model.getObservable());
        listView.setCellFactory((param) -> new LobbyItem());

        startBtn.setOnAction(this::startEvent);
        backBtn.setOnAction(this::backEvent);
    }

    /**
     * EventHandler for when the start button is pressed.
     * Closes the lobby and attaches all established connections with a new LocalMapModel
     * switches to the game screen.
     *
     * @param event the generated event
     */
    private void startEvent (ActionEvent event) {
        if (model.nPlayers() > MAX_PLAYERS){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, String.format(
                    "Maximum of %d players! Kick %d player(s) and try again",
                    MAX_PLAYERS + 1,
                    model.nPlayers() - MAX_PLAYERS));
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        model.close();

        LocalMapModel mapModel = new LocalMapModel(model.nPlayers() + 1, false);

        model.startGame(mapModel);
        new GameView(mapModel).switchHere(stage);
        mapModel.broadcastMap();
    }

    /**
     * EventHandler for when the back button is pressed.
     * Kicks all players, closes the lobby and switches to the menu.
     *
     * @param event the generated event
     */
    private void backEvent (ActionEvent event) {
        try {
            model.abort();
            stage.setOnCloseRequest(null);
            new UIController().switchHere(stage);
        }
        catch (IOException ex){
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * switches the stage to contain the scene controlled by this object
     *
     * @param stage the stage to be switched here.
     */
    void switchHere(Stage stage){
        this.stage = stage;
        stage.setOnCloseRequest((e) -> model.abort());
        stage.setScene(scene);
        stage.show();
    }
}
