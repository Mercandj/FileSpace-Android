package com.mercandalli.android.filespace.file.cloud;

import com.mercandalli.android.filespace.main.Config;
import com.mercandalli.android.filespace.file.FileModel;
import com.mercandalli.android.filespace.file.cloud.response.FilesResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * The {@link FileModel} online api .
 */
public interface FileOnlineApi {

    @GET("/" + Config.routeFile)
    void getFiles(
            @Query("id_file_parent") int fileParentId,
            @Query("all-public") String publicFiles,
            @Query("search") String search,
            Callback<FilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile)
    void uploadFile(
            @Part("file") TypedFile file,
            @Part("url") TypedString url,
            @Part("id_file_parent") TypedString id_file_parent,
            @Part("directory") TypedString directory,
            Callback<FilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile + "/{id}")
    void rename(
            @Path("id") int fileId,
            @Part("url") TypedString newFullName,
            Callback<FilesResponse> result);

    @POST("/" + Config.routeFileDelete + "/{id}")
    void delete(
            @Path("id") int fileId,
            @Body String body,
            Callback<FilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile + "/{id}")
    void setParent(
            @Path("id") int fileId,
            @Part("id_file_parent") TypedString idFileParent,
            Callback<FilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile + "/{id}")
    void setPublic(
            @Path("id") int fileId,
            @Part("public") TypedString isPublic,
            Callback<FilesResponse> result);
}
