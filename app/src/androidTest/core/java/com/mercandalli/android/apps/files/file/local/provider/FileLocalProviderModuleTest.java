package com.mercandalli.android.apps.files.file.local.provider;

import android.app.Application;

public class FileLocalProviderModuleTest extends FileLocalProviderModule {

    @Override
    FileLocalProviderManager provideFileProviderManager(Application application) {
        return new FileLocalProviderManagerTest(application);
    }
}
