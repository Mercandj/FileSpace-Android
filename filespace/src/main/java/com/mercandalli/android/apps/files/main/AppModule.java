package com.mercandalli.android.apps.files.main;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * The {@link App} module
 */
@Module
public class AppModule {

    private App mApp;

    public AppModule(App app) {
        this.mApp = app;
    }

    @Provides
    public Application provideApp() {
        return mApp;
    }

}
