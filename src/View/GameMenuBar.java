package View;

import Model.MapModel;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Created by adley on 14/05/17.
 */
public class GameMenuBar extends MenuBar {
    private MapModel model;
    private Stage stage;

    public GameMenuBar(MapModel model) {
        super();
        this.model = model;
        Menu options = new Menu("Options");
        MenuItem switchToMainMenu = new MenuItem("Go back to Main Menu");
        switchToMainMenu.setOnAction(event -> {
            try {
                //TODO graceful exit notify clientconnection
                new UIController().switchHere(stage);
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        });
        MenuItem newMap = new MenuItem("Generate new map");
        newMap.setOnAction(event -> {
            model.generateNewMap();
        });
        MenuItem reset = new MenuItem("Reset map");
        reset.setOnAction(event -> {
            model.reset();
        });
        options.getItems().addAll(switchToMainMenu, newMap, reset);
        getMenus().addAll(options);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
