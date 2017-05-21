package View;

import Model.LocalMapModel;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by adley
 */
public class TutorialController {

    private static final String TUTORIAL_FILE = "src/tutorial.txt";

    @FXML private Button exitBtn;
    @FXML private Button startTutorialBtn;

    private Stage stage;
    private Scene scene;

    public TutorialController() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tutorial.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    }

    @FXML
    public void initialize(){
        exitBtn.setOnAction(this::exitTutorial);
        startTutorialBtn.setOnAction(this::startTutorial);
    }

    private void exitTutorial (ActionEvent e) {
        try {
            new UIController().switchHere(stage);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void startTutorial (ActionEvent e) {
        try {
            LocalMapModel model = new LocalMapModel(TUTORIAL_FILE);
            new GameView(model, true).switchHere(stage);
            model.broadcastMap();
        }
        catch(FileNotFoundException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    public void switchHere(Stage stage){
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }
}
