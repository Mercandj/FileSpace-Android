package mercandalli.com.filespace.file;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mercandalli.com.filespace.file.cloud.FileOnlineApi;
import mercandalli.com.filespace.file.local.FileLocalApi;
import mercandalli.com.filespace.common.util.RetrofitUtils;

/**
 * Created by Jonathan on 23/10/2015.
 */
@Module
public class FileModule {

    @Provides
    @Singleton
    FileManager provideFileManager(Application application) {
        FileOnlineApi fileOnlineApi = RetrofitUtils.getAuthorizedRestAdapter().create(FileOnlineApi.class);
        FileLocalApi fileLocalApi = new FileLocalApi();
        FilePersistenceApi filePersistenceApi = new FilePersistenceApi();
        return new FileManager(application, fileOnlineApi, fileLocalApi, filePersistenceApi);
    }

}