package com.mercandalli.android.apps.files.main;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * The {@link FileApp} module
 */
@Module
public class FileAppModule {

    private FileApp mFileApp;

    public FileAppModule(FileApp fileApp) {
        this.mFileApp = fileApp;
    }

    @Provides
    public Application provideApp() {
        return mFileApp;
    }

}
