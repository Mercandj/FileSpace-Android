package com.mercandalli.android.apps.files.file;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.common.net.TaskGetDownload;
import com.mercandalli.android.apps.files.common.util.HtmlUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.util.TimeUtils;
import com.mercandalli.android.apps.files.file.audio.CoverUtils;
import com.mercandalli.android.apps.files.file.audio.FileAudioActivity;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;
import com.mercandalli.android.apps.files.file.cloud.response.FileResponse;
import com.mercandalli.android.apps.files.file.cloud.response.FilesResponse;
import com.mercandalli.android.apps.files.file.filespace.FileSpaceModel;
import com.mercandalli.android.apps.files.file.filespace.FileTimerActivity;
import com.mercandalli.android.apps.files.file.image.FileImageActivity;
import com.mercandalli.android.apps.files.file.local.FileLocalApi;
import com.mercandalli.android.apps.files.file.text.FileTextActivity;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.precondition.Preconditions;
import com.mercandalli.android.apps.files.common.util.StringUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A {@link FileModel} Manager.
 */
public class FileManagerImpl extends FileManager /*implements FileUploadTypedFile.FileUploadListener*/ {

    private static final String LIKE = " LIKE ?";
    private static final String MIME_TEXT = "text/plain";
    private static final String FAILED_FILE_IS_NULL = "Failed: File is null.";

    private final Context mContextApp;
    private final FileOnlineApi mFileOnlineApi;

    private final FileLocalApi mFileLocalApi;
    private final Handler mUiHandler;
    private final Thread mUiThread;

