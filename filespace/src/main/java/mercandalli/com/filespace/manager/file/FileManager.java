package mercandalli.com.filespace.manager.file;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.Spanned;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.ResultCallback;
import mercandalli.com.filespace.local.FileLocalApi;
import mercandalli.com.filespace.local.FilePersistenceApi;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileMusicModel;
import mercandalli.com.filespace.model.file.FileParentModel;
import mercandalli.com.filespace.model.file.FileTypeModel;
import mercandalli.com.filespace.model.file.FileTypeModelENUM;
import mercandalli.com.filespace.net.FileOnlineApi;
import mercandalli.com.filespace.net.TaskGetDownload;
import mercandalli.com.filespace.net.response.FileResponse;
import mercandalli.com.filespace.net.response.FilesResponse;
import mercandalli.com.filespace.ui.activitiy.FileAudioActivity;
import mercandalli.com.filespace.ui.activitiy.FilePictureActivity;
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
            mFileOnlineApi.getFiles(fileParent.getId(), fileParent.isMine(), StringUtils.toEmptyIfNull(search), new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    List<FileResponse> result = filesResponse.getResult(mContext);
                    List<FileModel> fileModelList = new ArrayList<>();
                    for (FileResponse fileResponse : result) {
                        fileModelList.add(fileResponse.createModel());
                    }
                    resultCallback.success(fileModelList);
                }

                @Override
                public void failure(RetrofitError error) {
                    resultCallback.failure();
                }
            });
        } else {
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
            mFileOnlineApi.rename(fileModel.getId(), new TypedString(newName), new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    filesResponse.getResult(mContext);
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
            mFileOnlineApi.delete(fileModel.getId(), "", new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    filesResponse.getResult(mContext);
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
            mFileOnlineApi.setParent(fileModel.getId(), new TypedString("" + id_file_parent), new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    filesResponse.getResult(mContext);
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
            mFileOnlineApi.setPublic(fileModel.getId(), new TypedString("" + isPublic), new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    filesResponse.getResult(mContext);
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
        } else if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
            Intent intent = new Intent(activity, FilePictureActivity.class);
            intent.putExtra("ID", fileModel.getId());
            intent.putExtra("TITLE", "" + fileModel.getFullName());
            intent.putExtra("URL_FILE", "" + fileModel.getOnlineUrl());
            intent.putExtra("ONLINE", true);
            intent.putExtra("SIZE_FILE", fileModel.getSize());
            intent.putExtra("DATE_FILE", fileModel.getDateCreation());
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
        } else if (fileModel.getType().equals(FileTypeModelENUM.AUDIO.type)) {
            Intent intent = new Intent(activity, FileAudioActivity.class);
            intent.putExtra("ONLINE", true);
            intent.putExtra("FILE", fileModel);
            ArrayList<FileModel> tmpFiles = new ArrayList<>();
            for (FileModel f : files)
                if (f.getType().equals(FileTypeModelENUM.AUDIO.type))
                    tmpFiles.add(f);
            intent.putParcelableArrayListExtra("FILES", tmpFiles);
            if (view == null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.icon), "transitionIcon");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, p1);
                activity.startActivity(intent, options.toBundle());
            }
        } /* else if (this.type.equals(FileTypeModelENUM.FILESPACE.type)) {
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

    public void executeLocal(final Activity activity, final FileModel fileModel, List<FileMusicModel> files, View view) {
        if (fileModel.isOnline()) {
            return;
        }
        if (fileModel.getType().equals(FileTypeModelENUM.APK.type)) {
            Intent apkIntent = new Intent();
            apkIntent.setAction(Intent.ACTION_VIEW);
            apkIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "application/vnd.android.package-archive");
            activity.startActivity(apkIntent);
        } else if (fileModel.getType().equals(FileTypeModelENUM.TEXT.type)) {
            Intent txtIntent = new Intent();
            txtIntent.setAction(Intent.ACTION_VIEW);
            txtIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "text/plain");
            try {
                activity.startActivity(txtIntent);
            } catch (ActivityNotFoundException e) {
                txtIntent.setType("text/*");
                activity.startActivity(txtIntent);
            }
        } else if (fileModel.getType().equals(FileTypeModelENUM.HTML.type)) {
            Intent htmlIntent = new Intent();
            htmlIntent.setAction(Intent.ACTION_VIEW);
            htmlIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "text/html");
            try {
                activity.startActivity(htmlIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        } else if (fileModel.getType().equals(FileTypeModelENUM.AUDIO.type)) {
            Intent intent = new Intent(activity, FileAudioActivity.class);
            intent.putExtra("ONLINE", false);
            intent.putExtra("FILE", fileModel);
            ArrayList<FileModel> tmpFiles = new ArrayList<>();
            for (FileModel f : files)
                if (f.getType() != null && f.getType().equals(FileTypeModelENUM.AUDIO.type))
                    tmpFiles.add(f);
            intent.putParcelableArrayListExtra("FILES", tmpFiles);
            if (view == null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.icon), "transitionIcon");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, p1);
                activity.startActivity(intent, options.toBundle());
            }
        } else if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
            Intent picIntent = new Intent();
            picIntent.setAction(Intent.ACTION_VIEW);
            picIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "image/*");
            activity.startActivity(picIntent);
        } else if (fileModel.getType().equals(FileTypeModelENUM.VIDEO.type)) {
            Intent videoIntent = new Intent();
            videoIntent.setAction(Intent.ACTION_VIEW);
            videoIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "video/*");
            try {
                activity.startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        } else if (fileModel.getType().equals(FileTypeModelENUM.PDF.type)) {
            Intent pdfIntent = new Intent();
            pdfIntent.setAction(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "application/pdf");
            try {
                activity.startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openLocalAs(final Activity activity, final FileModel fileModel) {
        if (!fileModel.isOnline()) {
            return;
        }
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(activity);
        String[] menuList = {
                activity.getString(R.string.text),
                activity.getString(R.string.image),
                activity.getString(R.string.audio),
                activity.getString(R.string.video),
                activity.getString(R.string.other)};
        menuAlert.setTitle(activity.getString(R.string.open_as));

        menuAlert.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String type_mime = "";
                        switch (item) {
                            case 0:
                                type_mime = "text/plain";
                                break;
                            case 1:
                                type_mime = "image/*";
                                break;
                            case 2:
                                type_mime = "audio/*";
                                break;
                            case 3:
                                type_mime = "video/*";
                                break;
                        }
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(fileModel.getFile()), type_mime);
                        activity.startActivity(i);
                    }
                });
        AlertDialog menuDrop = menuAlert.create();
        menuDrop.show();
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

    public List<StringPair> getForUpload(final FileModel fileModel) {
        List<StringPair> parameters = new ArrayList<>();
        if (fileModel.getName() != null) {
            parameters.add(new StringPair("url", fileModel.getName()));
        }
        if (fileModel.isDirectory()) {
            parameters.add(new StringPair("directory", "true"));
        }
        if (fileModel.getIdFileParent() != -1) {
            parameters.add(new StringPair("id_file_parent", "" + fileModel.getIdFileParent()));
        }
        return parameters;
    }
}
