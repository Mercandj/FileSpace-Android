package mercandalli.com.filespace.net;

import java.util.List;

import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.net.response.GetFileResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Jonathan on 23/10/2015.
 */
public interface FileApiService {
    @GET("/" + Config.routeFile + "/{id}")
    void getFileById(
            @Path("id") int fileId,
            Callback<GetFileResponse> result);

    @GET("/" + Config.routeFile)
    void getFiles(
            @Query("id_file_parent") int fileParentId,
            @Query("mine") boolean mine,
            @Query("search") String search,
            Callback<List<GetFileResponse>> result);
}
