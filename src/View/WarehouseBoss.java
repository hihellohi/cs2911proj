package View;

import javafx.application.Application;
import javafx.stage.Stage;

public class WarehouseBoss extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Warehouse Boss");
        new UIController().switchHere(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
