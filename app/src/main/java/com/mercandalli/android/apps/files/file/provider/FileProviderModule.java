package com.mercandalli.android.apps.files.file.provider;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileProviderModule {

    @Provides
    @Singleton
    FileProviderManagerImpl provideFileProviderManager(Application application) {
        return new FileProviderManagerImpl(application);
    }

}
