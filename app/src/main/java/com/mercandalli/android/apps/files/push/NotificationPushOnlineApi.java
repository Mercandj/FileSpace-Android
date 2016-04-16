package com.mercandalli.android.apps.files.push;

import com.mercandalli.android.apps.files.main.Config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * A retrofit online api.
 */
/* package */
interface NotificationPushOnlineApi {

    @FormUrlEncoded
    @POST("/" + Config.ROUTE_PUSH_DEVICE_ADD)
    Call<NotificationPushResponse> add(
            final @Field("id_gcm") String gcmId,
            final @Field("android_app_version_code") String androidAppVersionCode,
            final @Field("android_app_version_name") String androidAppVersionName);
}
