package com.mercandalli.android.apps.files.main;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class FileApp extends Application {

    private FileAppComponent mFileAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        //Dagger - Object graph creation
        setupGraph();
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

    public static FileApp get(Context context) {
        return (FileApp) context.getApplicationContext();
    }
}
