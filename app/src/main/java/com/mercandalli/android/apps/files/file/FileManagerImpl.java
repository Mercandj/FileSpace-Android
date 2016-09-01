package com.mercandalli.android.apps.files.file;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IListenerUtils;
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
import com.mercandalli.android.apps.files.file.cloud.FileUploadOnlineApi;
import com.mercandalli.android.apps.files.file.cloud.ProgressRequestBody;
import com.mercandalli.android.apps.files.file.cloud.response.FileResponse;
import com.mercandalli.android.apps.files.file.cloud.response.FilesResponse;
import com.mercandalli.android.apps.files.file.filespace.FileSpaceModel;
import com.mercandalli.android.apps.files.file.filespace.FileTimerActivity;
import com.mercandalli.android.apps.files.file.image.FileImageActivity;
import com.mercandalli.android.apps.files.file.local.FileLocalApi;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;
import com.mercandalli.android.apps.files.file.text.FileTextActivity;
import com.mercandalli.android.apps.files.file.video.FileVideoActivity;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.main.network.RetrofitUtils;
import com.mercandalli.android.apps.files.settings.SettingsManager;
import com.mercandalli.android.library.base.java.StringUtils;
import com.mercandalli.android.library.base.precondition.Preconditions;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION_CODES.N;

/**
 * A {@link FileModel} Manager.
 */
/* package */
class FileManagerImpl extends FileManager implements ProgressRequestBody.UploadCallbacks /*implements FileUploadTypedFile.FileUploadListener*/ {

    private static final String LIKE = " LIKE ?";
    private static final String MIME_TEXT = "text/plain";
    private static final String FAILED_FILE_IS_NULL = "Failed: File is null.";

    @NonNull
    private final Context mContextApp;
    @NonNull
    private final FileOnlineApi mFileOnlineApi;
    @NonNull
    private final FileLocalApi mFileLocalApi;
    @NonNull
    private final FileUploadOnlineApi mFileUploadOnlineApi;
    @NonNull
    private final Handler mUiHandler;
    @NonNull
    private final Thread mUiThread;

    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;

    private final FileLocalProviderManager mFileLocalProviderManager;

