package com.mercandalli.android.apps.files.file.local.provider;

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
import android.util.Log;

import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class FileLocalProviderManagerImpl implements FileLocalProviderManager {

    private static final String LIKE = " LIKE ?";
    private static final String TAG = "FileLocalProviderMa";

    private final Handler mUiHandler;
    private final Thread mUiThread;
    private final Context mContextApp;

    private final List<String> mFilePaths;
    private final List<String> mFileAudioPaths;
    private final List<String> mFileImagePaths;
    private final List<GetFileListener> mGetFileListeners;
    private final List<GetFileAudioListener> mGetFileAudioListeners;
    private final List<GetFileImageListener> mGetFileImageListeners;

    private boolean mIsLoadLaunched = false;
    private boolean mIsLoaded = false;

    protected final List<FileProviderListener> mFileProviderListeners = new ArrayList<>();

    /**
     * The manager constructor.
     *
     * @param contextApp The {@link Context} of this application.
     */
    public FileLocalProviderManagerImpl(final Context contextApp) {
        Preconditions.checkNotNull(contextApp);
        mContextApp = contextApp;

        final Looper mainLooper = Looper.getMainLooper();
        mUiHandler = new Handler(mainLooper);
        mUiThread = mainLooper.getThread();
        mFilePaths = new ArrayList<>();
        mFileAudioPaths = new ArrayList<>();
        mFileImagePaths = new ArrayList<>();
        mGetFileListeners = new ArrayList<>();
        mGetFileAudioListeners = new ArrayList<>();
        mGetFileImageListeners = new ArrayList<>();
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
        mIsLoaded = false;

        synchronized (mFileProviderListeners) {
            for (final FileProviderListener fileProviderManager : mFileProviderListeners) {
                fileProviderManager.onFileProviderReloadStarted();
            }
        }

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

                for (final String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection.append(" OR " + MediaStore.Files.FileColumns.DATA + LIKE);
                    searchArray.add('%' + end);
                }
                for (final String end : FileTypeModelENUM.IMAGE.type.getExtensions()) {
                    selection.append(" OR " + MediaStore.Files.FileColumns.DATA + LIKE);
                    searchArray.add('%' + end);
                }
                selection.append(" )");

                final Cursor cursor = mContextApp.getContentResolver().query(
                        allSongsUri,
                        PROJECTION,
                        selection.toString(),
                        searchArray.toArray(new String[searchArray.size()]),
                        MediaStore.Files.FileColumns.TITLE + " ASC");

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                            final String pathLower = path.toLowerCase();

                            if (isAudioPath(pathLower)) {
                                fileAudioPaths.add(path);
                            } else if (isImagePath(pathLower)) {
                                fileImagePaths.add(path);
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

    @Override
    public void getFilePaths(final GetFileListener getFileListener) {
        if (mIsLoaded) {
            getFileListener.onGetFile(new ArrayList<>(mFilePaths));
            return;
        }
        synchronized (mGetFileListeners) {
            //noinspection SimplifiableIfStatement
            if (getFileListener == null || mGetFileListeners.contains(getFileListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return;
            }
            mGetFileListeners.add(getFileListener);
        }
    }

    @Override
    public void getFileAudioPaths(final GetFileAudioListener getFileAudioListener) {
        if (mIsLoaded) {
            getFileAudioListener.onGetFileAudio(new ArrayList<>(mFileAudioPaths));
            return;
        }
        synchronized (mGetFileAudioListeners) {
            //noinspection SimplifiableIfStatement
            if (getFileAudioListener == null || mGetFileAudioListeners.contains(getFileAudioListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return;
            }
            mGetFileAudioListeners.add(getFileAudioListener);
        }
    }

    @Override
    public void getFileImagePaths(final GetFileImageListener getFileImageListener) {
        if (mIsLoaded) {
            getFileImageListener.onGetFileImage(new ArrayList<>(mFileImagePaths));
            return;
        }
        synchronized (mGetFileImageListeners) {
            //noinspection SimplifiableIfStatement
            if (getFileImageListener == null || mGetFileImageListeners.contains(getFileImageListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return;
            }
            mGetFileImageListeners.add(getFileImageListener);
        }
    }

    @Override
    public void clearCache() {
        mFileAudioPaths.clear();
        mFileImagePaths.clear();
        mFilePaths.clear();
    }

    public boolean registerFileProviderListener(final FileProviderListener fileProviderListener) {
        synchronized (mFileProviderListeners) {
            //noinspection SimplifiableIfStatement
            if (fileProviderListener == null || mFileProviderListeners.contains(fileProviderListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mFileProviderListeners.add(fileProviderListener);
        }
    }

    public boolean unregisterFileProviderListener(final FileProviderListener fileProviderListener) {
        synchronized (mFileProviderListeners) {
            return mFileProviderListeners.remove(fileProviderListener);
        }
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

        mIsLoaded = true;

        if (fileProviderListener != null) {
            fileProviderListener.onFileProviderAllBasicLoaded(filePaths);
            fileProviderListener.onFileProviderAudioLoaded(fileAudioPaths);
            fileProviderListener.onFileProviderImageLoaded(fileImagePaths);
        }

        synchronized (mGetFileListeners) {
            for (final GetFileListener getFileListener : mGetFileListeners) {
                getFileListener.onGetFile(filePaths);
            }
            mGetFileListeners.clear();
        }

        synchronized (mGetFileAudioListeners) {
            for (final GetFileAudioListener getFileAudioListener : mGetFileAudioListeners) {
                getFileAudioListener.onGetFileAudio(fileAudioPaths);
            }
            mGetFileAudioListeners.clear();
        }

        synchronized (mGetFileImageListeners) {
            for (final GetFileImageListener getFileImageListener : mGetFileImageListeners) {
                getFileImageListener.onGetFileImage(fileImagePaths);
            }
            mGetFileImageListeners.clear();
        }

        synchronized (mFileProviderListeners) {
            for (final FileProviderListener fileProviderManager : mFileProviderListeners) {
                fileProviderManager.onFileProviderAllBasicLoaded(filePaths);
                fileProviderManager.onFileProviderAudioLoaded(fileAudioPaths);
                fileProviderManager.onFileProviderImageLoaded(fileImagePaths);
            }
        }
        mIsLoadLaunched = false;
    }

    private void notifyLoadFailed(
            @Nullable final FileProviderListener fileProviderListener,
            @LoadingError final int error) {

        Log.e(TAG, "notifyLoadFailed LoadingError=" + error);

        if (fileProviderListener != null) {
            fileProviderListener.onFileProviderFailed(error);
        }
        synchronized (mFileProviderListeners) {
            for (final FileProviderListener fileProviderManager : mFileProviderListeners) {
                fileProviderManager.onFileProviderFailed(error);
            }
        }
    }

    private boolean isAudioPath(@NonNull final String pathBrut) {
        final String path = pathBrut.toLowerCase();
        for (final String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
            if (path.endsWith(end)) {
                return true;
            }
        }
        return false;
    }

    private boolean isImagePath(@NonNull final String pathBrut) {
        final String path = pathBrut.toLowerCase();
        for (final String end : FileTypeModelENUM.IMAGE.type.getExtensions()) {
            if (path.endsWith(end)) {
                return true;
            }
        }
        return false;
    }
}
