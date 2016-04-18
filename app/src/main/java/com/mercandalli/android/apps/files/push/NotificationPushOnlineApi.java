package com.mercandalli.android.apps.files.push;

import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.library.baselibrary.device.Device;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * A retrofit online api.
 */
/* package */
interface NotificationPushOnlineApi {

    @POST("/" + Config.ROUTE_DEVICE_ADD)
    Call<NotificationPushResponse> addOrUpdate(final @Body Device platform);
}
