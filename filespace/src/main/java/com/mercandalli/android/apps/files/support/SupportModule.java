package com.mercandalli.android.apps.files.support;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link com.mercandalli.android.apps.files.main.AppComponent}.
 */
@Module
public class SupportModule {

    @Provides
    @Singleton
    SupportManager provideSupportManager() {
        return new SupportManagerMock();
    }

}