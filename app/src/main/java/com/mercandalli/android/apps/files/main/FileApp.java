package com.mercandalli.android.apps.files.main;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.mercandalli.android.apps.files.analytics.AnalyticsTrackers;

import io.fabric.sdk.android.Fabric;

/**
 * The main {@link Application}.
 */
public class FileApp extends Application {

    private static FileApp sApplication;

    private FileAppComponent mFileAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        // Fabric - Crashlytics
        Fabric.with(this, new Crashlytics());

        // Dagger - Object graph creation
        setupGraph();

        // Google Analytics
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }

    private void setupGraph() {
        mFileAppComponent = DaggerFileAppComponent.builder()
                .fileAppModule(new FileAppModule(this))
                .build();

        mFileAppComponent.inject(this);
    }

    public FileAppComponent getFileAppComponent() {
        return mFileAppComponent;
    }

    public static FileApp get() {
        return sApplication;
    }
}
