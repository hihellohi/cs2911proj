package Model.Netcode;

import Model.MapTile;
import javafx.scene.input.KeyCode;

/**
 * Common constants used by classes in Model.Netcode
 *
 * @author Kevin Ni
 */
class Constants {

    static final KeyCode[] CODES = KeyCode.values();
    static final MapTile.MapItem[] TILES = MapTile.MapItem.values();
    static final String BEACON_MESSAGE = "Kevin is awesome";
}
