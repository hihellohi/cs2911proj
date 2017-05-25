package Model;

/**
 * Class to represent a pair of two values.
 *
 * @author Kevin Ni
 */
public class Pair<T1, T2> {
    protected T1 first;
    protected T2 second;

    /**
     * Create the pair.
     *
     * @param first
     * @param second
     */
    public Pair(T1 first, T2 second){
        this.first = first;
        this.second = second;
    }

    /**
     * Return the first element.
     *
     * @return first
     */
    public T1 first(){
        return first;
    }

    /**
     * Return the second element.
     *
     * @return second
     */
    public T2 second(){
        return second;
    }
}
