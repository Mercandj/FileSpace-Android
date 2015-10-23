package mercandalli.com.filespace.utils;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import mercandalli.com.filespace.BuildConfig;
import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.config.Constants;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class RetrofitUtils {

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        return okHttpClient;
    }

    private static RestAdapter.Builder getBaseRestAdapter() {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setClient(new OkClient(getOkHttpClient()))
                .setEndpoint(Constants.URL_SERVER_API);

        if (BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        return builder;
    }

    public static RestAdapter getRestAdapter() {
        return getBaseRestAdapter().build();
    }

    public static RestAdapter getAuthorizedRestAdapter(Context context) {
        RestAdapter.Builder builder = getBaseRestAdapter()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Accept", "application/json");
                        request.addHeader("Authorization", "Basic " + Config.getUserToken());
                    }
                });

        return builder.build();
    }
}
