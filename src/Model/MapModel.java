package Model;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;


/**
 * Created by Ni on 11/05/2017.
 */
public interface MapModel extends EventHandler<KeyEvent> {
    void handle(KeyEvent e);
    MapTile getMapAt(Position pos);
    int getHeight();
    int getWidth();
    void subscribeModelUpdate(ModelEventHandler<MapUpdateInfo> listener);
}
