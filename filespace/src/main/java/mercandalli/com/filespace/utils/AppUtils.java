/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by Jonathan on 17/05/2015.
 */
public class AppUtils {

    public static boolean launchPackage(Context context, String pckg) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager manager = context.getPackageManager();
        i = manager.getLaunchIntentForPackage(pckg);
        if (i == null)
            return false;
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);

        return true;
    }

    public static boolean checkPackage(String pakage, Activity activity) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager manager = activity.getPackageManager();
        i = manager.getLaunchIntentForPackage(pakage);
        return i != null;
    }

}
