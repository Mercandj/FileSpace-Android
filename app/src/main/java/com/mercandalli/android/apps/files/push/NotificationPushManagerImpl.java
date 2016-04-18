package com.mercandalli.android.apps.files.push;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.mercandalli.android.library.baselibrary.device.Device;
import com.mercandalli.android.library.baselibrary.device.DeviceUtils;
import com.mercandalli.android.library.baselibrary.network.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manage the notification push: id...
 */
/* package */
class NotificationPushManagerImpl implements
        NotificationPushManager,
        NotificationPush.OnGetGcmIdListener {

    private final Context mAppContext;
    private final NotificationPushOnlineApi mNotificationPushOnlineApi;
    private final NotificationPush mNotificationPush;

    private int mVersionCode;
    private String mVersionName;

    /* package */ NotificationPushManagerImpl(
            final Application application,
            final NotificationPushOnlineApi notificationPushOnlineApi) {
        mAppContext = application.getApplicationContext();
        mNotificationPushOnlineApi = notificationPushOnlineApi;
        mNotificationPush = new NotificationPush(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendGcmId() {
        if (!NetworkUtils.isNetworkAvailable(mAppContext)) {
            return;
        }
        try {
            final PackageInfo packageInfo = mAppContext.getPackageManager()
                    .getPackageInfo(mAppContext.getPackageName(), 0);
            mVersionCode = packageInfo.versionCode;
            mVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }
        mNotificationPush.requestId(mAppContext, mVersionCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGetGcmId(final String gcmId) {
        if (TextUtils.isEmpty(gcmId)) {
            // Nothing here.
            return;
        }
        final Device.DeviceBuilder deviceBuilder = DeviceUtils.getDeviceBuilder(mAppContext);
        deviceBuilder.androidAppGcmId(gcmId);
        deviceBuilder.androidAppVersionName(mVersionName);
        deviceBuilder.androidAppVersionCode(String.valueOf(mVersionCode));
        final Call<NotificationPushResponse> notificationPushResponseCall =
                mNotificationPushOnlineApi.addOrUpdate(deviceBuilder.build());
        notificationPushResponseCall.enqueue(new Callback<NotificationPushResponse>() {
            @Override
            public void onResponse(
                    final Call<NotificationPushResponse> call,
                    final Response<NotificationPushResponse> response) {
                if (response.isSuccessful()) {
                    final NotificationPushResponse body = response.body();
                    if (body != null) {
                        body.parseResult(mAppContext);
                    }
                    // Nothing here.
                }
                // Nothing here.
            }

            @Override
            public void onFailure(
                    final Call<NotificationPushResponse> call,
                    final Throwable t) {
                // Nothing here.
            }
        });
    }
}
