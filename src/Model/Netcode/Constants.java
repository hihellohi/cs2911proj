package Model.Netcode;

import Model.MapTile;
import javafx.scene.input.KeyCode;

/**
 * @author Kevin Ni
 */
class Constants {

    static final KeyCode[] CODES = KeyCode.values();
    static final MapTile.MapItem[] TILES = MapTile.MapItem.values();
    static final int TCP_PORT = 1337;
    static final int UDP_PORT = 1338;
    static final String BEACON_MESSAGE = "Kevin is awesome";
}
