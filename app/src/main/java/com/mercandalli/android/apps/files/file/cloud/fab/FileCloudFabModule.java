package com.mercandalli.android.apps.files.file.cloud.fab;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link com.mercandalli.android.apps.files.main.FileAppComponent}.
 */
@Module
public class FileCloudFabModule {

    @Provides
    @Singleton
    FileCloudFabManager provideFileCloudFabManager() {
        return new FileCloudFabManagerImpl();
    }
}