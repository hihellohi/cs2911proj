package Model.Netcode;

import Model.MapTile;
import javafx.scene.input.KeyCode;

/**
 * @author Kevin Ni
 */
class Constants {

    static final ProtocolHeader[] HEADERS = ProtocolHeader.values();
    static final KeyCode[] CODES = KeyCode.values();
    static final MapTile.MapItem[] TILES = MapTile.MapItem.values();
    static final int PORT = 1337;
    static final int PULSE = 5000;
}
