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
 * Class to setup the menu bar.
 *
 */
public class GameMenuBar extends MenuBar {
    private MapModel model;
    private Stage stage;

    /**
     * Setup the menu bar for a game, certain functions are disabled when the model is a client
     * in multiplayer or if in tutorial mode.
     *
     * @param model model of the map
     * @param leaveHandler method reference which exits the stage gracefully
     * @param tutorial indicate if in tutorial mode
     * @pre model != null, leaveHandler != null
     */
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

    /**
     * Set the game menubar's stage to be the same as gameview's.
     *
     * @param stage gameview's stage
     * @pre stage != null
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
