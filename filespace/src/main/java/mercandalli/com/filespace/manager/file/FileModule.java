package mercandalli.com.filespace.manager.file;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mercandalli.com.filespace.net.FileOnlineDataApi;
import mercandalli.com.filespace.persistence.FileLocalDataApi;
import mercandalli.com.filespace.utils.RetrofitUtils;

/**
 * Created by Jonathan on 23/10/2015.
 */
@Module
public class FileModule {

    @Provides
    @Singleton
    FileManager provideFileManager(Application application) {
        FileLocalDataApi fileLocalDataApi = new FileLocalDataApi();
        FileOnlineDataApi fileOnlineDataApi = RetrofitUtils.getAuthorizedRestAdapter().create(FileOnlineDataApi.class);
        return new FileManager(fileLocalDataApi, fileOnlineDataApi, application);
    }

}