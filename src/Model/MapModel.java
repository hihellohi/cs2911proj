package Model;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import java.util.function.Consumer;


/**
 * @author Kevin Ni
 */
public interface MapModel extends EventHandler<KeyEvent> {
    void handle(KeyEvent e);
    int getHeight();
    int getWidth();
    int getPlayer();
    void generateNewMap();
    void undo();
    void reset();
    void subscribeModelUpdate(Consumer<MapUpdateInfo> listener);
}
