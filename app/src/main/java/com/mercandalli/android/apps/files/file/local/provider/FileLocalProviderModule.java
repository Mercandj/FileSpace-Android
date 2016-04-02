package com.mercandalli.android.apps.files.file.local.provider;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileLocalProviderModule {

    @Provides
    @Singleton
    FileLocalProviderManager provideFileProviderManager(Application application) {
        return new FileLocalProviderManagerImpl(application);
    }
}
