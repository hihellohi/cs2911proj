package View;

import Model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WarehouseBoss extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        new UIController().switchHere(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
