package mercandalli.com.filespace.common.local;

import android.content.Context;

/**
 * Created by Jonathan on 24/10/2015.
 */
public interface PersistenceApi<T> {
    T get(Context context, int id);
    void add(Context context, T entity);
    T update(Context context, T entity);
    boolean delete(Context context, T entity);
    long count(Context context);
}
