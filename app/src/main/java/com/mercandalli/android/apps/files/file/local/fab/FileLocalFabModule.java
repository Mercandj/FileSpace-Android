package com.mercandalli.android.apps.files.file.local.fab;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link com.mercandalli.android.apps.files.main.FileAppComponent}.
 */
@Module
public class FileLocalFabModule {

    @Provides
    @Singleton
    FileLocalFabManager provideFabManager() {
        return new FileLocalFabManagerImpl();
    }
}