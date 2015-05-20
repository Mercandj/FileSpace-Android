package mercandalli.com.jarvis.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Jonathan on 21/05/2015.
 */
public class NetUtils {

    public static final boolean isInternetConnection(Context context) {
        final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null)
            if (activeNetwork.isConnected())
                return true;
        return false;
    }

}
