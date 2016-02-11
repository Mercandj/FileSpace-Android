package com.mercandalli.android.apps.files.support;

import com.mercandalli.android.apps.files.main.Config;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface SupportOnlineApi {

    @GET("/" + Config.routeSupportComment)
    Call<SupportCommentsResponse> getSupportComments(
            @Query("id_device") String deviceId);

    @FormUrlEncoded
    @POST("/" + Config.routeSupportComment)
    Call<SupportCommentsResponse> postSupportComment(
            @Field("id_device") String deviceId,
            @Field("is_dev_response") boolean isDevResponse,
            @Field("content") String commentContent);

    @FormUrlEncoded
    @POST("/" + Config.routeSupportCommentDelete)
    Call<SupportCommentsResponse> deleteSupportComment(
            @Field("id") String id,
            @Field("id_device") String deviceId);
}
