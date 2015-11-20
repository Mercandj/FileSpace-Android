package mercandalli.com.filespace.file.audio;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jonathan on 01/11/2015.
 */
@Module
public class FileAudioModule {

    @Provides
    @Singleton
    FileAudioPlayer provideMusicPlayer(Application application) {
        return new FileAudioPlayer(application);
    }

}
