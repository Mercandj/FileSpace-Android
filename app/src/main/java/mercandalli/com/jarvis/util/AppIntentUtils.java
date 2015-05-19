package mercandalli.com.jarvis.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by Jonathan on 17/05/2015.
 */
public class AppIntentUtils {

    public static boolean launchPackage(Context context, String pckg) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager manager = context.getPackageManager();
        i = manager.getLaunchIntentForPackage(pckg);
        if(i==null)
            return false;
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);

        return true;
    }

    public static boolean checkPackage(String pakage, Activity activity) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager manager = activity.getPackageManager();
        i = manager.getLaunchIntentForPackage(pakage);
        return i!=null;
    }

}
