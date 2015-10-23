package mercandalli.com.filespace.manager.file;

import android.app.Application;
import android.content.Context;

import mercandalli.com.filespace.net.FileApiService;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class FileManager {
    private FileDAO mFileDAO;
    private FileApiService mFileApiService;
    private Context mContext;

    public FileManager(FileDAO fileDAO, FileApiService fileApiService, Application app) {
        mFileDAO = fileDAO;
        mFileApiService = fileApiService;
        mContext = app.getApplicationContext();
    }
}
