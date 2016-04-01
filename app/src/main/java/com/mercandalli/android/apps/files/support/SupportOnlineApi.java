package com.mercandalli.android.apps.files.support;

import com.mercandalli.android.apps.files.main.Config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface SupportOnlineApi {

    @GET("/" + Config.ROUTE_SUPPORT_COMMENT)
    Call<SupportCommentsResponse> getSupportComments(
            @Query("id_device") String deviceId);

    @FormUrlEncoded
    @POST("/" + Config.ROUTE_SUPPORT_COMMENT)
    Call<SupportCommentsResponse> postSupportComment(
            @Field("id_device") String deviceId,
            @Field("is_dev_response") boolean isDevResponse,
            @Field("content") String commentContent,

            @Field("android_app_version_code") String androidAppVersionCode,
            @Field("android_app_version_name") String androidAppVersionName,
            @Field("android_app_notification_id") String androidAppNotificationId,

            @Field("android_device_version_sdk") String androidDeviceVersionSdk,
            @Field("android_device_model") String androidDeviceModel,
            @Field("android_device_manufacturer") String androidDeviceManufacturer,
            @Field("android_device_display_language") String androidDeviceDisplayLanguage,
            @Field("android_device_country") String androidDeviceCountry
    );

    @FormUrlEncoded
    @POST("/" + Config.ROUTE_SUPPORT_COMMENT_DELETE)
    Call<SupportCommentsResponse> deleteSupportComment(
            @Field("id") String id,
            @Field("id_device") String deviceId);

    @GET("/" + Config.ROUTE_SUPPORT_COMMENT_DEVICE_ID)
    Call<SupportCommentsResponse> getAllDeviceIdSupportComment();
}
