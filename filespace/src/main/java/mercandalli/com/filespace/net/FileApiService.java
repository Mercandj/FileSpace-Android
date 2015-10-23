package mercandalli.com.filespace.net;

import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.net.response.GetFileResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Jonathan on 23/10/2015.
 */
public interface FileApiService {
    @GET("/" + Config.routeFile + "/{id}")
    void getFileById(@Path("id") String fileId, Callback<GetFileResponse> result);
}
