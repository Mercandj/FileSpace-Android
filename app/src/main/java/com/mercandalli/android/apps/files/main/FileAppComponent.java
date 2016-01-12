package com.mercandalli.android.apps.files.main;

import android.app.Application;

import com.mercandalli.android.apps.files.file.FileChooserDialog;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModule;
import com.mercandalli.android.apps.files.file.FileUploadDialog;
import com.mercandalli.android.apps.files.file.audio.FileAudioManager;
import com.mercandalli.android.apps.files.file.audio.FileAudioRowAdapter;
import com.mercandalli.android.apps.files.file.audio.FileAudioLocalFragment;
import com.mercandalli.android.apps.files.file.audio.FileAudioModule;
import com.mercandalli.android.apps.files.file.audio.FileAudioPlayer;
import com.mercandalli.android.apps.files.file.cloud.FileCloudDownloadedFragment;
import com.mercandalli.android.apps.files.file.cloud.FileCloudFragment;
import com.mercandalli.android.apps.files.file.cloud.FileMyCloudFragment;
import com.mercandalli.android.apps.files.file.local.FileLocalFragment;
import com.mercandalli.android.apps.files.file.local.SearchActivity;
import com.mercandalli.android.apps.files.file.image.FileImageLocalFragment;
import com.mercandalli.android.apps.files.support.SupportFragment;
import com.mercandalli.android.apps.files.support.SupportManager;
import com.mercandalli.android.apps.files.support.SupportModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                FileAppModule.class,
                FileModule.class,
                FileAudioModule.class,
                SupportModule.class
        }
)
public interface FileAppComponent {

    //Injections
    void inject(FileApp fileApp);

    void inject(MainActivity mainActivity);

    void inject(FileCloudFragment fileCloudFragment);

    void inject(FileMyCloudFragment fileMyCloudFragment);

    void inject(FileAudioLocalFragment fileAudioLocalFragment);

    void inject(FileLocalFragment fileLocalFragment);

    void inject(FileChooserDialog fileChooserDialog);

    void inject(FileUploadDialog fileUploadDialog);

    void inject(SearchActivity searchActivity);

    void inject(FileAudioRowAdapter fileAudioRowAdapter);

    void inject(FileCloudDownloadedFragment fileCloudDownloadedFragment);

    void inject(SupportFragment supportFragment);

    void inject(FileImageLocalFragment fileImageLocalFragment);

    //void inject(HomeFragment homeFragment);

    //Providers
    Application provideApp();

    FileManager provideFileManager();

    FileAudioManager provideFileAudioManager();

    FileAudioPlayer provideMusicPlayer();

    SupportManager provideSupportManager();
}
