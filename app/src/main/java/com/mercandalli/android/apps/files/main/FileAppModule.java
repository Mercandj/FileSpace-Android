package com.mercandalli.android.apps.files.main;

import android.app.Application;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

/**
 * The {@link FileApp} module
 */
@Module
public class FileAppModule {

    @NonNull
    private FileApp mFileApp;

    public FileAppModule(@NonNull final FileApp fileApp) {
        this.mFileApp = fileApp;
    }

    @Provides
    public Application provideApp() {
        return mFileApp;
    }
}
