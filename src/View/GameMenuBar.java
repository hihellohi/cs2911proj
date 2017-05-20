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

    public GameMenuBar(MapModel model, boolean tutorial) {
        super();
        this.model = model;
        Menu options = new Menu("Options");
        MenuItem switchToMainMenu = new MenuItem("Main menu");
        switchToMainMenu.setOnAction(event -> {
            try {
                //TODO graceful exit notify clientconnection
                new UIController().switchHere(stage);
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        });
        MenuItem newMap = new MenuItem("New game - n");
        if (!tutorial) {
            newMap.setOnAction(event -> {
                model.generateNewMap();
            });
        }
        MenuItem undo = new MenuItem("Undo move - u");
        undo.setOnAction(event -> {
            model.undo();
        });
        MenuItem reset = new MenuItem("Reset game - r");
        reset.setOnAction(event -> {
            model.reset();
        });
        if (!tutorial) {
            options.getItems().addAll(switchToMainMenu, newMap, undo, reset);
        }
        else {
            options.getItems().addAll(switchToMainMenu, undo, reset);
        }
        getMenus().addAll(options);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
