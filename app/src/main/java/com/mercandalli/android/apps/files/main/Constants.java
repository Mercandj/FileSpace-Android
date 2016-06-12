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
package com.mercandalli.android.apps.files.main;

import android.support.annotation.NonNull;

import com.mercandalli.android.apps.files.BuildConfig;

public class Constants {

    @NonNull
    public static final String GCM_SENDER = "807253530972";

    @NonNull
    public static final String URL_DOMAIN = "http://mercandalli.com/";
    @NonNull
    public static final String URL_DOMAIN_API = "http://mercandalli.com/FileSpace-API/";
    @NonNull
    public static final String URL_FOLDER_API = "FileSpace-API/";

    public static final int TAB_VIEW_TYPE_NORMAL = 0;
    public static final int TAB_VIEW_TYPE_SECTION = 1;

    public static final long SIZE_MAX_ONLINE_PICTURE_ICON = 100_000;
    public static final int WIDTH_MAX_ONLINE_PICTURE_BITMAP = 500;

    public static final boolean ADS_VISIBLE = true;

    @NonNull
    public static final String AD_MOB_KEY_NAV_DRAWER = BuildConfig.DEBUG ?
            "ca-app-pub-3940256099942544/1033173712" : "ca-app-pub-4616471093567176/1180013643";

    @NonNull
    public static final String AD_MOB_KEY_SETTINGS = BuildConfig.DEBUG ?
            "ca-app-pub-3940256099942544/1033173712" : "ca-app-pub-4616471093567176/3476162047";
}
