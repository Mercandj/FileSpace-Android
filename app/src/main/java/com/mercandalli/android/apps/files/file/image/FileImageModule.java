package com.mercandalli.android.apps.files.file.image;

import android.app.Application;

import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;
import com.mercandalli.android.apps.files.main.FileAppComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link FileAppComponent}.
 */
@Module
public class FileImageModule {

    @Provides
    @Singleton
    FileImageManager provideFileImageManager(
            final Application application,
            final FileLocalProviderManager fileLocalProviderManager) {
        return new FileImageManagerImpl(application, fileLocalProviderManager);
    }
}
