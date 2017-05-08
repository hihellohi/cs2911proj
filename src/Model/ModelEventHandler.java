package Model;

/**
 * @author Kevin Ni
 */
public interface ModelEventHandler<T> {
    void handle(T updateInfo);
}

