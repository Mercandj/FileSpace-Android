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
package com.mercandalli.android.apps.files.main.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.library.base.precondition.Preconditions;

import java.util.List;

public class NetUtils {

    public static boolean isInternetConnection(final Context context) {
        Preconditions.checkNotNull(context);
        final ConnectivityManager conMgr = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static String addUrlParameters(String url, final List<StringPair> parameters) {
        if (parameters == null) {
            return url;
        }
        if (parameters.size() < 1) {
            return url;
        }
        if (parameters.size() == 1) {
            return url + (url.endsWith("?") ? "" : "?") + parameters.get(0).getName() + "=" + parameters.get(0).getValue();
        }
        url += url.endsWith("?") ? "" : "?";
        for (int i = 0; i < parameters.size() - 1; i++) {
            url += parameters.get(i).getName() + "=" + parameters.get(i).getValue() + "&";
        }
        return url + parameters.get(parameters.size() - 1);
    }

    public static void search(final Context context, final String url) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }
}
