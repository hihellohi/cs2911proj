package View;

import Model.Netcode.BeaconFinder;
import Model.Netcode.RemoteMapModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.UnknownHostException;

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

        finder = new BeaconFinder(this::startEvent);
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

        searchBtn.setOnAction(this::searchEvent);
        backBtn.setOnAction(this::backEvent);
        refreshBtn.setOnAction(this::refreshEvent);
    }

    private void refreshEvent (ActionEvent event) {
        finder.broadcast();
    }

    private synchronized void startEvent (RemoteMapModel model) {
        if(finder.isLive()) {
            finder.finish(model);

            model.setConnectionInterruptedListener(() -> {
                try {
                    UIController controller = new UIController();
                    Platform.runLater(() -> {
                        controller.switchHere(stage);
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Connection to the host has been lost");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                });
            });

            GameView view = new GameView(model);
            Platform.runLater(() -> {
                view.switchHere(stage);
            });
        }
    }

    private void searchEvent (ActionEvent event) {
        try {
            finder.target(ipField.getText());
        }
        catch(UnknownHostException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, String.format("hostname %s not found", ex.getMessage()));
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    private void backEvent (ActionEvent event) {
        try {
            finder.abort();
            stage.setOnCloseRequest(null);
            new UIController().switchHere(stage);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void switchHere(Stage stage){
        this.stage = stage;
        stage.setOnCloseRequest((e) -> finder.abort());
        stage.setScene(scene);
        stage.show();
        finder.broadcast();
    }
}

