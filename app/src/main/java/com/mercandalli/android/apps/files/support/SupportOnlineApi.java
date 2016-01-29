package com.mercandalli.android.apps.files.support;

import com.mercandalli.android.apps.files.main.Config;

import retrofit.Callback;
import retrofit.http.GET;

public interface SupportOnlineApi {

    @GET("/" + Config.routeSupportComment)
    void getSupportComments(
            Callback<SupportCommentsResponse> result);

}
