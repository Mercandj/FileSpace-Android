package com.mercandalli.android.apps.files.support;

import com.mercandalli.android.apps.files.main.Config;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedString;

public interface SupportOnlineApi {

    @GET("/" + Config.routeSupportComment)
    void getSupportComments(
            @Query("id_device") String deviceId,
            Callback<SupportCommentsResponse> result);

    @Multipart
    @POST("/" + Config.routeSupportComment)
    void postSupportComment(
            @Part("id_device") TypedString deviceId,
            @Part("is_dev_response") boolean isDevResponse,
            @Part("content") TypedString commentContent,
            Callback<SupportCommentsResponse> result);

}
