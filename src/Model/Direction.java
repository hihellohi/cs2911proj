package Model;

import javafx.scene.input.KeyCode;

/**
 * Created by willi on 18/05/2017.
 */
public enum Direction {
    NORTH,
    EAST ,
    SOUTH,
    WEST;

    public Direction changeDirection(KeyCode k) {
        switch (k) {
            case UP:
                return values()[(ordinal() + (values().length - ordinal())) % values().length];
            case DOWN:
                return values()[(ordinal() + ((values().length - SOUTH.ordinal()) - ordinal())) % values().length];
            case LEFT:
                return values()[(ordinal() + ((values().length - EAST.ordinal()) - ordinal())) % values().length];
            case RIGHT:
                return values()[(ordinal() + ((values().length - WEST.ordinal()) - ordinal())) % values().length];
        }
        return SOUTH;
    }
}
