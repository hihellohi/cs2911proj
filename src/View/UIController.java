package View;

import Model.*;
import Model.Netcode.RemoteMapModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;

/**
 * Created by willi on 9/05/2017.
 */
public class UIController {

    @FXML private Button playGameBtn;
    @FXML private Button hostBtn;
    @FXML private Button clientBtn;
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
        playGameBtn.setOnAction(switchToGame);
        hostBtn.setOnAction(startHost);
        clientBtn.setOnAction(startClient);
        settingsBtn.setOnAction(switchToSettings);
        closeGameBtn.setOnAction(closeGame);
    }

    public void switchHere(Stage stage){
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }

    private EventHandler<ActionEvent> switchToGame = (e) -> {
        //TODO THROW EXCEPTION
        LocalMapModel model = new LocalMapModel();
        GameView view = new GameView(model);
        view.switchHere(stage);
    };

    private EventHandler<ActionEvent> startHost = (e) -> {
        try {
            new LobbyController().switchHere(stage);
        }
        catch (IOException ex){
            //TODO Socket used
            ex.printStackTrace();
            System.out.println("port already occupied");
        }
    };

    private EventHandler<ActionEvent> startClient = (e) -> {
        try {
            RemoteMapModel client = new RemoteMapModel(HOST);
            client.start();
            GameView view = new GameView(client);
            view.switchHere(stage);
        }
        catch (EOFException ex){
            System.out.println("you got kicked lol");
        }
        catch(ConnectException ex){
            System.out.println("host not found");
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    };

    private EventHandler<ActionEvent> switchToSettings = (e) -> {
        try {
            new SettingsController().switchHere(stage);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    };

    private EventHandler<ActionEvent> closeGame = (e) -> {
        Platform.exit();
    };
}
