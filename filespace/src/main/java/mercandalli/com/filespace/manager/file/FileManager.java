package mercandalli.com.filespace.manager.file;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.listener.ResultCallback;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.net.FileOnlineDataApi;
import mercandalli.com.filespace.net.response.GetFileResponse;
import mercandalli.com.filespace.net.response.GetFilesResponse;
import mercandalli.com.filespace.persistence.file.FileLocalDataApi;
import mercandalli.com.filespace.util.StringUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class FileManager {
    private FileLocalDataApi mFileLocalDataApi;
    private FileOnlineDataApi mFileOnlineDataApi;
    private Context mContext;

    public FileManager(FileLocalDataApi fileLocalDataApi, FileOnlineDataApi fileOnlineDataApi, Context context) {
        mFileLocalDataApi = fileLocalDataApi;
        mFileOnlineDataApi = fileOnlineDataApi;
        mContext = context;
    }

    public void getFileById(final int fileId, final ResultCallback<FileModel> result) {
        mFileOnlineDataApi.getFileById(fileId, new Callback<GetFileResponse>() {
            @Override
            public void success(GetFileResponse getFileResponse, Response response) {
                result.success(getFileResponse.createFileModel());
            }

            @Override
            public void failure(RetrofitError error) {
                result.failure();
            }
        });
    }

    public void getFiles(final int fileParentId, final boolean mine, final String search, final ResultCallback<List<FileModel>> result) {
        mFileOnlineDataApi.getFiles(fileParentId, mine, StringUtils.toEmptyIfNull(search), new Callback<GetFilesResponse>() {
            @Override
            public void success(GetFilesResponse getFilesResponse, Response response) {
                List<FileModel> fileModelList = new ArrayList<>();
                for (GetFileResponse getFileResponse : getFilesResponse.getFiles()) {
                    fileModelList.add(getFileResponse.createFileModel());
                }
                result.success(fileModelList);
            }

            @Override
            public void failure(RetrofitError error) {
                result.failure();
            }
        });
    }
}
