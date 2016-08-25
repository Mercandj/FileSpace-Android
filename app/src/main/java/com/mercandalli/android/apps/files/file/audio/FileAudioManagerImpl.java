package com.mercandalli.android.apps.files.file.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Spanned;

import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.audio.artist.Artist;
import com.mercandalli.android.apps.files.file.audio.metadata.FileAudioMetaDataUtils;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;
import com.mercandalli.android.library.base.precondition.Preconditions;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.library.base.java.FileUtils.getExtensionFromPath;
import static com.mercandalli.android.library.base.java.FileUtils.getNameFromPath;
import static com.mercandalli.android.library.base.java.FileUtils.getParentPathFromPath;

/**
 * A {@link FileModel} Manager.
 */
/* package */
class FileAudioManagerImpl extends FileAudioManagerNotifier {

    @NonNull
    protected final Context mContextApp;
    @NonNull
    protected final FileLocalProviderManager mFileLocalProviderManager;
    @NonNull
    protected final FileManager mFileManager;

    /* Cache */
    @NonNull
    protected final List<FileAudioModel> mCacheAllLocalMusics = new ArrayList<>();
    @NonNull
    protected final List<FileModel> mCacheLocalMusicFolders = new ArrayList<>();

    protected boolean mIsGetAllLocalMusicLaunched;
    protected boolean mIsGetLocalMusicFoldersLaunched;

    @NonNull
    private final Handler mUiHandler;

    @NonNull
    private final Thread mUiThread;

