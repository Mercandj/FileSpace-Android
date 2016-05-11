package com.mercandalli.android.apps.files.push;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.library.base.network.NetworkUtils;
import com.mercandalli.android.library.base.notification.NotificationUtils;
import com.mercandalli.android.library.base.push.PushManager;
import com.mercandalli.android.library.base.store.StoreUtils;

/**
 * Manage the notification push: id...
 */
/* package */
class NotificationPushManagerImpl implements
        NotificationPushManager,
        PushManager.OnGcmMessageListener {


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
            @Nullable final String actionData) {
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
                        1);
                break;
            case PushManager.PUSH_TYPE_NOTIFICATION_OPEN_PLAY_STORE:
                if (actionData == null) {
                    break;
                }
                final Intent openPlayStoreIntent =
                        StoreUtils.getOpenPlayStoreIntent(mAppContext, actionData);
                NotificationUtils.sendNotification(
                        mAppContext,
                        openPlayStoreIntent,
                        message,
                        TextUtils.isEmpty(title) ? mAppContext.getString(R.string.app_name) : title,
                        R.drawable.ic_notification_folder,
                        ContextCompat.getColor(mAppContext, R.color.accent),
                        1);
                break;
            case PushManager.PUSH_TYPE_NOTIFICATION_OPEN_URL:
                if (actionData == null) {
                    break;
                }
                final Intent openUrlIntent = NetworkUtils.getOpenUrlIntent(mAppContext, actionData);
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
                        1);
                break;
            case PushManager.PUSH_TYPE_OPEN_PLAY_STORE:
                if (actionData == null) {
                    break;
                }
                StoreUtils.openPlayStore(mAppContext, actionData);
                break;
            case PushManager.PUSH_TYPE_OPEN_URL:
                if (actionData == null) {
                    break;
                }
                NetworkUtils.openUrl(mAppContext, actionData);
                break;
        }
    }
}
