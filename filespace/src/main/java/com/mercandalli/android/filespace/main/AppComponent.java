package com.mercandalli.android.filespace.main;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import com.mercandalli.android.filespace.file.FileManager;
import com.mercandalli.android.filespace.file.FileModule;
import com.mercandalli.android.filespace.file.audio.FileAudioDragAdapter;
import com.mercandalli.android.filespace.file.audio.FileAudioLocalFragment;
import com.mercandalli.android.filespace.file.audio.FileAudioModule;
import com.mercandalli.android.filespace.file.audio.FileAudioPlayer;
import com.mercandalli.android.filespace.search.SearchActivity;
import com.mercandalli.android.filespace.file.FileChooserDialog;
import com.mercandalli.android.filespace.file.FileUploadDialog;
import com.mercandalli.android.filespace.file.cloud.FileCloudFragment;
import com.mercandalli.android.filespace.file.local.FileLocalFragment;
import com.mercandalli.android.filespace.file.cloud.FileMyCloudFragment;

@Singleton
@Component(
        modules = {
                AppModule.class,
                FileModule.class,
                FileAudioModule.class
        }
)
public interface AppComponent {

    //Injections
    void inject(App app);

    void inject(MainActivity mainActivity);

    void inject(FileCloudFragment fileCloudFragment);

    void inject(FileMyCloudFragment fileMyCloudFragment);

    void inject(FileAudioLocalFragment fileAudioLocalFragment);

    void inject(FileLocalFragment fileLocalFragment);

    void inject(FileChooserDialog fileChooserDialog);

    void inject(FileUploadDialog fileUploadDialog);

    void inject(SearchActivity searchActivity);

    void inject(FileAudioDragAdapter fileAudioDragAdapter);

    //void inject(HomeFragment homeFragment);

    //Providers
    Application provideApp();

    FileManager provideFileManager();

    FileAudioPlayer provideMusicPlayer();
}
