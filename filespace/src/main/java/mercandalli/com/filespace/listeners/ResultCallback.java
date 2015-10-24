package mercandalli.com.filespace.listeners;

/**
 * Created by Jonathan on 24/10/2015.
 */
public interface ResultCallback<T> {
    void success(T result);

    void failure();
}
