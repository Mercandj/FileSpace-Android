package com.mercandalli.android.apps.files.file.audio;

import android.app.Application;

import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;

/**
 * A Dagger module used to test the app.
 */
public class FileAudioModuleTest extends FileAudioModule {

    @Override
    FileAudioManager provideFileAudioManager(
            final Application application,
            final FileLocalProviderManager fileLocalProviderManager,
            final FileManager fileManager) {
        return new FileAudioManagerTest(application, fileLocalProviderManager, fileManager);
    }

    @Override
    FileAudioPlayerManager provideFileAudioPlayer(Application application) {
        return new FileAudioPlayerManager(application);
    }
}
