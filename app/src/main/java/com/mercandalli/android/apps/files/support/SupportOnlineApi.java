package com.mercandalli.android.apps.files.support;

import com.mercandalli.android.apps.files.main.Config;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

public interface SupportOnlineApi {

    @GET("/" + Config.routeSupportComment)
    void getSupportComments(
            @Query("id_device") String deviceId,
            Callback<SupportCommentsResponse> result);

    @Multipart
    @POST("/" + Config.routeSupportComment)
    void postSupportComment(
            @Path("id_device") String deviceId,
            @Part("content") String commentContent,
            Callback<SupportCommentsResponse> result);

}
