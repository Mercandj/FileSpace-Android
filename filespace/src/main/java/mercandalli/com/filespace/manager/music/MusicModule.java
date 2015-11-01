package mercandalli.com.filespace.manager.music;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jonathan on 01/11/2015.
 */
@Module
public class MusicModule {

    @Provides
    @Singleton
    MusicPlayer provideMusicPlayer(Application application) {
        return new MusicPlayer(application);
    }

}
