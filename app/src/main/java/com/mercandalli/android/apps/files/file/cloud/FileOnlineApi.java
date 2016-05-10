package com.mercandalli.android.apps.files.file.cloud;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.cloud.response.FilesResponse;
import com.mercandalli.android.apps.files.main.Config;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The {@link FileModel} online api .
 */
public interface FileOnlineApi {

    @GET("/" + Config.ROUTE_FILE)
    Call<FilesResponse> getFiles(
            @Query("id_file_parent") int fileParentId,
            @Query("all-public") String publicFiles,
            @Query("search") String search);

    @FormUrlEncoded
    @POST("/" + Config.ROUTE_FILE + "/{id_file_to_rename}")
    Call<FilesResponse> rename(
            @Path("id_file_to_rename") int fileId,
            @Field("url") String newFullName);

    @POST("/" + Config.ROUTE_FILE_DELETE + "/{id_file_to_delete}")
    Call<FilesResponse> delete(
            @Path("id_file_to_delete") int fileId,
            @Body String body);

    @FormUrlEncoded
    @POST("/" + Config.ROUTE_FILE + "/{id_file}")
    Call<FilesResponse> setParent(
            @Path("id_file") int fileId,
            @Field("id_file_parent") String idFileParent);

    @FormUrlEncoded
    @POST("/" + Config.ROUTE_FILE + "/{id_file}")
    Call<FilesResponse> setPublic(
            @Path("id_file") int fileId,
            @Field("public") String isPublic);
}
