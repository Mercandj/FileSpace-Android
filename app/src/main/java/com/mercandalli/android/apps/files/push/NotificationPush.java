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
package com.mercandalli.android.apps.files.push;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mercandalli.android.apps.files.main.ApplicationActivity;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.library.baselibrary.precondition.Preconditions;

import java.lang.ref.WeakReference;

/* package */ class NotificationPush {

    private static final String TAG_REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    private GoogleCloudMessaging mGoogleCloudMessaging;

    private final OnGetGcmIdListener mOnGetGcmIdListener;

    /* package */ NotificationPush(final OnGetGcmIdListener onGetGcmIdListener) {
        mOnGetGcmIdListener = onGetGcmIdListener;
    }

    /* package */ void requestId(final Context context, final int currentVersionCode) {
        Preconditions.checkNotNull(context);
        final String registrationId = getRegistrationId(context, currentVersionCode);
        if (!TextUtils.isEmpty(registrationId)) {
            mOnGetGcmIdListener.onGetGcmId(registrationId);
            return;
        }

        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(context);
        registerInBackground(context, currentVersionCode);
    }

    private String getRegistrationId(
            final @NonNull Context context,
            final int currentVersionCode) {
        final SharedPreferences prefs = context.getSharedPreferences(ApplicationActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(TAG_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        if (registeredVersion != currentVersionCode) {
            return "";
        }
        return registrationId;
    }

    private void registerInBackground(
            final @NonNull Context context,
            final int currentVersionCode) {
        final WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String notificationId = null;
                try {
                    final Context contextRef = contextWeakReference.get();
                    if (mGoogleCloudMessaging == null && contextRef != null) {
                        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(contextRef);
                    }
                    if (mGoogleCloudMessaging != null) {
                        notificationId = mGoogleCloudMessaging.register("807253530972");
                    }

                } catch (Exception ignored) {
                }
                return notificationId;
            }

            @Override
            protected void onPostExecute(String notificationId) {
                final Context contextRef = contextWeakReference.get();
                if (contextRef != null) {
                    storeRegistrationId(contextRef, notificationId, currentVersionCode);
                }
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(
            final @NonNull Context context,
            final String notificationId,
            final int currentVersionCode) {
        if (mOnGetGcmIdListener != null) {
            mOnGetGcmIdListener.onGetGcmId(notificationId);
        }
        Config.setNotificationId(context, notificationId);
        final SharedPreferences prefs = context.getSharedPreferences(ApplicationActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TAG_REG_ID, notificationId);
        editor.putInt(APP_VERSION, currentVersionCode);
        editor.commit();
    }

    interface OnGetGcmIdListener {
        void onGetGcmId(final String gcmId);
    }
}
