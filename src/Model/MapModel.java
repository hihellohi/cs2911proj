package Model;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;


/**
 * @author Kevin Ni
 */
public interface MapModel extends EventHandler<KeyEvent> {
    void handle(KeyEvent e);
    int getHeight();
    int getWidth();
    void generateNewMap();
    void reset();
    void subscribeModelUpdate(ModelEventHandler<MapUpdateInfo> listener);
}
