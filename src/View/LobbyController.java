package View;

import Model.LocalMapModel;
import Model.MapModel;
import Model.Netcode.LobbyModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

/**
 * @author Kevin Ni
 */
public class LobbyController {

    @FXML private ListView<Socket> listView;
    @FXML private Button startBtn;
    @FXML private Button backBtn;

    private LobbyModel model;
    private Scene scene;

    private EventHandler<ActionEvent> startEvent = (event) -> {
        LocalMapModel mapModel = new LocalMapModel("input1.txt");
        GameView view = new GameView(mapModel);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        view.switchScene(stage);

        model.finish(mapModel);
    };

    private EventHandler<ActionEvent> backEvent = (event) -> {
        try {
            model.abort();

            Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Warehouse Boss");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException ex){
            throw new UncheckedIOException(ex);
        }
    };

    public LobbyController() throws IOException {
        this.model = new LobbyModel();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Lobby.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

        model.start();
    }

    public Scene getScene(){
        return scene;
    }

    @FXML
    public void initialize(){
        listView.setPlaceholder(new Label("Nobody has joined and you have no friends :("));
        listView.setItems(model.getObservable());
        listView.setCellFactory((param) -> new LobbyItem(model));

        startBtn.setOnAction(startEvent);

        backBtn.setOnAction(backEvent);
    }
}
