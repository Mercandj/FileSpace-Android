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
import com.mercandalli.android.apps.files.common.Preconditions;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.common.net.TaskGetDownload;
import com.mercandalli.android.apps.files.common.util.FileUtils;
import com.mercandalli.android.apps.files.common.util.HtmlUtils;
import com.mercandalli.android.apps.files.common.util.NetUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.util.StringUtils;
import com.mercandalli.android.apps.files.common.util.TimeUtils;
import com.mercandalli.android.apps.files.file.audio.CoverUtils;
import com.mercandalli.android.apps.files.file.audio.FileAudioActivity;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;
import com.mercandalli.android.apps.files.file.cloud.response.FileResponse;
import com.mercandalli.android.apps.files.file.cloud.response.FilesResponse;
import com.mercandalli.android.apps.files.file.image.FileImageActivity;
import com.mercandalli.android.apps.files.file.local.FileLocalApi;
import com.mercandalli.android.apps.files.file.text.FileTextActivity;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

/**
 * A {@link FileModel} Manager.
 */
public class FileManagerImpl extends FileManager implements FileUploadTypedFile.FileUploadListener {

    private Context mContextApp;
    private FileOnlineApi mFileOnlineApi;
    private FileLocalApi mFileLocalApi;
    private FilePersistenceApi mFilePersistenceApi;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mNotificationBuilder;

    public FileManagerImpl(Context contextApp, FileOnlineApi fileOnlineApi, FileLocalApi fileLocalApi, FilePersistenceApi filePersistenceApi) {
        Preconditions.checkNotNull(contextApp);

        mContextApp = contextApp;
        mFileOnlineApi = fileOnlineApi;
        mFileLocalApi = fileLocalApi;
        mFilePersistenceApi = filePersistenceApi;
    }

    @Override
    public void getFiles(final FileModel fileParent, final int sortMode, final ResultCallback<List<FileModel>> resultCallback) {
        getFiles(fileParent, true, null, sortMode, resultCallback);
    }

