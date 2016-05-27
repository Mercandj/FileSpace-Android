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
import com.mercandalli.android.library.base.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

import static com.mercandalli.android.apps.files.file.FileUtils.isAudioPath;
import static com.mercandalli.android.apps.files.file.FileUtils.isImagePath;
import static com.mercandalli.android.apps.files.file.FileUtils.isVideoPath;

/**
 * Tha main {@link FileLocalProviderManager} implementation.
 * Manage {@link List}s of path.
 */
public class FileLocalProviderManagerImpl implements FileLocalProviderManager {

    private static final String STRING_SQL_OR = " OR ";
    private static final String STRING_SQL_LIKE = " LIKE ?";
    private static final String TAG = "FileLocalProviderMa";

    private final Handler mUiHandler;
    private final Thread mUiThread;
    private final Context mContextApp;

    private final List<String> mFilePaths = new ArrayList<>();
    private final List<String> mFileAudioPaths = new ArrayList<>();
    private final List<String> mFileImagePaths = new ArrayList<>();
    private final List<String> mFileVideoPaths = new ArrayList<>();
    private final List<GetFilePathsListener> mGetFilePathsListeners = new ArrayList<>();
    private final List<GetFileAudioListener> mGetFileAudioListeners = new ArrayList<>();
    private final List<GetFileImageListener> mGetFileImageListeners = new ArrayList<>();
    private final List<GetFileVideoListener> mGetFileVideoListeners = new ArrayList<>();

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
                final List<String> fileVideoPaths = new ArrayList<>();

                final String[] PROJECTION = {MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                final String mediaTypeKey = MediaStore.Files.FileColumns.MEDIA_TYPE;
                final StringBuilder selection = new StringBuilder("( " +
                        mediaTypeKey + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + STRING_SQL_OR +
                        mediaTypeKey + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + STRING_SQL_OR +
                        mediaTypeKey + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);

                for (final String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection.append(STRING_SQL_OR + MediaStore.Files.FileColumns.DATA + STRING_SQL_LIKE);
                    searchArray.add('%' + end);
                }
                for (final String end : FileTypeModelENUM.IMAGE.type.getExtensions()) {
                    selection.append(STRING_SQL_OR + MediaStore.Files.FileColumns.DATA + STRING_SQL_LIKE);
                    searchArray.add('%' + end);
                }
                for (final String end : FileTypeModelENUM.VIDEO.type.getExtensions()) {
                    selection.append(STRING_SQL_OR + MediaStore.Files.FileColumns.DATA + STRING_SQL_LIKE);
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
                            } else if (isVideoPath(pathLower)) {
                                fileVideoPaths.add(path);
                            }
                            filePaths.add(path);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                notifyLoadSucceeded(filePaths, fileAudioPaths, fileImagePaths, fileVideoPaths, fileProviderListener);
            }
        }.start();
    }

    @NonNull
    @Override
    public List<String> getFilePaths() {
        return new ArrayList<>(mFilePaths);
    }

    @Override
    public void getFilePaths(final GetFilePathsListener getFilePathsListener) {
        if (mIsLoaded) {
            getFilePathsListener.onGetFile(new ArrayList<>(mFilePaths));
            return;
        }
        synchronized (mGetFilePathsListeners) {
            //noinspection SimplifiableIfStatement
            if (getFilePathsListener == null || mGetFilePathsListeners.contains(getFilePathsListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return;
            }
            mGetFilePathsListeners.add(getFilePathsListener);
        }
    }

    @Override
    public void removeGetFilePathsListener(final GetFilePathsListener getFilePathsListener) {
        synchronized (mGetFilePathsListeners) {
            mGetFilePathsListeners.remove(getFilePathsListener);
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
    public void removeGetFileAudioListener(final GetFileAudioListener getFileAudioListener) {
        synchronized (mGetFileAudioListeners) {
            mGetFileAudioListeners.remove(getFileAudioListener);
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
    public void removeGetFileImageListener(final GetFileImageListener getFileImageListener) {
        synchronized (mGetFileImageListeners) {
            mGetFileImageListeners.remove(getFileImageListener);
        }
    }

    @Override
    public void getFileVideoPaths(final GetFileVideoListener getFileVideoListener) {
        if (mIsLoaded) {
            getFileVideoListener.onGetFileVideo(new ArrayList<>(mFileVideoPaths));
            return;
        }
        synchronized (mGetFileVideoListeners) {
            //noinspection SimplifiableIfStatement
            if (getFileVideoListener == null || mGetFileVideoListeners.contains(getFileVideoListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return;
            }
            mGetFileVideoListeners.add(getFileVideoListener);
        }
    }

    @Override
    public void removeGetFileVideoListener(final GetFileVideoListener getFileVideoListener) {
        synchronized (mGetFileVideoListeners) {
            mGetFileVideoListeners.remove(getFileVideoListener);
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
            @NonNull final List<String> filePaths,
            @NonNull final List<String> fileAudioPaths,
            @NonNull final List<String> fileImagePaths,
            @NonNull final List<String> fileVideoPaths,
            @Nullable final FileProviderListener fileProviderListener) {

        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyLoadSucceeded(filePaths, fileAudioPaths, fileImagePaths, fileVideoPaths, fileProviderListener);
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
        mFileVideoPaths.clear();
        mFileVideoPaths.addAll(fileVideoPaths);

        mIsLoaded = true;

        if (fileProviderListener != null) {
            fileProviderListener.onFileProviderAllBasicLoaded(filePaths);
            fileProviderListener.onFileProviderAudioLoaded(fileAudioPaths);
            fileProviderListener.onFileProviderImageLoaded(fileImagePaths);
            fileProviderListener.onFileProviderVideoLoaded(fileVideoPaths);
        }

        synchronized (mGetFilePathsListeners) {
            for (final GetFilePathsListener getFilePathsListener : mGetFilePathsListeners) {
                getFilePathsListener.onGetFile(filePaths);
            }
            mGetFilePathsListeners.clear();
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

        synchronized (mGetFileVideoListeners) {
            for (final GetFileVideoListener getFileVideoListener : mGetFileVideoListeners) {
                getFileVideoListener.onGetFileVideo(fileVideoPaths);
            }
            mGetFileVideoListeners.clear();
        }

        synchronized (mFileProviderListeners) {
            for (final FileProviderListener fileProviderManager : mFileProviderListeners) {
                fileProviderManager.onFileProviderAllBasicLoaded(filePaths);
                fileProviderManager.onFileProviderAudioLoaded(fileAudioPaths);
                fileProviderManager.onFileProviderImageLoaded(fileImagePaths);
                fileProviderManager.onFileProviderVideoLoaded(fileVideoPaths);
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
}
