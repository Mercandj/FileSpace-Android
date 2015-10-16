/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * Created by Jonathan on 21/05/2015.
 */
public class NetUtils {

    public static boolean isInternetConnection(Context context) {
        final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null)
            if (activeNetwork.isConnected())
                return true;
        return false;
    }

    public static String addUrlParameters(String url, List<StringPair> parameters) {
        if (parameters == null)
            return url;
        if (parameters.size() < 1)
            return url;
        if (parameters.size() == 1)
            return url + (url.endsWith("?") ? "" : "?") + parameters.get(0).getName() + "=" + parameters.get(0).getValue();
        url += url.endsWith("?") ? "" : "?";
        for (int i = 0; i < parameters.size() - 1; i++)
            url += parameters.get(i).getName() + "=" + parameters.get(i).getValue() + "&";
        return url + parameters.get(parameters.size() - 1);
    }

}
