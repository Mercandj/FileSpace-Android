package com.mercandalli.android.apps.files.main.version;

import android.app.Application;

import com.mercandalli.android.apps.files.main.network.RetrofitUtils;
import com.mercandalli.android.apps.files.main.FileAppComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link FileAppComponent}.
 */
@Module
public class VersionModule {

    @Provides
    @Singleton
    VersionManager provideVersionManager(Application application) {
        final VersionApi versionApi = RetrofitUtils.getAuthorizedRestAdapter().create(VersionApi.class);

        return new VersionManager(application, versionApi);
    }
}