    /**
     * The manager constructor.
     *
     * @param contextApp The {@link Context} of this application.
     */
    public FileAudioManagerImpl(final @NonNull Context contextApp) {
        Preconditions.checkNotNull(contextApp);
        mContextApp = contextApp.getApplicationContext();
        mFileLocalProviderManager = FileLocalProviderManager.getInstance(contextApp);
        mFileManager = FileManager.getInstance(contextApp);

        mFileLocalProviderManager.registerFileProviderListener(new FileLocalProviderManager.FileProviderListener() {
            @Override
            public void onFileProviderReloadStarted() {
                super.onFileProviderReloadStarted();
                clearCache();
            }
        });

        final Looper mainLooper = Looper.getMainLooper();
        mUiHandler = new Handler(mainLooper);
        mUiThread = mainLooper.getThread();

        // Cache.
        mIsGetLocalMusicFoldersLaunched = true;
        mIsGetAllLocalMusicLaunched = true;
        mFileLocalProviderManager.getFileAudioPaths(new FileLocalProviderManager.GetFileAudioListener() {
            @Override
            public void onGetFileAudio(@NonNull final List<String> fileAudioPaths) {
                new Thread() {
                    @Override
                    public void run() {
                        threadWorkerCreateLocalMusicFolders(fileAudioPaths);
                        threadWorkerCreateAllLocalMusic(fileAudioPaths);
                    }
                }.start();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NewApi")
    public void getAllLocalMusic() {
        if (mIsGetAllLocalMusicLaunched) {
            return;
        }
        mIsGetAllLocalMusicLaunched = true;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyAllLocalMusicListenerFailed();
            return;
        }

        if (!mCacheAllLocalMusics.isEmpty()) {
            notifyAllLocalMusicListenerSucceeded(mCacheAllLocalMusics, false);
            return;
        }

        mFileLocalProviderManager.getFileAudioPaths(new FileLocalProviderManager.GetFileAudioListener() {
            @Override
            public void onGetFileAudio(@NonNull final List<String> fileAudioPaths) {
                new Thread() {
                    @Override
                    public void run() {
                        threadWorkerCreateAllLocalMusic(fileAudioPaths);
                    }
                }.start();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getLocalMusic(
            final FileModel fileModelDirectParent) {

        Preconditions.checkNotNull(fileModelDirectParent);
        if (!fileModelDirectParent.isDirectory()) {
            notifyLocalMusicListenerFailed();
            return;
        }

        final File file = fileModelDirectParent.getFile();
        if (file == null || !file.exists()) {
            return;
        }

        final List<FileAudioModel> files = new ArrayList<>();
        final List<File> fs = Arrays.asList(file.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(final File file, final String name) {
                        return (new FileTypeModel(getExtensionFromPath(name)))
                                .equals(FileTypeModelENUM.AUDIO.type);
                    }
                }
        ));
        for (final File f : fs) {
            files.add(new FileAudioModel.FileAudioModelBuilder()
                    .file(f).build());
        }
        notifyLocalMusicListenerSucceeded(files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NewApi")
    public void getLocalMusicFolders() {

        if (mIsGetLocalMusicFoldersLaunched) {
            return;
        }
        mIsGetLocalMusicFoldersLaunched = true;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyLocalMusicFoldersListenerFailed();
            return;
        }

        if (!mCacheLocalMusicFolders.isEmpty()) {
            notifyLocalMusicFoldersListenerSucceeded(mCacheLocalMusicFolders, false);
            return;
        }

        mFileLocalProviderManager.getFileAudioPaths(new FileLocalProviderManager.GetFileAudioListener() {
            @Override
            public void onGetFileAudio(@NonNull final List<String> fileAudioPaths) {
                new Thread() {
                    @Override
                    public void run() {
                        threadWorkerCreateLocalMusicFolders(fileAudioPaths);
                    }
                }.start();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NewApi")
    public void getAllLocalMusicAlbums() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyAllLocalMusicAlbumsListenerFailed();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllLocalMusicAlbumsInternal();
            }
        }).start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getAllLocalMusicArtists() {

    }

    @Override
    public boolean setFileAudioMetaData(
            final File file,
            final String newTitle,
            final String newArtist,
            final String newAlbum) {
        return setFileAudioMetaDataPrivate(file, newTitle, newArtist, newAlbum);
    }

    @Override
    public boolean setFileAudioMetaData(
            final FileAudioModel fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum) {
        Preconditions.checkNotNull(fileAudio);
        final File file = fileAudio.getFile();
        return file != null && setFileAudioMetaDataPrivate(file, newTitle, newArtist, newAlbum);
    }

    @Override
    public Spanned toSpanned(final Context context, final FileAudioModel fileAudioModel) {
        return mFileManager.toSpanned(context, fileAudioModel);
    }

    //region Create.
    private void threadWorkerCreateLocalMusicFolders(@NonNull final List<String> fileAudioPaths) {
        final Map<String, MutableInt> directories = new HashMap<>();
        for (final String path : fileAudioPaths) {
            final String parentPath = getParentPathFromPath(path);
            final MutableInt count = directories.get(parentPath);
            if (count == null) {
                directories.put(parentPath, new MutableInt());
            } else {
                count.increment();
            }
        }

        final List<FileModel> result = new ArrayList<>();
        for (String path : directories.keySet()) {
            result.add(new FileModel.FileModelBuilder()
                    .id(path.hashCode())
                    .url(path)
                    .name(getNameFromPath(path))
                    .isDirectory(true)
                    .countAudio(directories.get(path).value)
                    .isOnline(false)
                    .build());
        }

        // Sorting
        Collections.sort(result, new Comparator<FileModel>() {
            @Override
            public int compare(final FileModel fileModel1, final FileModel fileModel2) {
                return fileModel2.getCountAudio() - fileModel1.getCountAudio();
            }
        });

        notifyLocalMusicFoldersListenerSucceeded(result, true);
    }

    private void threadWorkerCreateAllLocalMusic(@NonNull final List<String> fileAudioPaths) {
        final List<FileAudioModel> files = new ArrayList<>();
        for (final String path : fileAudioPaths) {
            final File file = new File(path);
            if (file.exists() && !file.isDirectory()) {
                files.add(new FileAudioModel.FileAudioModelBuilder().file(file).build());
            }
        }
        notifyAllLocalMusicListenerSucceeded(files, true);
    }
    //endregion Create.

    private boolean setFileAudioMetaDataPrivate(
            final File file,
            final String newTitle,
            final String newArtist,
            final String newAlbum) {
        Preconditions.checkNotNull(file);
        final boolean result = file.exists() &&
                FileAudioMetaDataUtils.setMetaData(file, newTitle, newArtist, newAlbum);
        if (result) {
            notifyOnMusicFile();
        }
        return result;
    }

    public void clearCache() {
        mCacheAllLocalMusics.clear();
        mCacheLocalMusicFolders.clear();
    }

    //region notify listeners
    private void notifyAllLocalMusicListenerSucceeded(
            final List<FileAudioModel> fileModels,
            final boolean cacheResult) {
        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyAllLocalMusicListenerSucceeded(fileModels, cacheResult);
                }
            });
            return;
        }
        if (cacheResult) {
            mCacheAllLocalMusics.clear();
            mCacheAllLocalMusics.addAll(fileModels);
        }
        if (!mIsGetAllLocalMusicLaunched) {
            return;
        }
        mIsGetAllLocalMusicLaunched = false;

        synchronized (mGetAllLocalMusicListeners) {
            for (int i = 0, size = mGetAllLocalMusicListeners.size(); i < size; i++) {
                mGetAllLocalMusicListeners.get(i).onAllLocalMusicSucceeded(fileModels);
            }
        }
    }

    private void notifyAllLocalMusicListenerFailed() {
        synchronized (mGetAllLocalMusicListeners) {
            for (int i = 0, size = mGetAllLocalMusicListeners.size(); i < size; i++) {
                mGetAllLocalMusicListeners.get(i).onAllLocalMusicFailed();
            }
        }
    }

    private void notifyAllLocalMusicArtistsListenerSucceeded(final List<Artist> artists) {
        synchronized (mGetAllLocalMusicArtistsListeners) {
            for (int i = 0, size = mGetAllLocalMusicArtistsListeners.size(); i < size; i++) {
                mGetAllLocalMusicArtistsListeners.get(i).onAllLocalMusicArtistsSucceeded(artists);
            }
        }
    }

    private void notifyAllLocalMusicAlbumsListenerSucceeded(final List<Album> albums) {
        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyAllLocalMusicAlbumsListenerSucceeded(albums);
                }
            });
            return;
        }
        synchronized (mGetAllLocalMusicAlbumsListeners) {
            for (int i = 0, size = mGetAllLocalMusicAlbumsListeners.size(); i < size; i++) {
                mGetAllLocalMusicAlbumsListeners.get(i).onAllLocalMusicAlbumsSucceeded(albums);
            }
        }
    }

    private void notifyAllLocalMusicAlbumsListenerFailed() {
        synchronized (mGetAllLocalMusicAlbumsListeners) {
            for (int i = 0, size = mGetAllLocalMusicAlbumsListeners.size(); i < size; i++) {
                mGetAllLocalMusicAlbumsListeners.get(i).onAllLocalMusicAlbumsFailed();
            }
        }
    }

    protected void notifyLocalMusicFoldersListenerSucceeded(
            final List<FileModel> fileModelFolders,
            final boolean cacheResult) {
        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyLocalMusicFoldersListenerSucceeded(fileModelFolders, cacheResult);
                }
            });
            return;
        }
        if (cacheResult) {
            mCacheLocalMusicFolders.clear();
            mCacheLocalMusicFolders.addAll(fileModelFolders);
        }
        if (!mIsGetLocalMusicFoldersLaunched) {
            return;
        }
        mIsGetLocalMusicFoldersLaunched = false;

        synchronized (mGetLocalMusicFoldersListeners) {
            for (int i = 0, size = mGetLocalMusicFoldersListeners.size(); i < size; i++) {
                mGetLocalMusicFoldersListeners.get(i).onLocalMusicFoldersSucceeded(fileModelFolders);
            }
        }
    }

    protected void notifyLocalMusicFoldersListenerFailed() {
        synchronized (mGetLocalMusicFoldersListeners) {
            for (int i = 0, size = mGetLocalMusicFoldersListeners.size(); i < size; i++) {
                mGetLocalMusicFoldersListeners.get(i).onLocalMusicFoldersFailed();
            }
        }
    }

    private void notifyLocalMusicListenerSucceeded(final List<FileAudioModel> fileModels) {
        synchronized (mGetLocalMusicListeners) {
            for (int i = 0, size = mGetLocalMusicListeners.size(); i < size; i++) {
                mGetLocalMusicListeners.get(i).onLocalMusicSucceeded(fileModels);
            }
        }
    }

    private void notifyLocalMusicListenerFailed() {
        synchronized (mGetLocalMusicListeners) {
            for (int i = 0, size = mGetLocalMusicListeners.size(); i < size; i++) {
                mGetLocalMusicListeners.get(i).onLocalMusicFailed();
            }
        }
    }

    private void notifyOnMusicFile() {
        clearCache();
        synchronized (mMusicsChangeListeners) {
            for (int i = 0, size = mMusicsChangeListeners.size(); i < size; i++) {
                mMusicsChangeListeners.get(i).onMusicsContentChange();
            }
        }
    }
    //endregion notify listeners

    private void getAllLocalMusicAlbumsInternal() {
        // Used to count the number of music inside.
        final Map<Integer, Album> albums = new HashMap<>();

        final Cursor cursor = mContextApp.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Albums._ID,
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums.NUMBER_OF_SONGS},
                null,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    final int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                    final String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                    final int numberOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                    if (!albums.containsKey(id) && name != null) {
                        albums.put(id,
                                new Album(
                                        id,
                                        name,
                                        numberOfSongs));
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        notifyAllLocalMusicAlbumsListenerSucceeded(new ArrayList<>(albums.values()));
    }
}
