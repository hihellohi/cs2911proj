package View;

import Model.Netcode.BeaconFinder;
import Model.Netcode.HostConnection;
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
 * Controller for the join game screen
 *
 * @author Kevin Ni
 */
public class JoinGameController{

    @FXML private ListView<HostConnection> listView;
    @FXML private Button backBtn;
    @FXML private Button searchBtn;
    @FXML private Button refreshBtn;
    @FXML private TextField ipField;

    private Scene scene;
    private Stage stage;
    private BeaconFinder finder;

    /**
     * Class constructor
     *
     * @throws IOException when unable to load .fxml
     */
    public JoinGameController() throws IOException {

        finder = new BeaconFinder(this::startEvent);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("JoinGame.fxml"));
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

        listView.setPlaceholder(new Label("You haven't found anyone :("));
        listView.setItems(finder.getObservable());
        listView.setCellFactory((param) -> new JoinGameItem(finder::removeConnection));

        searchBtn.setOnAction(this::searchEvent);
        backBtn.setOnAction(this::backEvent);
        refreshBtn.setOnAction(this::refreshEvent);
    }

    /**
     * event handler for refresh button. Pings the broadcast address.
     *
     * @param event generated event
     */
    private void refreshEvent (ActionEvent event) {
        finder.broadcast();
    }

    /**
     * Event handler for a HostConnection receiving a signal to start a game. Switches to the game view.
     *
     * @param model the connection that received the signal to start
     */
    private synchronized void startEvent (HostConnection model) {
        if(finder.isLive()) {
            finder.finish(model);

            model.setConnectionInterruptedListener(() -> {
                //when connection to host is interrupted, switch back to main menu
                try {
                    UIController controller = new UIController();
                    Platform.runLater(() -> {
                        controller.switchHere(stage);
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                //show messagebox to user to inform them of connection loss
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

    /**
     * pings the address in the ip textfield
     *
     * @param event the generated event
     */
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

    /**
     * goes back to the main menu
     *
     * @param event the generated event
     */
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

    /**
     * switches the stage to contain the scene controlled by this object
     *
     * @param stage the stage to be switched here.
     */
    void switchHere(Stage stage){
        this.stage = stage;
        stage.setOnCloseRequest((e) -> finder.abort());
        stage.setScene(scene);
        stage.show();
        finder.broadcast();
    }
}

