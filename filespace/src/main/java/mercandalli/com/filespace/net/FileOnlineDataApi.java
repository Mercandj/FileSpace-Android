package mercandalli.com.filespace.net;

import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.net.response.GetFilesResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by Jonathan on 23/10/2015.
 */
public interface FileOnlineDataApi {

    @GET("/" + Config.routeFile)
    void getFiles(
            @Query("id_file_parent") int fileParentId,
            @Query("mine") boolean mine,
            @Query("search") String search,
            Callback<GetFilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile)
    void uploadFile(
            @Part("file") TypedFile file,
            @Part("url") TypedString url,
            @Part("id_file_parent") TypedString id_file_parent,
            @Part("directory") TypedString directory,
            Callback<GetFilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile + "/{id}")
    void rename(
            @Path("id") int fileId,
            @Part("url") TypedString newFullName,
            Callback<GetFilesResponse> result);

    @POST("/" + Config.routeFileDelete + "/{id}")
    void delete(
            @Path("id") int fileId,
            Callback<GetFilesResponse> result);

    @POST("/" + Config.routeFileDelete + "/{id}")
    void setParent(
            @Path("id") int fileId,
            @Part("id_file_parent") TypedString idFileParent,
            Callback<GetFilesResponse> result);

    @POST("/" + Config.routeFileDelete + "/{id}")
    void setPublic(
            @Path("id") int fileId,
            @Part("public") TypedString isPublic,
            Callback<GetFilesResponse> result);
}
