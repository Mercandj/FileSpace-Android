package com.mercandalli.android.apps.files.file;

import android.app.Application;

import com.mercandalli.android.apps.files.common.util.RetrofitUtils;
import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;
import com.mercandalli.android.apps.files.file.local.FileLocalApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link com.mercandalli.android.apps.files.main.AppComponent}.
 */
@Module
public class FileModule {

    @Provides
    @Singleton
    FileManager provideFileManager(Application application) {
        FileOnlineApi fileOnlineApi = RetrofitUtils.getAuthorizedRestAdapter().create(FileOnlineApi.class);
        FileLocalApi fileLocalApi = new FileLocalApi();
        FilePersistenceApi filePersistenceApi = new FilePersistenceApi();
        return new FileManagerImpl(application, fileOnlineApi, fileLocalApi, filePersistenceApi);
    }

}