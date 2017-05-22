package View;

import Model.LocalMapModel;
import Model.MapModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * Created by adley on 14/05/17.
 */
public class GameMenuBar extends MenuBar {
    private MapModel model;
    private Stage stage;

    public GameMenuBar(MapModel model, EventHandler<ActionEvent> leaveHandler, boolean tutorial) {
        super();
        this.model = model;
        Menu options = new Menu("Options");
        MenuItem switchToMainMenu = new MenuItem("Main menu");
        switchToMainMenu.setOnAction(leaveHandler);

        if (model instanceof LocalMapModel){
            LocalMapModel localModel = (LocalMapModel)model;
            MenuItem newMap = new MenuItem("New game - n");
            if (!tutorial) {
                newMap.setOnAction(event -> {
                    localModel.generateNewMap();
                });
            }
            MenuItem undo = new MenuItem("Undo move - u");
            undo.setOnAction(event -> {
                localModel.undo();
            });
            MenuItem reset = new MenuItem("Reset game - r");
            reset.setOnAction(event -> {
                localModel.reset();
            });
            if (!tutorial) {
                options.getItems().addAll(switchToMainMenu, newMap, undo, reset);
            }
            else {
                options.getItems().addAll(switchToMainMenu, undo, reset);
            }
        }
        else {
            options.getItems().addAll(switchToMainMenu);
        }

        getMenus().addAll(options);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
