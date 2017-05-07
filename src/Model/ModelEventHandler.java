package Model;

/**
 * @author Kevin Ni
 */
public interface ModelEventHandler<T> {
    void Handle(T updateInfo);
}