    @Override
    public void getFiles(final FileModel fileParent, boolean areMyFiles, final String search, final int sortMode, final ResultCallback<List<FileModel>> resultCallback) {
        if (fileParent.isOnline()) {
            mFileOnlineApi.getFiles(fileParent.getId(), areMyFiles ? "" : "true", StringUtils.toEmptyIfNull(search), new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    List<FileResponse> result = filesResponse.getResult(mContextApp);
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

    @Override
    public void download(final Activity activity, final FileModel fileModel, final IListener listener) {
        if (NetUtils.isInternetConnection(mContextApp) && fileModel.isOnline()) {
            if (fileModel.isDirectory()) {
                Toast.makeText(mContextApp, "Directory download not supported yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            String url = Constants.URL_DOMAIN_API + "/" + Config.routeFile + "/" + fileModel.getId();
            String pathFolderDownloaded = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Config.localFolderNameDefault;
            final File folder = new File(pathFolderDownloaded);
            if (!folder.exists()) {
                folder.mkdir();
            }
            String pathFileDownloaded = pathFolderDownloaded + File.separator + fileModel.getFullName();
            new TaskGetDownload(activity, url, pathFileDownloaded, fileModel, listener).execute();
        }
    }

    @Override
    public void upload(final FileModel fileModel, int idFileParent, final IListener listener) {
        if (NetUtils.isInternetConnection(mContextApp) && !fileModel.isOnline()) {
            if (fileModel.isDirectory()) {
                Toast.makeText(mContextApp, "Directory download not supported yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            mFileOnlineApi.uploadFile(
                    new FileUploadTypedFile("*/*", fileModel, this),
                    new TypedString(fileModel.getName()),
                    new TypedString("" + idFileParent),
                    new TypedString("false"),
                    new Callback<FilesResponse>() {
                        @Override
                        public void success(FilesResponse filesResponse, Response response) {
                            listener.execute();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
        }
    }

    @Override
    public void rename(final FileModel fileModel, final String newName, final IListener listener) {
        if (fileModel.isOnline()) {
            mFileOnlineApi.rename(fileModel.getId(), new TypedString(newName), new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    filesResponse.getResult(mContextApp);
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

    @Override
    public void renameLocalByPath(FileModel fileModel, String path) {
        File tmp = new File(path);
        fileModel.getFile().renameTo(tmp);
    }

    @Override
    public void delete(final FileModel fileModel, final IListener listener) {
        Preconditions.checkNotNull(fileModel);
        if (fileModel.isOnline()) {
            mFileOnlineApi.delete(fileModel.getId(), "", new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    filesResponse.getResult(mContextApp);
                    listener.execute();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else {
            if (fileModel.getFile().isDirectory()) {
                FileUtils.deleteDirectory(fileModel.getFile());
            } else {
                fileModel.getFile().delete();
            }
            listener.execute();
        }
    }

    @Override
    public void setParent(final FileModel fileModel, final int newIdFileParent, final IListener listener) {
        Preconditions.checkNotNull(fileModel);
        if (fileModel.isOnline()) {
            mFileOnlineApi.setParent(fileModel.getId(), new TypedString("" + newIdFileParent), new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    filesResponse.getResult(mContextApp);
                    listener.execute();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    @Override
    public void setPublic(final FileModel fileModel, final boolean isPublic, final IListener listener) {
        if (fileModel.isOnline()) {
            mFileOnlineApi.setPublic(fileModel.getId(), new TypedString("" + isPublic), new Callback<FilesResponse>() {
                @Override
                public void success(FilesResponse filesResponse, Response response) {
                    filesResponse.getResult(mContextApp);
                    listener.execute();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

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

    private void executeOnline(final Activity activity, final int position, final List<FileModel> fileModelList, View view) {
        if (fileModelList == null || position >= fileModelList.size()) {
            return;
        }
        final FileModel fileModel = fileModelList.get(position);
        if (fileModel.getType().equals(FileTypeModelENUM.TEXT.type)) {
            FileTextActivity.start(activity, fileModel, true);
        } else if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
            Intent intent = new Intent(activity, FileImageActivity.class);
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
        } else if (fileModel.getType().equals(FileTypeModelENUM.AUDIO.type)) {
            Intent intent = new Intent(activity, FileAudioActivity.class);
            intent.putExtra("CLOUD", true);
            intent.putExtra("FILE", fileModel);
            ArrayList<FileModel> tmpFiles = new ArrayList<>();
            for (FileModel f : fileModelList) {
                if (f.getType().equals(FileTypeModelENUM.AUDIO.type)) {
                    tmpFiles.add(f);
                }
            }
            intent.putParcelableArrayListExtra("FILES", tmpFiles);
            if (view == null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                Pair<View, String> p1 = Pair.create(view.findViewById(R.id.tab_icon), "transitionIcon");
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
                    intent.putExtra("CLOUD", true);
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

    private void executeLocal(final Activity activity, final int position, final List fileModelList, View view) {
        if (fileModelList == null || position >= fileModelList.size()) {
            return;
        }
        final FileModel fileModel = (FileModel) fileModelList.get(position);
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

            int musicCurrentPosition = position;
            List<String> filesPath = new ArrayList<>();
            for (int i = 0; i < fileModelList.size(); i++) {
                final FileModel f = (FileModel) fileModelList.get(i);
                if (f.getType() != null && f.getType().equals(FileTypeModelENUM.AUDIO.type) && f.getFile() != null) {
                    filesPath.add(f.getFile().getAbsolutePath());
                } else if (i < musicCurrentPosition) {
                    musicCurrentPosition--;
                }
            }
            FileAudioActivity.startLocal(activity, musicCurrentPosition, filesPath, view);


            /*
            Intent intent = new Intent(activity, FileAudioActivity.class);
            intent.putExtra("CLOUD", false);
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
            */
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

    @Override
    public void openLocalAs(final Activity activity, final FileModel fileModel) {
        if (fileModel.isOnline()) {
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

    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void copyLocalFile(final Activity activity, final FileModel fileModel, final String outputPath) {
        copyLocalFile(activity, fileModel, outputPath, null);
    }

    @Override
    public void copyLocalFile(final Activity activity, final FileModel fileModel, String outputPath, IPostExecuteListener listener) {
        if (fileModel.isOnline()) {
            //TODO copy online
            Toast.makeText(activity, activity.getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
        } else {
            InputStream in;
            OutputStream out;
            try {
                File dir = new File(outputPath);
                if (!dir.exists())
                    dir.mkdirs();

                String outputUrl = outputPath + fileModel.getFullName();
                while ((new File(outputUrl)).exists()) {
                    outputUrl = outputPath + fileModel.getCopyName();
                }

                if (fileModel.isDirectory()) {
                    File copy = new File(outputUrl);
                    copy.mkdirs();
                    File[] children = fileModel.getFile().listFiles();
                    for (File aChildren : children) {
                        copyLocalFile(activity, new FileModel.FileModelBuilder().file(aChildren).build(), copy.getAbsolutePath() + File.separator);
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

    @Override
    public boolean isMine(final FileModel fileModel) {
        return !fileModel.isOnline() || fileModel.getIdUser() == Config.getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getLocalMusic(final Context context, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback) {

        final List<FileAudioModel> files = new ArrayList<>();

        final String[] STAR = {"*"};

        final Uri allsongsuri = MediaStore.Files.getContentUri("external");
        final List<String> searchArray = new ArrayList<>();

        String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

        for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
            selection += " OR " + MediaStore.Files.FileColumns.DATA + " LIKE ?";
            searchArray.add("%" + end + "%");
        }
        selection += " )";

        if (search != null && !search.isEmpty()) {
            searchArray.add("%" + search + "%");
            selection += " AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?";
        }

        Cursor cursor = context.getContentResolver().query(allsongsuri, STAR, selection, searchArray.toArray(new String[searchArray.size()]), null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String song_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
                    int song_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));

                    String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));

                    final File file = new File(fullpath);
                    if (file.exists() && !file.isDirectory()) {

                        FileAudioModel.FileMusicModelBuilder fileMusicModelBuilder = new FileAudioModel.FileMusicModelBuilder()
                                .file(new File(fullpath));

                        try {
                            MusicMetadataSet src_set = new MyID3().read(file);
                            try {
                                IMusicMetadata metadata = src_set.getSimplified();
                                fileMusicModelBuilder.album(metadata.getAlbum());
                                fileMusicModelBuilder.artist(metadata.getArtist());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } // read metadata

                        //if (mSortMode == Constants.SORT_SIZE)
                        //    fileMusicModel.adapterTitleStart = FileUtils.humanReadableByteCount(fileMusicModel.getSize()) + " - ";

                        files.add(fileMusicModelBuilder.build());
                    }

                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        if (sortMode == Constants.SORT_ABC) {
            Collections.sort(files, new Comparator<FileAudioModel>() {
                @Override
                public int compare(final FileAudioModel f1, final FileAudioModel f2) {
                    if (f1.getName() == null || f2.getName() == null) {
                        return 0;
                    }
                    return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
                }
            });
        } else if (sortMode == Constants.SORT_SIZE) {
            Collections.sort(files, new Comparator<FileAudioModel>() {
                @Override
                public int compare(final FileAudioModel f1, final FileAudioModel f2) {
                    return (new Long(f2.getSize())).compareTo(f1.getSize());
                }
            });
        } else {
            final Map<FileModel, Long> staticLastModifiedTimes = new HashMap<>();
            for (FileModel f : files) {
                staticLastModifiedTimes.put(f, f.getLastModified());
            }
            Collections.sort(files, new Comparator<FileAudioModel>() {
                @Override
                public int compare(final FileAudioModel f1, final FileAudioModel f2) {
                    return staticLastModifiedTimes.get(f2).compareTo(staticLastModifiedTimes.get(f1));
                }
            });
        }

        resultCallback.success(files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getLocalMusic(final Context context, final FileModel fileModelDirectParent, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback) {
        Preconditions.checkNotNull(fileModelDirectParent);
        Preconditions.checkNotNull(resultCallback);
        if (!fileModelDirectParent.isDirectory()) {
            resultCallback.failure();
            return;
        }
        final List<FileAudioModel> files = new ArrayList<>();
        List<File> fs = Arrays.asList(fileModelDirectParent.getFile().listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return (new FileTypeModel(FileUtils.getExtensionFromPath(name))).equals(FileTypeModelENUM.AUDIO.type);
                    }
                }
        ));
        for (File file : fs) {
            final FileAudioModel.FileMusicModelBuilder fileMusicModelBuilder = new FileAudioModel.FileMusicModelBuilder()
                    .file(file);
            try {
                MusicMetadataSet src_set = new MyID3().read(file);
                try {
                    IMusicMetadata metadata = src_set.getSimplified();
                    fileMusicModelBuilder.album(metadata.getAlbum());
                    fileMusicModelBuilder.artist(metadata.getArtist());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } // read metadata
            files.add(fileMusicModelBuilder.build());
        }
        resultCallback.success(files);
    }


    @Override
    public void getLocalMusicFolder(Context context, int sortMode, String search, final ResultCallback<List<FileModel>> resultCallback) {
        getLocalMusic(context, sortMode, search, new ResultCallback<List<FileAudioModel>>() {
            @Override
            public void success(final List<FileAudioModel> result) {
                final Map<String, FileModel> directories = new HashMap<>();
                for (FileAudioModel fileAudioModel : result) {
                    File parent = fileAudioModel.getFile().getParentFile();
                    FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
                    fileModelBuilder.file(parent);
                    directories.put(parent.getPath(), fileModelBuilder.build());
                }

                resultCallback.success(new ArrayList<>(directories.values()));
            }

            @Override
            public void failure() {
                resultCallback.failure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getCover(final Context context, final FileAudioModel fileAudioModel, final ImageView imageView) {

        if (!StringUtils.isNullOrEmpty(fileAudioModel.getAlbum())) {
            CoverUtils.getCoverUrl(context, fileAudioModel.getAlbum(), new ResultCallback<String>() {
                @Override
                public void success(String result) {
                    Picasso.with(context)
                            .load(result)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(imageView);
                }

                @Override
                public void failure() {

                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFileUploadProgress(final FileModel fileModel, long progress, long length) {
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new NotificationCompat.Builder(this.mContextApp);
            mNotificationBuilder.setContentTitle("Upload: " + fileModel.getName())
                    .setContentText("Upload in progress: " + FileUtils.humanReadableByteCount(progress) + " / " + FileUtils.humanReadableByteCount(length))
                    .setSmallIcon(R.drawable.ic_notification);
        } else {
            mNotificationBuilder.setContentText("Upload in progress: " + FileUtils.humanReadableByteCount(progress) + " / " + FileUtils.humanReadableByteCount(length));
        }
        mNotificationBuilder.setProgress((int) (length / 1000.0f), (int) (progress / 1000.0f), false);

        if (mNotifyManager == null) {
            mNotifyManager = (NotificationManager) this.mContextApp.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotifyManager.notify(1, mNotificationBuilder.build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFileUploadFinished(FileModel fileModel) {
        mNotificationBuilder.setContentText("Upload complete")
                // Removes the progress bar
                .setProgress(0, 0, false);
        mNotifyManager.notify(1, mNotificationBuilder.build());
    }
}
