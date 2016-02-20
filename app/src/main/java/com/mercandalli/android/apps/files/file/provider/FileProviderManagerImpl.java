package com.mercandalli.android.apps.files.file.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class FileProviderManagerImpl extends FileProviderManager {

    private static final String LIKE = " LIKE ?";

    private final Handler mUiHandler;
    private final Thread mUiThread;
    private final Context mContextApp;

    private final List<String> mFilePaths;
    private final List<String> mFileAudioPaths;
    private final List<String> mFileImagePaths;

    private boolean mIsLoadLaunched = false;

    /**
     * The manager constructor.
     *
     * @param contextApp The {@link Context} of this application.
     */
    public FileProviderManagerImpl(final Context contextApp) {
        Preconditions.checkNotNull(contextApp);
        mContextApp = contextApp;

        final Looper mainLooper = Looper.getMainLooper();
        mUiHandler = new Handler(mainLooper);
        mUiThread = mainLooper.getThread();
        mFilePaths = new ArrayList<>();
        mFileAudioPaths = new ArrayList<>();
        mFileImagePaths = new ArrayList<>();
    }

    @SuppressLint("NewApi")
    public void load() {
        load(null);
    }

    @SuppressLint("NewApi")
    public void load(@Nullable final FileProviderListener fileProviderListener) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyLoadFailed(fileProviderListener, LOADING_ERROR_ANDROID_API);
            return;
        }

        if (mIsLoadLaunched) {
            notifyLoadFailed(fileProviderListener, LOADING_ERROR_ALREADY_LAUNCHED);
            return;
        }
        mIsLoadLaunched = true;

        new Thread() {
            @Override
            public void run() {
                final List<String> filePaths = new ArrayList<>();
                final List<String> fileAudioPaths = new ArrayList<>();
                final List<String> fileImagePaths = new ArrayList<>();

                final String[] PROJECTION = {MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                final String mediaTypeKey = MediaStore.Files.FileColumns.MEDIA_TYPE;
                final StringBuilder selection = new StringBuilder("( " + mediaTypeKey + " = " +
                        MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + " OR " + mediaTypeKey + " = " +
                        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection.append(" OR " + MediaStore.Files.FileColumns.DATA + LIKE);
                    searchArray.add('%' + end);
                }
                for (String end : FileTypeModelENUM.IMAGE.type.getExtensions()) {
                    selection.append(" OR " + MediaStore.Files.FileColumns.DATA + LIKE);
                    searchArray.add('%' + end);
                }
                selection.append(" )");

                final Cursor cursor = mContextApp.getContentResolver().query(
                        allSongsUri,
                        PROJECTION,
                        selection.toString(),
                        searchArray.toArray(new String[searchArray.size()]),
                        null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                            final String pathLower = path.toLowerCase();
                            for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                                if (pathLower.endsWith(end)) {
                                    fileAudioPaths.add(path);
                                }
                            }
                            for (String end : FileTypeModelENUM.IMAGE.type.getExtensions()) {
                                if (pathLower.endsWith(end)) {
                                    fileImagePaths.add(path);
                                }
                            }

                            filePaths.add(path);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                notifyLoadSucceeded(filePaths, fileAudioPaths, fileImagePaths, fileProviderListener);
            }
        }.start();
    }

    @NonNull
    public List<String> getFilePaths() {
        return new ArrayList<>(mFilePaths);
    }

    @NonNull
    public List<String> getFileAudioPaths() {
        return new ArrayList<>(mFileAudioPaths);
    }

    @NonNull
    public List<String> getFileImagePaths() {
        return new ArrayList<>(mFileImagePaths);
    }

    private void notifyLoadSucceeded(
            final List<String> filePaths,
            final List<String> fileAudioPaths,
            final List<String> fileImagePaths,
            @Nullable final FileProviderListener fileProviderListener) {

        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyLoadSucceeded(filePaths, fileAudioPaths, fileImagePaths, fileProviderListener);
                }
            });
            return;
        }
        mFilePaths.clear();
        mFilePaths.addAll(filePaths);
        mFileAudioPaths.clear();
        mFileAudioPaths.addAll(fileAudioPaths);
        mFileImagePaths.clear();
        mFileImagePaths.addAll(fileImagePaths);

        if (fileProviderListener != null) {
            fileProviderListener.onFileProviderAllBasicLoaded(filePaths);
            fileProviderListener.onFileProviderAudioLoaded(fileAudioPaths);
            fileProviderListener.onFileProviderImageLoaded(fileImagePaths);
        }

        synchronized (mFileProviderListeners) {
            for (int i = 0, size = mFileProviderListeners.size(); i < size; i++) {
                mFileProviderListeners.get(i).onFileProviderAllBasicLoaded(filePaths);
                mFileProviderListeners.get(i).onFileProviderAudioLoaded(fileAudioPaths);
                mFileProviderListeners.get(i).onFileProviderImageLoaded(fileImagePaths);
            }
        }
        mIsLoadLaunched = false;
    }

    private void notifyLoadFailed(
            @Nullable final FileProviderListener fileProviderListener,
            @LoadingError final int error) {
        if (fileProviderListener != null) {
            fileProviderListener.onFileProviderFailed(error);
        }
        synchronized (mFileProviderListeners) {
            for (int i = 0, size = mFileProviderListeners.size(); i < size; i++) {
                mFileProviderListeners.get(i).onFileProviderFailed(error);
            }
        }
    }
}
