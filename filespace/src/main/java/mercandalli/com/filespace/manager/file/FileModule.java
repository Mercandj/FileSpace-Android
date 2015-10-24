package mercandalli.com.filespace.manager.file;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mercandalli.com.filespace.net.FileApiService;
import mercandalli.com.filespace.utils.RetrofitUtils;

/**
 * Created by Jonathan on 23/10/2015.
 */
@Module
public class FileModule {

    @Provides
    @Singleton
    FileManager provideFileManager(Application app) {
        FileDAO fileDAO = new FileDAO();
        FileApiService karaokeApiService = RetrofitUtils.getAuthorizedRestAdapter().create(FileApiService.class);
        return new FileManager(fileDAO, karaokeApiService, app);
    }

}