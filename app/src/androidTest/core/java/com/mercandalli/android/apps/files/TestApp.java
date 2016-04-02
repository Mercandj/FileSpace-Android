package com.mercandalli.android.apps.files;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import com.mercandalli.android.apps.files.file.audio.FileAudioModuleTest;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderModuleTest;
import com.mercandalli.android.apps.files.main.DaggerFileAppComponent;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.main.FileAppComponent;
import com.mercandalli.android.apps.files.main.FileAppModule;

public class TestApp extends FileApp {

    @Override
    protected void setupGraph() {
        mFileAppComponent = DaggerFileAppComponent.builder()
                .fileAppModule(new FileAppModule(this))
                .fileLocalProviderModule(new FileLocalProviderModuleTest())
                .fileAudioModule(new FileAudioModuleTest())
                .build();

        mFileAppComponent.inject(this);
    }

    public static void resetApp(final Context targetContext) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
                new Runnable() {
                    @Override
                    public void run() {
                        final FileAppComponent appComponent = FileApp.get().getFileAppComponent();
                        appComponent.provideFileProviderManager().clearCache();
                        appComponent.provideFileAudioManager().clearCache();
                        appComponent.provideFileImageManager().clearCache();
                        PreferenceManager.getDefaultSharedPreferences(targetContext).edit().clear().commit();
                    }
                }
        );
    }
}
