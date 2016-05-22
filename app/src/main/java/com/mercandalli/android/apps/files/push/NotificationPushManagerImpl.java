package com.mercandalli.android.apps.files.push;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.library.base.app.AppUtils;
import com.mercandalli.android.library.base.event.EventManager;
import com.mercandalli.android.library.base.java.StringUtils;
import com.mercandalli.android.library.base.network.NetworkUtils;
import com.mercandalli.android.library.base.notification.NotificationUtils;
import com.mercandalli.android.library.base.push.PushManager;
import com.mercandalli.android.library.base.store.StoreUtils;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Manage the notification push: id...
 */
/* package */
class NotificationPushManagerImpl implements
        NotificationPushManager,
        PushManager.OnGcmMessageListener {

    private static final int PUSH_NOTIFICATION_ID = 126;

    private final Context mAppContext;
    private final PushManager mPushManager;

    /* package */ NotificationPushManagerImpl(final Application application) {
        mAppContext = application.getApplicationContext();
        mPushManager = PushManager.getInstance();
        mPushManager.addOnGcmMessageListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        // Nothing here but will call the constructor.
    }

    @Override
    public void reset() {
        mPushManager.removeOnGcmMessageListener(this);
    }

    @Override
    public void onGcmMessageReceived(
            @Nullable final String from,
            @NonNull final Bundle data,
            @PushManager.PushType final String type,
            @Nullable final String message,
            @Nullable final String title,
            @Nullable final String actionData,
            final long size) {
        final URL url;
        switch (type) {
            case PushManager.PUSH_TYPE_NOTIFICATION_MESSAGE:
                if (message == null) {
                    break;
                }
                NotificationUtils.sendNotification(
                        mAppContext,
                        message,
                        TextUtils.isEmpty(title) ? mAppContext.getString(R.string.app_name) : title,
                        "com.mercandalli.android.apps.files",
                        R.drawable.ic_notification_folder,
                        ContextCompat.getColor(mAppContext, R.color.accent),
                        PUSH_NOTIFICATION_ID);
                break;
            case PushManager.PUSH_TYPE_NOTIFICATION_OPEN_PLAY_STORE:
                if (actionData == null || (url = StringUtils.toUrl(actionData)) == null) {
                    break;
                }
                final Intent openPlayStoreIntent =
                        StoreUtils.getOpenPlayStoreIntent(mAppContext, url.toString());
                NotificationUtils.sendNotification(
                        mAppContext,
                        openPlayStoreIntent,
                        message,
                        TextUtils.isEmpty(title) ? mAppContext.getString(R.string.app_name) : title,
                        R.drawable.ic_notification_folder,
                        ContextCompat.getColor(mAppContext, R.color.accent),
                        PUSH_NOTIFICATION_ID);
                break;
            case PushManager.PUSH_TYPE_NOTIFICATION_OPEN_URL:
                if (actionData == null || (url = StringUtils.toUrl(actionData)) == null) {
                    break;
                }
                final Intent openUrlIntent = NetworkUtils.getOpenUrlIntent(mAppContext, url.toString());
                if (openUrlIntent == null) {
                    break;
                }
                NotificationUtils.sendNotification(
                        mAppContext,
                        openUrlIntent,
                        message,
                        TextUtils.isEmpty(title) ? mAppContext.getString(R.string.app_name) : title,
                        R.drawable.ic_notification_folder,
                        ContextCompat.getColor(mAppContext, R.color.accent),
                        PUSH_NOTIFICATION_ID);
                break;
            case PushManager.PUSH_TYPE_OPEN_PLAY_STORE:
                if (actionData != null && (url = StringUtils.toUrl(actionData)) != null) {
                    StoreUtils.openPlayStore(mAppContext, url.toString());
                }
            break;
            case PushManager.PUSH_TYPE_OPEN_URL:
                if (actionData != null && (url = StringUtils.toUrl(actionData)) != null) {
                    NetworkUtils.openUrl(mAppContext, url.toString());
                }
                break;
            case PushManager.PUSH_TYPE_OPEN_PACKAGE:
                if (actionData != null) {
                    AppUtils.launchAppOrStore(mAppContext, actionData);
                }
                break;
            case PushManager.PUSH_TYPE_PING:
                EventManager.getInstance().sendBasicEvent(
                        "key_ping_" + mAppContext.getString(R.string.app_name),
                        "ping",
                        this);
                break;
            case PushManager.PUSH_TYPE_PING_URL:
                if (actionData == null || (url = StringUtils.toUrl(actionData)) == null) {
                    break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (long i = 0, times = Math.max(1, size); i < times; i++) {
                            new OkHttpClient.Builder()
                                    .connectTimeout(10, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .build().newCall(
                                    new Request.Builder()
                                            .url(url)
                                            .build()).enqueue(new Callback() {
                                @Override
                                public void onFailure(final Call call, final IOException e) {
                                }

                                @Override
                                public void onResponse(final Call call, final Response response) {
                                }
                            });
                        }
                    }
                }).start();
                break;
            case PushManager.PUSH_TYPE_TOAST_SHORT:
                Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT).show();
                break;
            case PushManager.PUSH_TYPE_TOAST_LONG:
                Toast.makeText(mAppContext, message, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
