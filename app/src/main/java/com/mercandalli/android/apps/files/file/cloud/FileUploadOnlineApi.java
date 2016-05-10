package com.mercandalli.android.apps.files.file.cloud;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.cloud.response.FilesResponse;
import com.mercandalli.android.apps.files.main.Config;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * The {@link FileModel} online api .
 */
public interface FileUploadOnlineApi {

    @Multipart
    @POST("/" + Config.ROUTE_FILE)
    Call<FilesResponse> uploadFile(
            @PartMap() Map<String, ProgressRequestBody> mapFileAndData
            //@Part("file") FileUploadTypedFile file,
            //@Field("url") String url,
            //@Field("id_file_parent") String id_file_parent,
            //@Field("directory") String directory
    );
}
