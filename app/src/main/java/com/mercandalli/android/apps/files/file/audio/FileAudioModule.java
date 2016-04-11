package com.mercandalli.android.apps.files.file.audio;

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
public class FileAudioModule {

    @Provides
    @Singleton
    FileAudioManager provideFileAudioManager(
            final Application application,
            final FileLocalProviderManager fileLocalProviderManager) {
        // return new FileAudioManagerMock(application);
        return new FileAudioManagerImpl(application, fileLocalProviderManager);
    }

    @Provides
    @Singleton
    FileAudioPlayer provideFileAudioPlayer(Application application) {
        return new FileAudioPlayer(application);
    }
}
