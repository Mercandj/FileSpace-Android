package com.mercandalli.android.apps.files.push;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.mercandalli.android.apps.files.BuildConfig;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.library.baselibrary.push.PushManager;

import static com.mercandalli.android.library.baselibrary.device.DeviceUtils.getDeviceBuilder;
import static com.mercandalli.android.library.baselibrary.network.NetworkUtils.isNetworkAvailable;

/**
 * Manage the notification push: id...
 */
/* package */
class NotificationPushManagerImpl implements NotificationPushManager, PushManager.OnGcmMessageListener {

    private static final String GCM_SENDER = "807253530972";

    private final Context mAppContext;
    private final PushManager mPushManager;

    /* package */ NotificationPushManagerImpl(final Application application) {
        mAppContext = application.getApplicationContext();
        mPushManager = PushManager.getInstance(mAppContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        if (!isNetworkAvailable(mAppContext)) {
            return;
        }
        mPushManager.addOnGcmMessageListener(this);
        mPushManager.initialize(
                BuildConfig.DEBUG,
                getDeviceBuilder(mAppContext),
                GCM_SENDER,
                Constants.URL_DOMAIN,
                Config.ROUTE_DEVICE_ADD);
    }

    @Override
    public void reset() {
        mPushManager.removeOnGcmMessageListener(this);
    }

    @Override
    public void onGcmMessageReceived(final String from, final Bundle data) {
        sendNotification(mAppContext, data.toString());
    }

    public static final int NOTIFICATION_ID = 1;

    private void sendNotification(final Context context, String message) {
        final PackageManager manager = context.getPackageManager();
        final Intent intent = manager.getLaunchIntentForPackage("com.mercandalli.android.apps.files");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification_cloud)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setLights(ContextCompat.getColor(context, R.color.accent), 500, 2200)
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
