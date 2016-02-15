package com.mercandalli.android.apps.files.file;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
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
import com.mercandalli.android.apps.files.common.util.StringUtils;
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
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.precondition.Preconditions;
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

    private final Context mContextApp;
    private final FileOnlineApi mFileOnlineApi;
    private final FileLocalApi mFileLocalApi;

    public FileManagerImpl(final Context contextApp, final FileOnlineApi fileOnlineApi) {
        Preconditions.checkNotNull(contextApp);

        mContextApp = contextApp;
        mFileOnlineApi = fileOnlineApi;
        mFileLocalApi = new FileLocalApi();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFiles(
            final FileModel fileParent,
            final int sortMode,
            final ResultCallback<List<FileModel>> resultCallback) {

        getFiles(fileParent, true, null, sortMode, resultCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFiles(
            final FileModel fileParent,
            boolean areMyFiles,
            final String search,
            final int sortMode,
            final ResultCallback<List<FileModel>> resultCallback) {

        if (!fileParent.isOnline()) {
            resultCallback.success(mFileLocalApi.getFiles(fileParent.getFile(), search, sortMode));
            return;
        }
        final Call<FilesResponse> call = mFileOnlineApi.getFiles(fileParent.getId(), areMyFiles ? "" : "true", StringUtils.toEmptyIfNull(search));
        call.enqueue(new Callback<FilesResponse>() {
            @Override
            public void onResponse(Call<FilesResponse> call, Response<FilesResponse> response) {
                if (response.isSuccess()) {
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
                .getAbsolutePath() + File.separator + Config.localFolderNameDefault;
        final File folder = new File(pathFolderDownloaded);
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdir();
        }
        new TaskGetDownload(activity, Constants.URL_DOMAIN_API + Config.routeFile + "/" +
                fileModel.getId(), pathFolderDownloaded + File.separator + fileModel.getFullName(),
                fileModel, listener).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(
            final FileModel fileModel,
            final int idFileParent,
            final IListener listener) {

        if (!NetUtils.isInternetConnection(mContextApp) || fileModel.isOnline()) {
            return;
        }
        if (fileModel.isDirectory()) {
            Toast.makeText(mContextApp, "Directory download not supported yet.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        final Map<String, RequestBody> params = new HashMap<>();

        final File file = fileModel.getFile();
        String filename = file.getName();
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
                if (response.isSuccess()) {
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
                    if (response.isSuccess()) {
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
            File parent = fileModel.getFile().getParentFile();
            if (parent != null) {
                //noinspection ResultOfMethodCallIgnored
                fileModel.getFile().renameTo(new File(parent.getAbsolutePath(), newName));
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
        fileModel.getFile().renameTo(new File(path));
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
                    if (response.isSuccess()) {
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
            if (fileModel.getFile().isDirectory()) {
                FileUtils.deleteDirectory(fileModel.getFile());
            } else {
                //noinspection ResultOfMethodCallIgnored
                fileModel.getFile().delete();
            }
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
                if (response.isSuccess()) {
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
                if (response.isSuccess()) {
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
    public void searchLocal(
            final Context context,
            final String search,
            final ResultCallback<List<FileModel>> resultCallback) {

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

    private void executeOnline(final Activity activity, final int position, final List<FileModel> fileModelList, View view) {
        if (fileModelList == null || position >= fileModelList.size()) {
            return;
        }
        final FileModel fileModel = fileModelList.get(position);
        final FileTypeModel fileTypeModel = fileModel.getType();
        if (FileTypeModelENUM.TEXT.type.equals(fileTypeModel)) {
            FileTextActivity.start(activity, fileModel, true);
        } else if (FileTypeModelENUM.PICTURE.type.equals(fileTypeModel)) {
            final Intent intent = new Intent(activity, FileImageActivity.class);
            intent.putExtra("ID", fileModel.getId());
            intent.putExtra("TITLE", "" + fileModel.getFullName());
            intent.putExtra("URL_FILE", "" + fileModel.getOnlineUrl());
            intent.putExtra("CLOUD", true);
            intent.putExtra("SIZE_FILE", fileModel.getSize());
            intent.putExtra("DATE_FILE", fileModel.getDateCreation());
            if (view == null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.tab_icon), "transitionIcon");
                Pair<View, String> p2 = Pair.create(view.findViewById(R.id.title), "transitionTitle");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, p1, p2);
                activity.startActivity(intent, options.toBundle());
            }
        } else if (FileTypeModelENUM.AUDIO.type.equals(fileTypeModel)) {

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

        } else if (FileTypeModelENUM.FILESPACE.type.equals(fileTypeModel)) {
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
        }
    }

    private void executeLocal(final Activity activity, final int position, final List fileModelList, View view) {
        if (fileModelList == null || position >= fileModelList.size()) {
            return;
        }
        final FileModel fileModel = (FileModel) fileModelList.get(position);
        if (fileModel.isOnline()) {
            return;
        }

        final FileTypeModel fileTypeModel = fileModel.getType();
        if (FileTypeModelENUM.APK.type.equals(fileTypeModel)) {
            final Intent apkIntent = new Intent();
            apkIntent.setAction(Intent.ACTION_VIEW);
            apkIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "application/vnd.android.package-archive");
            activity.startActivity(apkIntent);
        } else if (FileTypeModelENUM.TEXT.type.equals(fileTypeModel)) {
            final Intent txtIntent = new Intent();
            txtIntent.setAction(Intent.ACTION_VIEW);
            txtIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), MIME_TEXT);
            try {
                activity.startActivity(txtIntent);
            } catch (ActivityNotFoundException e) {
                txtIntent.setType("text/*");
                activity.startActivity(txtIntent);
            }
        } else if (FileTypeModelENUM.TEXT.type.equals(fileTypeModel)) {
            final Intent htmlIntent = new Intent();
            htmlIntent.setAction(Intent.ACTION_VIEW);
            htmlIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "text/html");
            try {
                activity.startActivity(htmlIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        } else if (FileTypeModelENUM.AUDIO.type.equals(fileTypeModel)) {
            int musicCurrentPosition = position;
            final List<String> filesPath = new ArrayList<>();
            for (int i = 0; i < fileModelList.size(); i++) {
                final FileModel f = (FileModel) fileModelList.get(i);
                if (f.getType() != null && f.getType().equals(FileTypeModelENUM.AUDIO.type) && f.getFile() != null) {
                    filesPath.add(f.getFile().getAbsolutePath());
                } else if (i < musicCurrentPosition) {
                    musicCurrentPosition--;
                }
            }
            FileAudioActivity.start(activity, musicCurrentPosition, filesPath, view, false);
        } else if (FileTypeModelENUM.PICTURE.type.equals(fileTypeModel)) {
            final Intent picIntent = new Intent();
            picIntent.setAction(Intent.ACTION_VIEW);
            picIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "image/*");
            activity.startActivity(picIntent);
        } else if (FileTypeModelENUM.VIDEO.type.equals(fileTypeModel)) {
            final Intent videoIntent = new Intent();
            videoIntent.setAction(Intent.ACTION_VIEW);
            videoIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "video/*");
            try {
                activity.startActivity(videoIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        } else if (FileTypeModelENUM.PDF.type.equals(fileTypeModel)) {
            final Intent pdfIntent = new Intent();
            pdfIntent.setAction(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(Uri.fromFile(fileModel.getFile()), "application/pdf");
            try {
                activity.startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "ERREUR", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
