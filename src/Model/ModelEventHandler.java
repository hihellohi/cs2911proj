package Model;

/**
 * @author Kevin Ni
 */
public interface ModelEventHandler<T> {
    //TODO we can use consumer this class is not necessary
    void handle(T updateInfo);
}

