package View;

import Model.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Class to control the settings menu.
 *
 */
public class SettingsController {
    @FXML private ChoiceBox<String> choiceBox;
    @FXML private Button exitSettingsBtn;
    @FXML private TextField tcpField;
    @FXML private TextField nameField;

    private static String string;
    private Stage stage;
    private Scene scene;

    /**
     * Load SettingsController as the controller for the settings menu.
     *
     * @throws IOException
     */
    public SettingsController() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"));
        loader.setController(this);
        Parent parent = loader.load();
        scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
    }

    /**
     * Bind the buttons to perform their functionality.
     *
     */
    @FXML
    public void initialize(){
        choiceBox.setOnAction(this::chooseDifficulty);
        exitSettingsBtn.setOnAction(this::exitSettings);
        Settings settings = Settings.getInstance();

        choiceBox.setValue(settings.getDifficultyString());
        tcpField.setText(Integer.toString(settings.getTCPPort()));
        nameField.setText(settings.getName());
    }

    /**
     * Updates the difficulty to the selected difficulty.
     *
     * @param e button click event
     */
    private void chooseDifficulty (ActionEvent e) {
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
    }

    /**
     * Exit the settings and return to the main menu.
     *
     * @param e button click event
     */
    private void exitSettings (ActionEvent e) {
        Settings settings = Settings.getInstance();
        if(!settings.setTCPPort(tcpField.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR, "please enter a valid port");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        settings.setName(nameField.getText());

        try {
            new UIController().switchHere(stage);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Switch the current stage to the settings menu.
     *
     * @param stage current stage
     * @pre stage != null
     */
    public void switchHere(Stage stage){
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Getter to return the difficulty selected.
     *
     * @return difficulty
     */
    public static String getDifficulty() {
        return string;
    }
}
