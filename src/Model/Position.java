package Model;

import java.util.Objects;

/**
 * Simple class that represents a position on the game map.
 * It implements .equals(other) and hashCode() so positions can be compared
 * and hashed.
 */
public class Position extends Pair<Integer, Integer> {
    /**
     * Construct a new Position.
     *
     * @param x the x coordinate of the position
     * @param y the y coordinate of the position
     */
    public Position(Integer x, Integer y) {
        super(x, y);
    }

    /**
     * Getter for the x coordinate of the position
     */
    public int getX() {
        return first;
    }

    /**
     * Getter for the y coordinate of the position
     */
    public int getY() {
        return second;
    }

    /**
     * Hashes based on the x and y coordinates of the position
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    /**
     * Two positions are considered equal iff their x and y coordinates are the same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        Position other = (Position) o;
        return other.getX() == (first) &&
                other.getY() == (second);
    }

}
