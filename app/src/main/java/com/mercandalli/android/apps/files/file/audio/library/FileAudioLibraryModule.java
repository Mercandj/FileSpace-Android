package com.mercandalli.android.apps.files.file.audio.library;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileAudioLibraryModule {

    @Provides
    @Singleton
    FileAudioLibraryManager provideFileAudioLibraryManager(Application application) {
        return new FileAudioLibraryManager(application);
    }

}
