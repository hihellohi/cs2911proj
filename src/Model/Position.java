package Model;

import java.util.Objects;

/**
 * Created by adley on 8/05/17.
 */
public class Position extends Pair<Integer, Integer> {
    public Position(Integer x, Integer y) {
        super(x, y);
    }

    public int getX() {
        return first;
    }

    public int getY() {
        return second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

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
