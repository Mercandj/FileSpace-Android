package com.mercandalli.android.apps.files.file.cloud;

import com.mercandalli.android.apps.files.file.FileUploadTypedFile;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.cloud.response.FilesResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
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
            @Part("file") FileUploadTypedFile file,
            @Part("url") TypedString url,
            @Part("id_file_parent") TypedString id_file_parent,
            @Part("directory") TypedString directory,
            Callback<FilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile + "/{id_file_to_rename}")
    void rename(
            @Path("id_file_to_rename") int fileId,
            @Part("url") TypedString newFullName,
            Callback<FilesResponse> result);

    @POST("/" + Config.routeFileDelete + "/{id_file_to_delete}")
    void delete(
            @Path("id_file_to_delete") int fileId,
            @Body String body,
            Callback<FilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile + "/{id_file}")
    void setParent(
            @Path("id_file") int fileId,
            @Part("id_file_parent") TypedString idFileParent,
            Callback<FilesResponse> result);

    @Multipart
    @POST("/" + Config.routeFile + "/{id_file}")
    void setPublic(
            @Path("id_file") int fileId,
            @Part("public") TypedString isPublic,
            Callback<FilesResponse> result);
}
