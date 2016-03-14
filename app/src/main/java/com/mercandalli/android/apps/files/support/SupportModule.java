package com.mercandalli.android.apps.files.support;

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
public class SupportModule {

    @Provides
    @Singleton
    SupportManager provideSupportManager(Application application) {
        return new SupportManagerImpl(application, RetrofitUtils.getAuthorizedRetrofit().create(SupportOnlineApi.class));
        //return new SupportManagerMock(application);
    }

}