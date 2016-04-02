package com.mercandalli.android.apps.files.file.audio;

import android.app.Application;

/**
 * Created by Jonathan on 02/04/2016.
 */
public class FileAudioModuleTest extends FileAudioModule {

    @Override
    FileAudioManager provideFileAudioManager(Application application) {
        return new FileAudioManagerTest(application);
    }

    @Override
    FileAudioPlayer provideFileAudioPlayer(Application application) {
        return new FileAudioPlayer(application);
    }
}
