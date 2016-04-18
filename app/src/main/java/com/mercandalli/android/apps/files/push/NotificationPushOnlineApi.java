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
    @POST("/" + Config.ROUTE_DEVICE_ADD)
    Call<NotificationPushResponse> addOrUpdate(
            final @Field("operating_system") String platform,
            final @Field("android_app_gcm_id") String androidAppGcmId,
            final @Field("android_app_version_code") String androidAppVersionCode,
            final @Field("android_app_version_name") String androidAppVersionName,
            final @Field("android_device_language") String androidDeviceLanguage,
            final @Field("android_device_display_language") String androidDeviceDisplayLanguage,
            final @Field("android_device_country") String androidDeviceCountry,
            final @Field("android_device_version_sdk") String androidDeviceVersionSdk,
            final @Field("android_device_rooted") String androidDeviceRooted);
}
