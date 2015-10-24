package mercandalli.com.filespace.manager.file;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.listeners.ResultCallback;
import mercandalli.com.filespace.models.better.FileModel;
import mercandalli.com.filespace.net.FileApiService;
import mercandalli.com.filespace.net.response.GetFileResponse;
import mercandalli.com.filespace.net.response.GetFilesResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class FileManager {
    private FileDAO mFileDAO;
    private FileApiService mFileApiService;
    private Context mContext;

    public FileManager(FileDAO fileDAO, FileApiService fileApiService, Context context) {
        mFileDAO = fileDAO;
        mFileApiService = fileApiService;
        mContext = context;
    }

    public void getFileById(final int fileId, final ResultCallback<FileModel> result) {
        mFileApiService.getFileById(fileId, new Callback<GetFileResponse>() {
            @Override
            public void success(GetFileResponse getFileResponse, Response response) {
                FileModel.FileModelBuilder builder = new FileModel.FileModelBuilder()
                        .id(getFileResponse.getId())
                        .name(getFileResponse.getName())
                        .url(getFileResponse.getUrl());
                result.success(builder.build());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("FileManager", error.getMessage());
                result.failure();
            }
        });
    }

    public void getFiles(final int fileParentId, final boolean mine, final String search, final ResultCallback<List<FileModel>> result) {
        mFileApiService.getFiles(fileParentId, mine, search, new Callback<GetFilesResponse>() {
            @Override
            public void success(GetFilesResponse getFilesResponse, Response response) {
                List<FileModel> fileModelList = new ArrayList<>();
                for (GetFileResponse getFileResponse : getFilesResponse.getFiles()) {
                    fileModelList.add(new FileModel.FileModelBuilder()
                            .id(getFileResponse.getId())
                            .name(getFileResponse.getName())
                            .url(getFileResponse.getUrl())
                            .build());
                }
                result.success(fileModelList);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("FileManager", error.getMessage());
                result.failure();
            }
        });
    }
}
