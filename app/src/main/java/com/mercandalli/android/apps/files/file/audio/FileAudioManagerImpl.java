package com.mercandalli.android.apps.files.file.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Spanned;

import com.mercandalli.android.apps.files.common.util.HtmlUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.util.TimeUtils;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.file.audio.album.Album;
import com.mercandalli.android.apps.files.file.audio.artist.Artist;
import com.mercandalli.android.apps.files.file.audio.metadata.FileAudioMetaDataUtils;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.precondition.Preconditions;

import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.apps.files.file.FileUtils.getNameFromPath;

/**
 * A {@link FileModel} Manager.
 */
public class FileAudioManagerImpl extends FileAudioManager {

    private static final String LIKE = " LIKE ?";

    protected final Context mContextApp;
    protected final FileLocalProviderManager mFileLocalProviderManager;

    /* Cache */
    protected final List<FileAudioModel> mCacheAllLocalMusics = new ArrayList<>();
    protected final List<FileModel> mCacheLocalMusicFolders = new ArrayList<>();

    protected boolean mIsGetAllLocalMusicLaunched;
    protected boolean mIsGetLocalMusicFoldersLaunched;

    private final Handler mUiHandler;
    private final Thread mUiThread;

    /**
     * The manager constructor.
     *
     * @param contextApp The {@link Context} of this application.
     */
    public FileAudioManagerImpl(final Context contextApp) {
        Preconditions.checkNotNull(contextApp);

        mContextApp = contextApp;
        mFileLocalProviderManager = FileApp.get().getFileAppComponent().provideFileProviderManager();
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
            public void onGetFileAudio(final List<String> fileAudioPaths) {
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
            public void onGetFileAudio(final List<String> fileAudioPaths) {
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
        if (!file.exists()) {
            return;
        }

        final List<FileAudioModel> files = new ArrayList<>();
        final List<File> fs = Arrays.asList(file.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return (new FileTypeModel(FileUtils.getExtensionFromPath(name)))
                                .equals(FileTypeModelENUM.AUDIO.type);
                    }
                }
        ));
        for (final File f : fs) {
            files.add(new FileAudioModel.FileMusicModelBuilder()
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
            public void onGetFileAudio(final List<String> fileAudioPaths) {
                new Thread() {
                    @Override
                    public void run() {
                        threadWorkerCreateLocalMusicFolders(fileAudioPaths);
                    }
                }.start();
            }
        });

    }

    @Override
    @SuppressLint("NewApi")
    public void getAllLocalMusicAlbums() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyAllLocalMusicAlbumsListenerFailed();
            return;
        }

        new AsyncTask<Void, Void, List<Album>>() {
            @Override
            protected List<Album> doInBackground(Void... params) {
                // Used to count the number of music inside.
                final Map<String, Album> albums = new HashMap<>();

                final String[] PROJECTION = new String[]{MediaStore.Files.FileColumns.DATA, MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                final StringBuilder selection = new StringBuilder("( " +
                        MediaStore.Files.FileColumns.MEDIA_TYPE + " = " +
                        MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection.append(" OR " + MediaStore.Files.FileColumns.DATA + LIKE);
                    searchArray.add('%' + end);
                }
                selection.append(" )");

                final Cursor cursor = mContextApp.getContentResolver().query(allSongsUri, PROJECTION, selection.toString(), searchArray.toArray(new String[searchArray.size()]), null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            final String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                            Album album;
                            if (albums.containsKey(id)) {
                                album = albums.get(id);
                            } else {
                                album = new Album(id, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                            }
                            album.addFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                            albums.put(id, album);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                return new ArrayList<>(albums.values());
            }

            @Override
            protected void onPostExecute(final List<Album> fileModels) {
                notifyAllLocalMusicAlbumsListenerSucceeded(fileModels);
                super.onPostExecute(fileModels);
            }
        }.execute();
    }

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
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(fileAudioModel);

        final FileTypeModel type = fileAudioModel.getType();
        final boolean isDirectory = fileAudioModel.isDirectory();
        final long size = fileAudioModel.getSize();
        final boolean isPublic = fileAudioModel.isPublic();
        final Date dateCreation = fileAudioModel.getDateCreation();

        final List<StringPair> spl = new ArrayList<>();
        spl.add(new StringPair("Name", fileAudioModel.getName()));
        if (!fileAudioModel.isDirectory()) {
            spl.add(new StringPair("Extension", type.toString()));
        }
        spl.add(new StringPair("Type", type.getTitle(context)));
        spl.add(new StringPair("Title", fileAudioModel.getTitle()));
        spl.add(new StringPair("Artist", fileAudioModel.getArtist()));
        spl.add(new StringPair("Album", fileAudioModel.getAlbum()));
        if (!isDirectory || size != 0) {
            spl.add(new StringPair("Size", FileUtils.humanReadableByteCount(size)));
        }
        if (dateCreation != null) {
            if (fileAudioModel.isOnline()) {
                spl.add(new StringPair("Upload date", TimeUtils.getDate(dateCreation)));
            } else {
                spl.add(new StringPair("Last modification date", TimeUtils.getDate(dateCreation)));
            }
        }
        if (fileAudioModel.isOnline()) {
            spl.add(new StringPair("Visibility", isPublic ? "Public" : "Private"));
        }
        spl.add(new StringPair("Path", fileAudioModel.getUrl()));
        return HtmlUtils.createListItem(spl);
    }

    //region Create.
    private void threadWorkerCreateLocalMusicFolders(@NonNull final List<String> fileAudioPaths) {
        final Map<String, MutableInt> directories = new HashMap<>();
        for (final String path : fileAudioPaths) {
            final String parentPath = FileUtils.getParentPathFromPath(path);
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

        notifyLocalMusicFoldersListenerSucceeded(result, true);
    }

    private void threadWorkerCreateAllLocalMusic(@NonNull final List<String> fileAudioPaths) {
        final List<FileAudioModel> files = new ArrayList<>();
        final MyID3 myID3 = new MyID3();
        for (final String path : fileAudioPaths) {
            final File file = new File(path);
            if (file.exists() && !file.isDirectory()) {
                files.add(new FileAudioModel.FileMusicModelBuilder().file(file, myID3).build());
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

    private void clearCache() {
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
}
