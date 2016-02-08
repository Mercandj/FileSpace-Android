package com.mercandalli.android.apps.files.support;

import com.mercandalli.android.apps.files.main.Config;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;

public interface SupportOnlineApi {

    @GET("/" + Config.routeSupportComment)
    void getSupportComments(
            Callback<SupportCommentsResponse> result);

    @Multipart
    @POST("/" + Config.routeSupportComment)
    void pousrSupportComment(
            @Path("device_id") String deviceId,
            @Part("comment_content") String commentContent,
            Callback<SupportCommentsResponse> result);

}
