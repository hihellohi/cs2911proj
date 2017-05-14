package Model;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;


/**
 * Created by Ni on 11/05/2017.
 */
public interface MapModel extends EventHandler<KeyEvent> {
    void handle(KeyEvent e);
    MapTile getMapAt(Position pos);
    void setTime(long time);
    long getTime();
    int getScore();
    int getHeight();
    int getWidth();
    void generateNewMap();
    void reset();
    void subscribeModelUpdate(ModelEventHandler<MapUpdateInfo> listener);
}
