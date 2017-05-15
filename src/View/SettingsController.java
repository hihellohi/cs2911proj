package View;

import Model.Settings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Created by Vivian on 13/5/17.
 */
public class SettingsController {
    @FXML private ChoiceBox<String> choiceBox;
    @FXML private Button exitSettingsBtn;

    private static String string;
    private Stage stage;
    private Scene scene;

    public SettingsController() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
        choiceBox.setValue(Settings.getInstance().getDifficultyString());
    }

    @FXML
    public void initialize(){
        choiceBox.setOnAction(chooseDifficulty);
        exitSettingsBtn.setOnAction(exitSettings);
    }


    private EventHandler<ActionEvent> chooseDifficulty = (e) -> {
        string = choiceBox.getSelectionModel().getSelectedItem();
        if (string == null) {
            string = Settings.getInstance().getDifficultyString();
        }
        choiceBox.setValue(string);
        Settings.Difficulty difficulty;
        switch (string) {
            case "Easy":
                difficulty = Settings.Difficulty.EASY;
                break;
            case "Medium":
            default:
                difficulty = Settings.Difficulty.MEDIUM;
                break;
            case "Hard":
                difficulty = Settings.Difficulty.HARD;
                break;
        }
        Settings.getInstance().setDifficulty(difficulty);
    };

    private EventHandler<ActionEvent> exitSettings = (e) -> {
        try {
            new UIController().switchHere(stage);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    };

    public void switchHere(Stage stage){
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }

    public static String getDifficulty() {
        return string;
    }
}
