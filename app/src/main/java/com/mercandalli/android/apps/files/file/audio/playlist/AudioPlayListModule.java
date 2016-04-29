package com.mercandalli.android.apps.files.file.audio.playlist;

import android.app.Application;

import com.mercandalli.android.apps.files.main.FileAppComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link FileAppComponent}.
 */
@Module
public class AudioPlayListModule {

    @Provides
    @Singleton
    AudioPlayListManager provideFileAudioManager(
            final Application application) {
        return new AudioPlayListManagerImpl(application);
    }

}
