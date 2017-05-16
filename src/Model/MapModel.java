package Model;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;


/**
 * @author Kevin Ni
 */
public interface MapModel extends EventHandler<KeyEvent> {
    void handle(KeyEvent e);
    void setTime(long time);
    long getTime();
    int getScore();
    int getHeight();
    int getWidth();
    void generateNewMap();
    void reset();
    void subscribeModelUpdate(ModelEventHandler<MapUpdateInfo> listener);
}
