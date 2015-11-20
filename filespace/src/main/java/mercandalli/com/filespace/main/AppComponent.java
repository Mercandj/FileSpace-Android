package mercandalli.com.filespace.main;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import mercandalli.com.filespace.file.FileManager;
import mercandalli.com.filespace.file.FileModule;
import mercandalli.com.filespace.file.audio.FileAudioDragAdapter;
import mercandalli.com.filespace.file.audio.FileAudioLocalFragment;
import mercandalli.com.filespace.file.audio.FileAudioModule;
import mercandalli.com.filespace.file.audio.FileAudioPlayer;
import mercandalli.com.filespace.common.activity.SearchActivity;
import mercandalli.com.filespace.file.FileChooserDialog;
import mercandalli.com.filespace.file.FileUploadDialog;
import mercandalli.com.filespace.file.cloud.FileCloudFragment;
import mercandalli.com.filespace.file.local.FileLocalFragment;
import mercandalli.com.filespace.file.cloud.FileMyCloudFragment;

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
