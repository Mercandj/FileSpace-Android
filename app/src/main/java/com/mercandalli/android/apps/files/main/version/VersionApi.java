package com.mercandalli.android.apps.files.main.version;

import com.mercandalli.android.apps.files.main.Config;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * A simple retrofit interface.
 */
/* package */
interface VersionApi {

    @GET("/" + Config.routeVersionSupported)
    void getVersionSupported(Callback<VersionResponse> result);
}
