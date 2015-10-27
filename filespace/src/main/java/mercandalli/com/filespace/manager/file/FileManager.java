package mercandalli.com.filespace.manager.file;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.Spanned;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.ResultCallback;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileParentModel;
import mercandalli.com.filespace.model.file.FileTypeModel;
import mercandalli.com.filespace.model.file.FileTypeModelENUM;
import mercandalli.com.filespace.net.FileOnlineApi;
import mercandalli.com.filespace.net.TaskGetDownload;
import mercandalli.com.filespace.net.response.GetFileResponse;
import mercandalli.com.filespace.net.response.GetFilesResponse;
import mercandalli.com.filespace.local.FileLocalApi;
import mercandalli.com.filespace.local.FilePersistenceApi;
import mercandalli.com.filespace.ui.activitiy.FileTextActivity;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.HtmlUtils;
import mercandalli.com.filespace.util.NetUtils;
import mercandalli.com.filespace.util.StringPair;
import mercandalli.com.filespace.util.StringUtils;
import mercandalli.com.filespace.util.TimeUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class FileManager {
    private Context mContext;
    private FileOnlineApi mFileOnlineApi;
    private FileLocalApi mFileLocalApi;
    private FilePersistenceApi mFilePersistenceApi;

    public FileManager(Context context, FileOnlineApi fileOnlineApi, FileLocalApi fileLocalApi, FilePersistenceApi filePersistenceApi) {
        mContext = context;
        mFileOnlineApi = fileOnlineApi;
        mFileLocalApi = fileLocalApi;
        mFilePersistenceApi = filePersistenceApi;
    }

    public void getFiles(final FileParentModel fileParent, final String search, final int sortMode, final ResultCallback<List<FileModel>> resultCallback) {
        if (fileParent.isOnline()) {
            mFileOnlineApi.getFiles(fileParent.getId(), fileParent.isMine(), StringUtils.toEmptyIfNull(search), new Callback<GetFilesResponse>() {
                @Override
                public void success(GetFilesResponse getFilesResponse, Response response) {
                    List<GetFileResponse> result = getFilesResponse.getResult(mContext);
                    List<FileModel> fileModelList = new ArrayList<>();
                    for (GetFileResponse getFileResponse : result) {
                        fileModelList.add(getFileResponse.createModel());
                    }
                    resultCallback.success(fileModelList);
                }

                @Override
                public void failure(RetrofitError error) {
                    resultCallback.failure();
                }
            });
        }
        else {
            resultCallback.success(mFileLocalApi.getFiles(fileParent.getFile(), search, sortMode));
        }
    }

    public void download(final Activity activity, final FileModel fileModel, final IListener listener) {
        if (NetUtils.isInternetConnection(mContext) && fileModel.isOnline()) {
            if (fileModel.isDirectory()) {
                Toast.makeText(mContext, "Directory download not supported yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            String url = Constants.URL_API + "/" + Config.routeFile + "/" + fileModel.getId();
            String url_ouput = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Config.localFolderNameDefault + File.separator + fileModel.getFullName();
            new TaskGetDownload(activity, url, url_ouput, fileModel, listener).execute();
        }
    }

    public void rename(final FileModel fileModel, final String newName, final IListener listener) {
        if (fileModel.isOnline()) {
            mFileOnlineApi.rename(fileModel.getId(), new TypedString(newName), new Callback<GetFilesResponse>() {
                @Override
                public void success(GetFilesResponse getFilesResponse, Response response) {
                    getFilesResponse.getResult(mContext);
                    listener.execute();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else {
            File parent = fileModel.getFile().getParentFile();
            if (parent != null) {
                fileModel.getFile().renameTo(new File(parent.getAbsolutePath(), newName));
            }
            listener.execute();
        }
    }

    public void delete(final FileModel fileModel, final IListener listener) {
        if (fileModel.isOnline()) {
            mFileOnlineApi.delete(fileModel.getId(), "", new Callback<GetFilesResponse>() {
                @Override
                public void success(GetFilesResponse getFilesResponse, Response response) {
                    getFilesResponse.getResult(mContext);
                    listener.execute();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else {
            if (fileModel.getFile().isDirectory())
                FileUtils.deleteDirectory(fileModel.getFile());
            else
                fileModel.getFile().delete();
            listener.execute();
        }
    }

    public void setParent(final FileModel fileModel, final int id_file_parent, final IListener listener) {
        if (fileModel.isOnline()) {
            mFileOnlineApi.setParent(fileModel.getId(), new TypedString("" + id_file_parent), new Callback<GetFilesResponse>() {
                @Override
                public void success(GetFilesResponse getFilesResponse, Response response) {
                    getFilesResponse.getResult(mContext);
                    listener.execute();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    public void setPublic(final FileModel fileModel, final boolean isPublic, final IListener listener) {
        if (fileModel.isOnline()) {
            mFileOnlineApi.setPublic(fileModel.getId(), new TypedString("" + isPublic), new Callback<GetFilesResponse>() {
                @Override
                public void success(GetFilesResponse getFilesResponse, Response response) {
                    getFilesResponse.getResult(mContext);
                    listener.execute();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    public void executeOnline(final Activity activity, final FileModel fileModel, final ArrayList<FileModel> files, View view) {
        if (fileModel.getType().equals(FileTypeModelENUM.TEXT.type)) {
            FileTextActivity.start(activity, fileModel, true);
        } /* else if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
            Intent intent = new Intent(activity, FilePictureActivity.class);
            intent.putExtra("ID", this.id);
            intent.putExtra("TITLE", "" + this.getNameExt());
            intent.putExtra("URL_FILE", "" + this.onlineUrl);
            intent.putExtra("LOGIN", "" + this.app.getConfig().getUser().getAccessLogin());
            intent.putExtra("PASSWORD", "" + this.app.getConfig().getUser().getAccessPassword());
            intent.putExtra("ONLINE", true);
            intent.putExtra("SIZE_FILE", size);
            intent.putExtra("DATE_FILE", date_creation);
            if (view == null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.icon), "transitionIcon");
                Pair<View, String> p2 = Pair.create(view.findViewById(R.id.title), "transitionTitle");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, p1, p2);
                activity.startActivity(intent, options.toBundle());
            }
        } else if (this.type.equals(FileTypeModelENUM.AUDIO.type)) {
            Intent intent = new Intent(activity, FileAudioActivity.class);
            intent.putExtra("LOGIN", "" + app.getConfig().getUser().getAccessLogin());
            intent.putExtra("PASSWORD", "" + app.getConfig().getUser().getAccessPassword());
            intent.putExtra("ONLINE", true);
            intent.putExtra("FILE", this);
            ArrayList<ModelFile> tmpFiles = new ArrayList<>();
            for (ModelFile f : files)
                if (f.type.equals(ModelFileTypeENUM.AUDIO.type))
                    tmpFiles.add(f);
            intent.putParcelableArrayListExtra("FILES", tmpFiles);
            if (view == null) {
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.icon), "transitionIcon");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mActivity, p1);
                mActivity.startActivity(intent, options.toBundle());
            }
        } else if (this.type.equals(FileTypeModelENUM.FILESPACE.type)) {
            if (content != null) {
                if (content.timer.timer_date != null) {
                    Intent intent = new Intent(activity, FileTimerActivity.class);
                    intent.putExtra("URL_FILE", "" + this.onlineUrl);
                    intent.putExtra("LOGIN", "" + this.app.getConfig().getUser().getAccessLogin());
                    intent.putExtra("ONLINE", true);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    intent.putExtra("TIMER_DATE", "" + dateFormat.format(content.timer.timer_date));
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else if (!StringUtils.isNullOrEmpty(content.article.article_content_1)) {
                    FileTextActivity.startForSelection(activity, this, true);
                }
            }
        }*/
    }

    public Spanned toSpanned(final FileModel fileModel) {
        final FileTypeModel type = fileModel.getType();
        final boolean isDirectory = fileModel.isDirectory();
        final long size = fileModel.getSize();
        final boolean isPublic = fileModel.isPublic();
        final Date dateCreation = fileModel.getDateCreation();

        List<StringPair> spl = new ArrayList<>();
        spl.add(new StringPair("Name", fileModel.getName()));
        if (!fileModel.isDirectory()) {
            spl.add(new StringPair("Extension", type.toString()));
        }
        spl.add(new StringPair("Type", type.getTitle()));
        if (!isDirectory || size != 0) {
            spl.add(new StringPair("Size", FileUtils.humanReadableByteCount(size)));
        }
        if (dateCreation != null) {
            if (fileModel.isOnline()) {
                spl.add(new StringPair("Upload date", TimeUtils.getDate(dateCreation)));
            } else {
                spl.add(new StringPair("Last modification date", TimeUtils.getDate(dateCreation)));
            }
        }
        if (fileModel.isOnline())
            spl.add(new StringPair("Visibility", isPublic ? "Public" : "Private"));
        return HtmlUtils.createListItem(spl);
    }
}
