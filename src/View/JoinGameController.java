package View;

import Model.Netcode.BeaconFinder;
import Model.Netcode.RemoteMapModel;
import javafx.application.Platform;
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
import java.lang.management.PlatformLoggingMXBean;
import java.net.InetAddress;
import java.rmi.Remote;
import java.util.function.Consumer;

/**
 * @author Kevin Ni
 */
public class JoinGameController{

    @FXML private ListView<RemoteMapModel> listView;
    @FXML private Button backBtn;
    @FXML private Button searchBtn;
    @FXML private Button refreshBtn;
    @FXML private TextField ipField;

    private Scene scene;
    private Stage stage;
    private BeaconFinder finder;

    public JoinGameController() throws IOException {

        finder = new BeaconFinder(startEvent);
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
        refreshBtn.setOnAction(refreshEvent);
    }

    private EventHandler<ActionEvent> refreshEvent = (event) -> {
        finder.broadcast();
    };

    private Consumer<RemoteMapModel> startEvent = (model) -> {
        finder.close();
        Platform.runLater(() -> {
            new GameView(model).switchHere(stage);
        });
    };

    private EventHandler<ActionEvent> searchEvent = (event) -> {
        finder.target(ipField.getText());
    };

    private EventHandler<ActionEvent> backEvent = (event) -> {
        try {
            finder.abort();
            new UIController().switchHere(stage);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    };

    public void switchHere(Stage stage){
        finder.broadcast();
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }
}

