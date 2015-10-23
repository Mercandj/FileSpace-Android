package mercandalli.com.filespace.manager.file;

import android.content.Context;

/**
 * Created by Jonathan on 23/10/2015.
 */
public interface DAO<E> {

    E get(Context context, int id);
}
