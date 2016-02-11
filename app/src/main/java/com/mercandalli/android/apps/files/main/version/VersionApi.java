package com.mercandalli.android.apps.files.main.version;

import com.mercandalli.android.apps.files.main.Config;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * A simple retrofit interface.
 */
/* package */
interface VersionApi {

    @GET("/" + Config.routeVersionSupported)
    Call<VersionResponse> getVersionSupported();
}
