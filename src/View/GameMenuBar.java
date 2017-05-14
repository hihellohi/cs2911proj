package Model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Created by adley on 14/05/17.
 */
public class GameMenuBar extends MenuBar {
    private MapModel model;

    public GameMenuBar(MapModel model) {
        super();
        this.model = model;
        Menu options = new Menu("Options");
        MenuItem newMap = new MenuItem("Generate new map");
        newMap.setOnAction(event -> {
            model.generateNewMap();
        });
        MenuItem reset = new MenuItem("Reset map");
        reset.setOnAction(event -> {
            model.reset();
        });
        options.getItems().addAll(newMap, reset);
        getMenus().addAll(options);
    }
}
