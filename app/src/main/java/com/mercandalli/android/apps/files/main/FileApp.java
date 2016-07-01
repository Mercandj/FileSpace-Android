package com.mercandalli.android.apps.files.main;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mercandalli.android.apps.files.BuildConfig;
import com.mercandalli.android.apps.files.analytics.AnalyticsTrackers;
import com.mercandalli.android.library.base.main.BaseManager;

import java.lang.ref.WeakReference;

import io.fabric.sdk.android.Fabric;

/**
 * The main {@link Application}.
 */
public class FileApp extends MultiDexApplication {

    @NonNull
    private static final String TAG = "FileApp";

    @Nullable
    private static FileApp sApplication;

    private static long sTimeLaunch;

    public static FileApp get() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sTimeLaunch = System.currentTimeMillis();

        sApplication = this;

        logPerformance(TAG, "FileApp#onCreate()");

        BaseManager.getInstance().initialize(
                this,
                Constants.GCM_SENDER);

        logPerformance(TAG, "FileApp#onCreate() - Fabric Dagger");

        final WeakReference<Context> contextWeakReference = new WeakReference<>(
                getApplicationContext());
        (new Thread() {
            public void run() {
                final Context context = contextWeakReference.get();
                if (context == null) {
                    return;
                }
                // Fabric - Crashlytics.
                Fabric.with(context, new Crashlytics());

                // Google Analytics.
                AnalyticsTrackers.initialize(context);
                AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            }
        }).start();

        logPerformance(TAG, "FileApp#onCreate() - Fabric Fabric Analytics");
    }

    //region - Performance
    public static void logPerformance(final String tag, final String message) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.d(getPerformanceTag(tag), "Performance \t\t- " + getPerformanceTime(
                System.currentTimeMillis() - FileApp.sTimeLaunch) + " - " + message);
    }

    private static String getPerformanceTime(final long timeDiff) {
        String result = String.valueOf(timeDiff);
        int missingZero = 5 - result.length();
        for (int i = 0; i < missingZero; i++) {
            result = "0" + result;
        }
        return result;
    }

    private static String getPerformanceTag(final String tag) {
        String result = tag;
        int missingSpace = 21 - result.length();
        for (int i = 0; i < missingSpace; i++) {
            result = " " + result;
        }
        return result;
    }
    //endregion - Performance
}