    protected FileManagerImpl(@NonNull final Context context) {
        Preconditions.checkNotNull(context);

        mContextApp = context.getApplicationContext();
        mFileOnlineApi = RetrofitUtils.getAuthorizedRetrofit().create(FileOnlineApi.class);
        mFileUploadOnlineApi = RetrofitUtils.getAuthorizedRetrofitUpload().create(FileUploadOnlineApi.class);
        mFileLocalApi = FileLocalApi.getInstance();

        mFileLocalProviderManager = FileLocalProviderManager.getInstance(context);

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
            final boolean isSuperUser,
            final ResultCallback<List<FileModel>> resultCallback) {
        new Thread() {
            @Override
            public void run() {
                final List<FileModel> files = mFileLocalApi.getFiles(fileParent);
                if (!isSuperUser || !files.isEmpty()) {
                    notifyGetFilesSucceeded(files, resultCallback);
                } else {
                    mFileLocalApi.getFilesSuperUser(fileParent, new FileLocalApi.GetFilesSuperUser() {
                        @Override
                        public void onGetFilesSuperUser(final List<FileModel> files) {
                            notifyGetFilesSucceeded(files, resultCallback);
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFiles(
            final FileModel fileParent,
            final boolean isSuperUser,
            final ResultCallback<List<FileModel>> resultCallback) {
        getFiles(fileParent, isSuperUser, true, resultCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFiles(
            final FileModel fileParent,
            final boolean isSuperUser,
            final boolean areMyFiles,
            final ResultCallback<List<FileModel>> resultCallback) {

        if (!fileParent.isOnline()) {
            new Thread() {
                @Override
                public void run() {
                    final File fileParentFile = fileParent.getFile();
                    final List<FileModel> files = mFileLocalApi.getFiles(fileParentFile);
                    if (!isSuperUser || !files.isEmpty()) {
                        notifyGetFilesSucceeded(files, resultCallback);
                    } else {
                        mFileLocalApi.getFilesSuperUser(fileParentFile, new FileLocalApi.GetFilesSuperUser() {
                            @Override
                            public void onGetFilesSuperUser(final List<FileModel> files) {
                                notifyGetFilesSucceeded(files, resultCallback);
                            }
                        });
                    }
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

        final Map<String, ProgressRequestBody> params = new HashMap<>();

        final String filename = file.getName();
        ProgressRequestBody photo = ProgressRequestBody.create(MediaType.parse("multipart/form-data"), file, this);
        params.put("file\"; filename=\"" + filename, photo);

        params.put("url", ProgressRequestBody.create(MediaType.parse(MIME_TEXT), fileModel.getName()));
        params.put("id_file_parent", ProgressRequestBody.create(MediaType.parse(MIME_TEXT), "" + idFileParent));
        params.put("directory", ProgressRequestBody.create(MediaType.parse(MIME_TEXT), "false"));

        final Call<FilesResponse> call = mFileUploadOnlineApi.uploadFile(params);
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
            final WeakReference<IListener> weakReference = new WeakReference<>(listener);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                    } else {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                    FileLocalProviderManager.getInstance(mContextApp).load();
                    final IListener iListener = weakReference.get();
                    if (iListener != null) {
                        IListenerUtils.executeOnUiThread(iListener);
                    }
                }
            }).start();
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
    public void execute(
            @NonNull final Activity activity,
            final int position,
            @NonNull final List<FileModel> fileModelList,
            final View view) {
        if (position >= fileModelList.size()) {
            return;
        }
        final FileModel fileModel = fileModelList.get(position);
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
        final File file = fileModel.getFile();
        if (fileModel.isOnline() || file == null) {
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
                        final String typeMime;
                        switch (item) {
                            case 0:
                                typeMime = MIME_TEXT;
                                break;
                            case 1:
                                typeMime = "image/*";
                                break;
                            case 2:
                                typeMime = "audio/*";
                                break;
                            case 3:
                                typeMime = "video/*";
                                break;
                            default:
                                typeMime = "*/*";
                        }
                        final Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(getUriFromFile(activity, file), typeMime);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(activity, intent);
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

        final List<StringPair> stringPairs = new ArrayList<>();
        stringPairs.add(new StringPair("Name", fileModel.getName()));
        if (type != null) {
            if (!fileModel.isDirectory()) {
                stringPairs.add(new StringPair("Extension", type.toString()));
            }
            stringPairs.add(new StringPair("Type", type.getTitle(context)));
        }
        if (fileModel instanceof FileAudioModel) {
            final FileAudioModel fileAudioModel = (FileAudioModel) fileModel;
            final String title = fileAudioModel.getTitle();
            final String artist = fileAudioModel.getArtist();
            final String album = fileAudioModel.getAlbum();
            if (title != null) {
                stringPairs.add(new StringPair("Audio title", title));
            }
            if (artist != null) {
                stringPairs.add(new StringPair("Audio artist", artist));
            }
            if (album != null) {
                stringPairs.add(new StringPair("Audio album", album));
            }
        }

        if (!isDirectory || size != 0) {
            stringPairs.add(new StringPair("Size", FileUtils.humanReadableByteCount(size)));
        }
        if (dateCreation != null) {
            if (fileModel.isOnline()) {
                stringPairs.add(new StringPair("Upload date", TimeUtils.getDate(dateCreation)));
            } else {
                stringPairs.add(new StringPair("Last modification date", TimeUtils.getDate(dateCreation)));
            }
        }
        if (fileModel.isOnline()) {
            stringPairs.add(new StringPair("Visibility", isPublic ? "Public" : "Private"));
        }
        stringPairs.add(new StringPair("Path", fileModel.getUrl()));
        return HtmlUtils.createListItem(stringPairs);
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
                    final InputStream in = new FileInputStream(fileModel.getFile().getAbsoluteFile());
                    final OutputStream out = new FileOutputStream(outputUrl);

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
                final String[] projection = new String[]{MediaStore.Files.FileColumns.DATA};
                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();
                final String selection = MediaStore.Files.FileColumns.DISPLAY_NAME + LIKE;
                searchArray.add("%" + search + "%");

                final Cursor cursor = context.getContentResolver().query(allSongsUri, projection, selection,
                        searchArray.toArray(new String[searchArray.size()]), null);

                final List<String> paths = new ArrayList<>();
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            paths.add(cursor.getString(cursor.getColumnIndex(
                                    MediaStore.Files.FileColumns.DATA)));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                mFileLocalProviderManager.getFilePaths();
                final List<FileModel> result = new ArrayList<>();
                for (final String path : paths) {
                    if (path != null && path.toLowerCase().contains(search.toLowerCase())) {
                        final File file = new File(path);
                        if (file.exists()) {
                            result.add(new FileModel.FileModelBuilder()
                                    .file(file)
                                    .build());
                        }
                    }
                }
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

    private void executeOnline(
            @NonNull final Activity activity,
            final int position,
            @NonNull final List<FileModel> fileModelList,
            final View view) {
        if (position >= fileModelList.size()) {
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
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
            @NonNull final Activity activity,
            final int position,
            @NonNull final List<FileModel> fileModelList,
            final View view) {
        if (position >= fileModelList.size()) {
            return;
        }
        final FileModel fileModel = fileModelList.get(position);
        final File file = fileModel.getFile();
        if (fileModel.isOnline() || file == null) {
            return;
        }

        final String mime = FileTypeModelENUM.openAs(fileModel.getType());

        if (FileTypeModelENUM.OPEN_AS_VIDEO.equals(mime) &&
                SettingsManager.getInstance(activity).isDevUser() &&
                SettingsManager.getInstance(activity).isSuperUser()) {
            FileVideoActivity.startVideo(activity, file);
            return;
        }

        switch (mime) {
            case FileTypeModelENUM.NOT_OPEN:
                break;
            case FileTypeModelENUM.OPEN_AS_AUDIO:
                int musicCurrentPosition = position;
                final List<String> filesPath = new ArrayList<>();
                for (int i = 0; i < fileModelList.size(); i++) {
                    final FileModel f = fileModelList.get(i);
                    if (f.getType() != null && f.getType().equals(FileTypeModelENUM.AUDIO.type) &&
                            f.getFile() != null) {
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
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(getUriFromFile(activity, file), mime);
                startActivity(activity, intent);
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

    @Override
    public List<StringPair> getForUpload(final FileModel fileModel) {
        final List<StringPair> parameters = new ArrayList<>();
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

    @Override
    public void onUploadProgressUpdate(final long progress, final long length) {
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new NotificationCompat.Builder(mContextApp);
            mNotificationBuilder.setContentTitle("Upload: "/* + fileModel.getName()*/)
                    .setContentText("Upload in progress: " + FileUtils.humanReadableByteCount(progress)
                            + " / " + FileUtils.humanReadableByteCount(length))
                    .setSmallIcon(R.drawable.ic_notification_cloud);
        } else {
            mNotificationBuilder.setContentText("Upload in progress: " +
                    FileUtils.humanReadableByteCount(progress) + " / " +
                    FileUtils.humanReadableByteCount(length));
        }
        mNotificationBuilder.setProgress((int) (length / 1_000.0f), (int) (progress / 1_000.0f), false);

        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContextApp.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.notify(1, mNotificationBuilder.build());
    }

    @Override
    public void onUploadError() {

    }

    @Override
    public void onUploadFinish() {
        if (mNotificationBuilder == null || mNotificationManager == null) {
            return;
        }
        mNotificationBuilder.setContentText("Upload complete")
                // Removes the progress bar
                .setProgress(0, 0, false);
        mNotificationManager.notify(1, mNotificationBuilder.build());
    }

    private static void startActivity(
            @NonNull final Activity activity,
            @NonNull final Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= N) {
                startActivityOverN(activity, intent);
            } else {
                activity.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Oops, there is an error. Try with \"Open as...\"",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = N)
    private static void startActivityOverN(
            final @NonNull Activity activity,
            final @NonNull Intent intent) {
        try {
            activity.startActivity(intent);
        } catch (FileUriExposedException e) {
            Toast.makeText(activity, "Oops, there is an error.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static Uri getUriFromFile(
            @NonNull final Context context,
            @NonNull final File file) {
        if (Build.VERSION.SDK_INT >= N) {
            return getUriFromFileOverN(context, file);
        }
        return Uri.fromFile(file);
    }

    private static Uri getUriFromFileOverN(@NonNull final Context context, @NonNull final File file) {
        return FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".provider",
                file);
    }
}
