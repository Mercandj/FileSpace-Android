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
package com.mercandalli.android.apps.files.notificationpush;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mercandalli.android.apps.files.main.ApplicationActivity;
import com.mercandalli.android.apps.files.main.Config;

public class NotificationPush {

    private static final String TAG_REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    private GoogleCloudMessaging mGoogleCloudMessaging;
    private String mNotificationId;

    public NotificationPush(final Context context) {
        if (TextUtils.isEmpty(mNotificationId)) {
            mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(context);
            mNotificationId = getRegistrationId(context);
            registerInBackground(context);
            return;
        }
        Config.setNotificationId(context, mNotificationId);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(ApplicationActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(TAG_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground(final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String notificationId = null;
                try {
                    if (mGoogleCloudMessaging == null) {
                        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                    }
                    notificationId = mGoogleCloudMessaging.register("807253530972");

                } catch (Exception ignored) {
                }
                return notificationId;
            }

            @Override
            protected void onPostExecute(String notificationId) {
                storeRegistrationId(context, notificationId);
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String notificationId) {
        Config.setNotificationId(context, notificationId);
        final SharedPreferences prefs = context.getSharedPreferences(ApplicationActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TAG_REG_ID, notificationId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }
}
