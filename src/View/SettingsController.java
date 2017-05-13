package View;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Created by Vivian on 13/5/17.
 */
public class SettingsController {
    @FXML
    private ChoiceBox<String> choiceBox;
    private static String string;

    public void chooseDifficulty(ActionEvent actionEvent) {
        string = choiceBox.getSelectionModel().getSelectedItem();
        choiceBox.setValue(string);

    }

    public void exitSettings(ActionEvent actionEvent) throws  IOException {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));

        Scene settingsScene = new Scene(root);
        settingsScene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

        stage.setTitle("Warehouse Boss");

        stage.setScene(settingsScene);
        stage.show();
    }

    public static String getDifficulty() {
        return string;
    }

}
