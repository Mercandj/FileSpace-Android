package com.mercandalli.android.apps.files.push;

import android.app.Application;

import com.mercandalli.android.apps.files.main.FileAppComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A Dagger module used by the {@link FileAppComponent}.
 */
@Module
public class NotificationPushModule {

    @Provides
    @Singleton
    /* package */ NotificationPushManager provideNotificationPushManager(Application application) {
        return new NotificationPushManagerImpl(
                application);
    }
}
