package com.mercandalli.android.apps.files.main;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        //Dagger - Object graph creation
        setupGraph();
    }

    private void setupGraph() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        mAppComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }
}
