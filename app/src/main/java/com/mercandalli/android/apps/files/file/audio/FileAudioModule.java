package com.mercandalli.android.apps.files.file.audio;

import android.app.Application;

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
    FileAudioManager provideFileAudioManager(Application application) {
        // return new FileAudioManagerMockImpl(application);
        return new FileAudioManagerImpl(application);
    }

    @Provides
    @Singleton
    FileAudioPlayer provideFileAudioPlayer(Application application) {
        return new FileAudioPlayer(application);
    }
}