    public FileManagerImpl(final Context contextApp, final FileOnlineApi fileOnlineApi) {
        Preconditions.checkNotNull(contextApp);

        mContextApp = contextApp;
        mFileOnlineApi = fileOnlineApi;
        mFileLocalApi = new FileLocalApi();

        final Looper mainLooper = Looper.getMainLooper();
        mUiHandler = new Handler(mainLooper);
        mUiThread = mainLooper.getThread();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFiles(
            final File fileParent,
            final ResultCallback<List<FileModel>> resultCallback) {
        new Thread() {
            @Override
            public void run() {
                notifyGetFilesSucceeded(mFileLocalApi.getFiles(fileParent), resultCallback);
            }
        }.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFiles(
            final FileModel fileParent,
            final ResultCallback<List<FileModel>> resultCallback) {
        getFiles(fileParent, true, resultCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFiles(
            final FileModel fileParent,
            final boolean areMyFiles,
            final ResultCallback<List<FileModel>> resultCallback) {

        if (!fileParent.isOnline()) {
            new Thread() {
                @Override
                public void run() {
                    notifyGetFilesSucceeded(mFileLocalApi.getFiles(fileParent.getFile()), resultCallback);
                }
            }.start();
            return;
        }
        final Call<FilesResponse> call = mFileOnlineApi.getFiles(fileParent.getId(), areMyFiles ? "" : "true", "");
        call.enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                if (response.isSuccessful()) {
                    final FilesResponse filesResponse = response.body();
                    final List<FileResponse> result = filesResponse.getResult(mContextApp);
                    final List<FileModel> fileModelList = new ArrayList<>();
                    for (FileResponse fileResponse : result) {
                        fileModelList.add(fileResponse.createModel());
                    }
                    resultCallback.success(fileModelList);
                } else {
                    resultCallback.failure();
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {
                resultCallback.failure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void download(
            final Activity activity,
            final FileModel fileModel,
            final IListener listener) {

        if (!NetUtils.isInternetConnection(mContextApp) || !fileModel.isOnline()) {
            return;
        }
        if (fileModel.isDirectory()) {
            Toast.makeText(mContextApp, "Directory download not supported yet.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        final String pathFolderDownloaded = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + Config.LOCAL_FOLDER_NAME_DEFAULT;
        final File folder = new File(pathFolderDownloaded);
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdir();
        }
        new TaskGetDownload(activity, fileModel.getOnlineUrl(), pathFolderDownloaded + File.separator + fileModel.getFullName(),
                fileModel, listener).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(
            final FileModel fileModel,
            final int idFileParent,
            @Nullable final IListener listener) {

        if (!NetUtils.isInternetConnection(mContextApp) || fileModel.isOnline()) {
            return;
        }
        if (fileModel.isDirectory()) {
            Toast.makeText(mContextApp, "Directory download not supported yet.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        final File file = fileModel.getFile();
        if (file == null) {
            Toast.makeText(mContextApp, FAILED_FILE_IS_NULL, Toast.LENGTH_SHORT).show();
            return;
        }

        final Map<String, RequestBody> params = new HashMap<>();

        final String filename = file.getName();
        RequestBody photo = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        params.put("file\"; filename=\"" + filename, photo);

        params.put("url", RequestBody.create(MediaType.parse(MIME_TEXT), fileModel.getName()));
        params.put("id_file_parent", RequestBody.create(MediaType.parse(MIME_TEXT), "" + idFileParent));
        params.put("directory", RequestBody.create(MediaType.parse(MIME_TEXT), "false"));

        final Call<FilesResponse> call = mFileOnlineApi.uploadFile(params);
        //new FileUploadTypedFile("*/*", fileModel, this),
        //fileModel.getName(),
        //"" + idFileParent,
        //"false");
        call.enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                if (response.isSuccessful() && listener != null) {
                    listener.execute();
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {

            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rename(final FileModel fileModel, final String newName, final IListener listener) {
        if (fileModel.isOnline()) {
            final Call<FilesResponse> call = mFileOnlineApi.rename(fileModel.getId(), newName);
            call.enqueue(new Callback<FilesResponse>() {
                @Override
                public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                    if (response.isSuccessful()) {
                        final FilesResponse filesResponse = response.body();
                        filesResponse.getResult(mContextApp);
                        listener.execute();
                    }
                }

                @Override
                public void onFailure(Call<FilesResponse> call, Throwable t) {

                }
            });
        } else {
            final File file = fileModel.getFile();
            if (file == null) {
                Toast.makeText(mContextApp, FAILED_FILE_IS_NULL, Toast.LENGTH_SHORT).show();
                return;
            }
            final File parent = file.getParentFile();
            if (parent != null) {
                //noinspection ResultOfMethodCallIgnored
                file.renameTo(new File(parent.getAbsolutePath(), newName));
            }
            listener.execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renameLocalByPath(final FileModel fileModel, final String path) {
        Preconditions.checkNotNull(fileModel);
        Preconditions.checkNotNull(path);
        //noinspection ResultOfMethodCallIgnored
        final File file = fileModel.getFile();
        if (file == null) {
            Toast.makeText(mContextApp, FAILED_FILE_IS_NULL, Toast.LENGTH_SHORT).show();
            return;
        }
        //noinspection ResultOfMethodCallIgnored
        file.renameTo(new File(path));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final FileModel fileModel, final IListener listener) {
        Preconditions.checkNotNull(fileModel);
        if (fileModel.isOnline()) {
            final Call<FilesResponse> call = mFileOnlineApi.delete(fileModel.getId(), "");
            call.enqueue(new Callback<FilesResponse>() {
                @Override
                public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                    if (response.isSuccessful()) {
                        final FilesResponse filesResponse = response.body();
                        filesResponse.getResult(mContextApp);
                        listener.execute();
                    }
                }

                @Override
                public void onFailure(Call<FilesResponse> call, Throwable t) {

                }
            });
        } else {
            final File file = fileModel.getFile();
            if (file == null) {
                Toast.makeText(mContextApp, FAILED_FILE_IS_NULL, Toast.LENGTH_SHORT).show();
                listener.execute();
                return;
            }
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            FileApp.get().getFileAppComponent().provideFileProviderManager().load();
            listener.execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(final FileModel fileModel, final int newIdFileParent, final IListener listener) {
        Preconditions.checkNotNull(fileModel);
        if (!fileModel.isOnline()) {
            return;
        }
        final Call<FilesResponse> call = mFileOnlineApi.setParent(fileModel.getId(), "" + newIdFileParent);
        call.enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                if (response.isSuccessful()) {
                    final FilesResponse filesResponse = response.body();
                    filesResponse.getResult(mContextApp);
                    listener.execute();
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {

            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPublic(final FileModel fileModel, final boolean isPublic, final IListener listener) {
        if (!fileModel.isOnline()) {
            return;
        }
        final Call<FilesResponse> call = mFileOnlineApi.setPublic(fileModel.getId(), "" + isPublic);
        call.enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                if (response.isSuccessful()) {
                    final FilesResponse filesResponse = response.body();
                    filesResponse.getResult(mContextApp);
                    listener.execute();
                }
            }

            @Override
            public void onFailure(Call<FilesResponse> call, Throwable t) {

            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Activity activity, final int position, final List fileModelList, View view) {
        if (fileModelList == null || position >= fileModelList.size()) {
            return;
        }
        final FileModel fileModel = (FileModel) fileModelList.get(position);
        if (fileModel.isOnline()) {
            executeOnline(activity, position, fileModelList, view);
        } else {
            executeLocal(activity, position, fileModelList, view);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openLocalAs(final Activity activity, final FileModel fileModel) {
        if (fileModel.isOnline()) {
            return;
        }
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(activity);
        final String[] menuList = {
                activity.getString(R.string.text),
                activity.getString(R.string.image),
                activity.getString(R.string.audio),
                activity.getString(R.string.video),
                activity.getString(R.string.other)};
        menuAlert.setTitle(activity.getString(R.string.open_as));

        menuAlert.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String type_mime = "*/*";
                        switch (item) {
                            case 0:
                                type_mime = MIME_TEXT;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Spanned toSpanned(final Context context, final FileModel fileModel) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(fileModel);

        final FileTypeModel type = fileModel.getType();
        final boolean isDirectory = fileModel.isDirectory();
        final long size = fileModel.getSize();
        final boolean isPublic = fileModel.isPublic();
        final Date dateCreation = fileModel.getDateCreation();

        final List<StringPair> spl = new ArrayList<>();
        spl.add(new StringPair("Name", fileModel.getName()));
        if (!fileModel.isDirectory()) {
            spl.add(new StringPair("Extension", type.toString()));
        }
        spl.add(new StringPair("Type", type.getTitle(context)));
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
        if (fileModel.isOnline()) {
            spl.add(new StringPair("Visibility", isPublic ? "Public" : "Private"));
        }
        spl.add(new StringPair("Path", fileModel.getUrl()));
        return HtmlUtils.createListItem(spl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copyLocalFile(final Activity activity, final FileModel fileModel, final String outputPath) {
        copyLocalFile(activity, fileModel, outputPath, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copyLocalFile(
            final Activity activity,
            final FileModel fileModel,
            final String outputPath,
            final IPostExecuteListener listener) {

        if (fileModel.isOnline()) {
            //TODO copy online
            Toast.makeText(activity, activity.getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
        } else {
            InputStream in;
            OutputStream out;
            try {
                final File dir = new File(outputPath);
                if (!dir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdirs();
                }

                String outputUrl = outputPath + fileModel.getFullName();
                while ((new File(outputUrl)).exists()) {
                    outputUrl = outputPath + fileModel.getCopyName();
                }

                if (fileModel.isDirectory()) {
                    final File copy = new File(outputUrl);
                    //noinspection ResultOfMethodCallIgnored
                    copy.mkdirs();
                    final File[] children = fileModel.getFile().listFiles();
                    for (File aChildren : children) {
                        copyLocalFile(activity, new FileModel.FileModelBuilder().file(aChildren).build(),
                                copy.getAbsolutePath() + File.separator);
                    }
                } else {
                    in = new FileInputStream(fileModel.getFile().getAbsoluteFile());
                    out = new FileOutputStream(outputUrl);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
        if (listener != null) {
            listener.onPostExecute(null, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMine(final FileModel fileModel) {
        return !fileModel.isOnline() || fileModel.getIdUser() == Config.getUserId();
    }

    @Override
    @SuppressLint("NewApi")
    public void searchLocal(
            final Context context,
            final String search,
            final ResultCallback<List<FileModel>> resultCallback) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            resultCallback.failure();
            return;
        }

        new AsyncTask<Void, Void, List<FileModel>>() {
            @Override
            protected List<FileModel> doInBackground(Void... params) {
                final String[] PROJECTION = new String[]{MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                final String selection = MediaStore.Files.FileColumns.DISPLAY_NAME + LIKE;
                searchArray.add("%" + search + "%");

                final List<FileModel> result = new ArrayList<>();

                final Cursor cursor = context.getContentResolver().query(allSongsUri, PROJECTION, selection,
                        searchArray.toArray(new String[searchArray.size()]), null);

                if (cursor == null) {
                    return result;
                }
                if (cursor.moveToFirst()) {
                    do {
                        result.add(new FileModel.FileModelBuilder()
                                .file(new File(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))))
                                .build());
                    } while (cursor.moveToNext());
                }
                cursor.close();

                return result;
            }

            @Override
            protected void onPostExecute(List<FileModel> fileModels) {
                resultCallback.success(fileModels);
                super.onPostExecute(fileModels);
            }
        }.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getCover(
            final Context context,
            final FileAudioModel fileAudioModel,
            final ImageView imageView) {

        if (!StringUtils.isNullOrEmpty(fileAudioModel.getAlbum())) {
            CoverUtils.getCoverUrl(context, fileAudioModel, new CoverUtils.CoverResponse() {
                @Override
                public void onCoverUrlResult(FileAudioModel fileAudioModel, String url) {
                    Picasso.with(context)
                            .load(url)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(imageView);
                }
            });
        }
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void onFileUploadProgress(final FileModel fileModel, long progress, long length) {
//        if (mNotificationBuilder == null) {
//            mNotificationBuilder = new NotificationCompat.Builder(mContextApp);
//            mNotificationBuilder.setContentTitle("Upload: " + fileModel.getName())
//                    .setContentText("Upload in progress: " + FileUtils.humanReadableByteCount(progress)
//                            + " / " + FileUtils.humanReadableByteCount(length))
//                    .setSmallIcon(R.drawable.ic_notification);
//        } else {
//            mNotificationBuilder.setContentText("Upload in progress: " +
//                    FileUtils.humanReadableByteCount(progress) + " / " +
//                    FileUtils.humanReadableByteCount(length));
//        }
//        mNotificationBuilder.setProgress((int) (length / 1_000.0f), (int) (progress / 1_000.0f), false);
//
//        if (mNotifyManager == null) {
//            mNotifyManager = (NotificationManager) mContextApp.getSystemService(Context.NOTIFICATION_SERVICE);
//        }
//        mNotifyManager.notify(1, mNotificationBuilder.build());
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void onFileUploadFinished(FileModel fileModel) {
//        mNotificationBuilder.setContentText("Upload complete")
//                // Removes the progress bar
//                .setProgress(0, 0, false);
//        mNotifyManager.notify(1, mNotificationBuilder.build());
//    }

    private void executeOnline(
            final Activity activity,
            final int position,
            final List<FileModel> fileModelList,
            final View view) {
        if (fileModelList == null || position >= fileModelList.size()) {
            return;
        }

        final FileModel fileModel = fileModelList.get(position);
        final String mime = FileTypeModelENUM.openAs(fileModel.getType());
        switch (mime) {
            case FileTypeModelENUM.OPEN_AS_TEXT:
                FileTextActivity.start(activity, fileModel, true);
                break;
            case FileTypeModelENUM.OPEN_AS_AUDIO:
                int musicCurrentPosition = position;
                final List<String> filesPath = new ArrayList<>();
                for (int i = 0; i < fileModelList.size(); i++) {
                    final FileModel f = fileModelList.get(i);
                    if (f.getType() != null && f.getType().equals(FileTypeModelENUM.AUDIO.type) && f.getFile() != null) {
                        filesPath.add(f.getFile().getAbsolutePath());
                    } else if (i < musicCurrentPosition) {
                        musicCurrentPosition--;
                    }
                }
                FileAudioActivity.start(activity, musicCurrentPosition, filesPath, view, true);
                break;
            case FileTypeModelENUM.OPEN_AS_FILESPACE:
                final FileSpaceModel fileSpaceModel = fileModel.getContent();
                if (fileSpaceModel == null) {
                    return;
                }
                if (fileSpaceModel.getTimer().timer_date != null) {
                    Intent intent = new Intent(activity, FileTimerActivity.class);
                    intent.putExtra("URL_FILE", "" + fileModel.getOnlineUrl());
                    intent.putExtra("LOGIN", "" + Config.getUser().getAccessLogin());
                    intent.putExtra("CLOUD", true);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    intent.putExtra("TIMER_DATE", "" + dateFormat.format(fileSpaceModel.getTimer().timer_date));
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else if (!StringUtils.isNullOrEmpty(fileSpaceModel.getArticle().article_content_1)) {
                    FileTextActivity.start(activity, fileModel, true);
                }
                break;
            case FileTypeModelENUM.OPEN_AS_IMAGE:
                if (view == null) {
                    FileImageActivity.startOnlineImage(activity, fileModel);
                } else {
                    FileImageActivity.startOnlineImage(activity, fileModel,
                            view.findViewById(R.id.tab_file_card_icon),
                            view.findViewById(R.id.tab_file_card_title));
                }
                break;
        }
    }

    private void executeLocal(
            final Activity activity,
            final int position,
            final List fileModelList,
            final View view) {
        if (fileModelList == null || position >= fileModelList.size()) {
            return;
        }
        final FileModel fileModel = (FileModel) fileModelList.get(position);
        if (fileModel.isOnline()) {
            return;
        }

        final String mime = FileTypeModelENUM.openAs(fileModel.getType());
        switch (mime) {
            case FileTypeModelENUM.NOT_OPEN:
                break;
            case FileTypeModelENUM.OPEN_AS_AUDIO:
                int musicCurrentPosition = position;
                final List<String> filesPath = new ArrayList<>();
                for (int i = 0; i < fileModelList.size(); i++) {
                    final FileModel f = (FileModel) fileModelList.get(i);
                    if (f.getType() != null && f.getType().equals(FileTypeModelENUM.AUDIO.type) && f.getFile() != null) {
                        filesPath.add(f.getFile().getAbsolutePath());
                    } else if (i < position) {
                        musicCurrentPosition--;
                    }
                }
                FileAudioActivity.start(activity, musicCurrentPosition, filesPath, view, false);
                break;
            default:
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(fileModel.getFile()), mime);
                try {
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(activity, "Oops, there is an error. Try with \"Open as...\"", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void notifyGetFilesSucceeded(
            final List<FileModel> fileModels,
            final ResultCallback<List<FileModel>> resultCallback) {
        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyGetFilesSucceeded(fileModels, resultCallback);
                }
            });
            return;
        }
        resultCallback.success(fileModels);
    }
}
