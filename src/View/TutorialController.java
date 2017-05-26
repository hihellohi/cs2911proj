package View;

import Model.LocalMapModel;
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
 * Controller for the tutorial screen.
 */
public class TutorialController {
    // Tutorial map represented in ASCII. This is loaded by LocalMapModel instead of generating a random map
    private static final String TUTORIAL_FILE = "tutorial.txt";

    @FXML private Button exitBtn;
    @FXML private Button startTutorialBtn;

    private Stage stage;
    private Scene scene;

    /**
     * Load from fxml file and create the scene
     * @throws IOException
     */
    public TutorialController() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tutorial.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    }

    /**
     * Bind the buttons to perform their functionality
     */
    @FXML
    public void initialize(){
        exitBtn.setOnAction(this::exitTutorial);
        startTutorialBtn.setOnAction(this::startTutorial);
    }

    /**
     * Called when the exit button is pressed. Initializes a new main menu and swaps the stage to it
     *
     * @param e button press event
     */
    private void exitTutorial (ActionEvent e) {
        try {
            new UIController().switchHere(stage);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Called when the start tutorial button is pressed. Initializes the game and swaps the stage to it
     *
     * @param e button press event
     */
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

    /**
     * Switch the stage from its current scene to use the tutorial scene
     * @param stage
     */
    public void switchHere(Stage stage){
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }
}
