package com.mercandalli.android.apps.files.main;

import android.app.Application;
import android.content.Context;

public class FileApp extends Application {

    private FileAppComponent mFileAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

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
