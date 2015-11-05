package mercandalli.com.filespace.config;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.manager.file.FileModule;
import mercandalli.com.filespace.manager.music.MusicModule;
import mercandalli.com.filespace.manager.music.MusicPlayer;
import mercandalli.com.filespace.ui.activity.MainActivity;
import mercandalli.com.filespace.ui.activity.SearchActivity;
import mercandalli.com.filespace.ui.adapter.file.FileMusicModelDragAdapter;
import mercandalli.com.filespace.ui.dialog.DialogFileChooser;
import mercandalli.com.filespace.ui.dialog.DialogUpload;
import mercandalli.com.filespace.ui.fragment.file.FileCloudFragment;
import mercandalli.com.filespace.ui.fragment.file.FileLocalFragment;
import mercandalli.com.filespace.ui.fragment.file.FileLocalMusicFragment;
import mercandalli.com.filespace.ui.fragment.file.FileMyCloudFragment;

@Singleton
@Component(
        modules = {
                AppModule.class,
                FileModule.class,
                MusicModule.class
        }
)
public interface AppComponent {

    //Injections
    void inject(App app);

    void inject(MainActivity mainActivity);

    void inject(FileCloudFragment fileCloudFragment);

    void inject(FileMyCloudFragment fileMyCloudFragment);

    void inject(FileLocalMusicFragment fileLocalMusicFragment);

    void inject(FileLocalFragment fileLocalFragment);

    void inject(DialogFileChooser dialogFileChooser);

    void inject(DialogUpload dialogUpload);

    void inject(SearchActivity searchActivity);

    void inject(FileMusicModelDragAdapter fileMusicModelDragAdapter);

    //void inject(HomeFragment homeFragment);

    //Providers
    Application provideApp();

    FileManager provideFileManager();

    MusicPlayer provideMusicPlayer();
}
