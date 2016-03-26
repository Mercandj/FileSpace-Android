package com.mercandalli.android.apps.files.main;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mercandalli.android.apps.files.BuildConfig;
import com.mercandalli.android.apps.files.analytics.AnalyticsTrackers;

import io.fabric.sdk.android.Fabric;

/**
 * The main {@link Application}.
 */
public class FileApp extends Application {

    private static final String TAG = "FileApp";
    private static FileApp sApplication;

    private static long sTimeLaunch;

    public static FileApp get() {
        return sApplication;
    }

    //region - Performance
    public static void logPerformance(final String tag, final String message) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.d(getPerformanceTag(tag), "Performance \t\t- " + getPerformanceTime(System.currentTimeMillis() - FileApp.sTimeLaunch) + " - " + message);
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

    protected FileAppComponent mFileAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        sTimeLaunch = System.currentTimeMillis();

        sApplication = this;

        // Fabric - Crashlytics
        Fabric.with(this, new Crashlytics());

        logPerformance(TAG, "FileApp#onCreate() - Fabric");

        // Dagger - Object graph creation
        setupGraph();

        logPerformance(TAG, "FileApp#onCreate() - Fabric Dagger");

        // Google Analytics
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        logPerformance(TAG, "FileApp#onCreate() - Fabric Dagger Analytics");
    }

    public FileAppComponent getFileAppComponent() {
        return mFileAppComponent;
    }

    protected void setupGraph() {
        mFileAppComponent = DaggerFileAppComponent.builder()
                .fileAppModule(new FileAppModule(this))
                .build();

        mFileAppComponent.inject(this);
    }
}
