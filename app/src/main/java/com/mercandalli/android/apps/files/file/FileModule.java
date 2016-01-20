package com.mercandalli.android.apps.files.file;

import android.app.Application;

import com.mercandalli.android.apps.files.common.util.RetrofitUtils;
import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;
import com.mercandalli.android.apps.files.main.FileAppComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link FileAppComponent}.
 */
@Module
public class FileModule {

    @Provides
    @Singleton
    FileManager provideFileManager(Application application) {
        final FileOnlineApi fileOnlineApi = RetrofitUtils.getAuthorizedRestAdapter().create(FileOnlineApi.class);

        return new FileManagerImpl(application, fileOnlineApi);
        // return new FileManagerMockImpl(application, fileOnlineApi);
    }
}