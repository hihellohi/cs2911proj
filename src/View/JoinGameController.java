package View;

import Model.LocalMapModel;
import Model.Netcode.BeaconFinder;
import Model.Netcode.ClientConnection;
import Model.Netcode.LobbyModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author Kevin Ni
 */
public class JoinGameController{

    @FXML private ListView<String> listView;
    @FXML private Button backBtn;
    @FXML private Button searchBtn;
    @FXML private TextField ipField;

    private Scene scene;
    private Stage stage;
    private BeaconFinder finder;

    public JoinGameController() throws IOException {

        finder = new BeaconFinder();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("JoinGame.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    }

    @FXML
    public void initialize(){

        listView.setPlaceholder(new Label("You haven't found anyone :("));
        listView.setItems(finder.getObservable());
        listView.setCellFactory((param) -> new JoinGameItem());

        searchBtn.setOnAction(searchEvent);

        backBtn.setOnAction(backEvent);
    }

    private EventHandler<ActionEvent> searchEvent = (event) -> {
        finder.target(ipField.getText());
    };

    private EventHandler<ActionEvent> backEvent = (event) -> {
        try {
            finder.close();
            new UIController().switchHere(stage);
        }
        catch (IOException ex){
            throw new UncheckedIOException(ex);
        }
    };

    public void switchHere(Stage stage){
        finder.broadcast();
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }
}

