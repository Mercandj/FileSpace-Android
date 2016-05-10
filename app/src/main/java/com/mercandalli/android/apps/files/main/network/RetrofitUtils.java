package com.mercandalli.android.apps.files.main.network;

import com.mercandalli.android.apps.files.BuildConfig;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {

    private static OkHttpClient getOkHttpClient() {
        final OkHttpClient.Builder builder = (new OkHttpClient.Builder())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }

    private static OkHttpClient getAuthorizedOkHttpClient() {
        final OkHttpClient.Builder builder = (new OkHttpClient.Builder())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        // Customize the request
                        Request request = original.newBuilder()
                                .header("Accept", "application/json")
                                .header("Authorization", "Basic " + Config.getUserToken())
                                .method(original.method(), original.body())
                                .build();

                        // Customize or return the response
                        return chain.proceed(request);
                    }
                });
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }

    private static OkHttpClient getAuthorizedOkHttpClientUpload() {
        final OkHttpClient.Builder builder = (new OkHttpClient.Builder())
                .connectTimeout(12, TimeUnit.MINUTES)
                .readTimeout(12, TimeUnit.MINUTES)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        // Customize the request
                        Request request = original.newBuilder()
                                .header("Authorization", "Basic " + Config.getUserToken())
                                .method(original.method(), original.body())
                                .build();

                        // Customize or return the response
                        return chain.proceed(request);
                    }
                });
        return builder.build();
    }

    public static Retrofit getRetrofit() {
        return (new Retrofit.Builder())
                .baseUrl(Constants.URL_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
    }

    public static Retrofit getAuthorizedRetrofit() {
        return (new Retrofit.Builder())
                .baseUrl(Constants.URL_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getAuthorizedOkHttpClient())
                .build();
    }

    public static Retrofit getAuthorizedRetrofitUpload() {
        return (new Retrofit.Builder())
                .baseUrl(Constants.URL_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getAuthorizedOkHttpClientUpload())
                .build();
    }
}
