package mercandalli.com.filespace.config;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.manager.file.FileModule;
import mercandalli.com.filespace.ui.activitiy.MainActivity;
import mercandalli.com.filespace.ui.fragment.file.FileCloudFragment;
import mercandalli.com.filespace.ui.fragment.file.FileLocalFragment;
import mercandalli.com.filespace.ui.fragment.file.FileLocalMusicFragment;
import mercandalli.com.filespace.ui.fragment.file.FileMyCloudFragment;

@Singleton
@Component(
        modules = {
                MyAppModule.class,
                FileModule.class
        }
)
public interface MyAppComponent {

    //Injections
    void inject(MyApp app);

    void inject(MainActivity mainActivity);

    void inject(FileCloudFragment fileCloudFragment);

    void inject(FileMyCloudFragment fileMyCloudFragment);

    void inject(FileLocalMusicFragment fileLocalMusicFragment);

    void inject(FileLocalFragment fileLocalFragment);

    //void inject(HomeFragment homeFragment);

    //Providers
    Application provideApp();

    FileManager provideFileManager();
}
