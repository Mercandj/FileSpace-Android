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
import android.support.annotation.Nullable;
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
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    private final List<GetAllLocalMusicListener> mGetAllLocalMusicListeners = new ArrayList<>();
    private final List<GetAllLocalMusicArtistsListener> mGetAllLocalMusicArtistsListeners = new ArrayList<>();
    private final List<GetAllLocalMusicAlbumsListener> mGetAllLocalMusicAlbumsListeners = new ArrayList<>();
    private final List<GetLocalMusicFoldersListener> mGetLocalMusicFoldersListeners = new ArrayList<>();
    private final List<GetLocalMusicListener> mGetLocalMusicListeners = new ArrayList<>();
    private final List<MusicsChangeListener> mMusicsChangeListeners = new ArrayList<>();

    /* Cache */
    private final HashMap<String, List<FileAudioModel>> mCacheAllLocalMusics = new HashMap<>();
    private final HashMap<String, List<FileModel>> mCacheLocalMusicFolders = new HashMap<>();

    private boolean mIsGetAllLocalMusicLaunched;
    private boolean mIsGetLocalMusicFoldersLaunched;

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

        final Looper mainLooper = Looper.getMainLooper();
        mUiHandler = new Handler(mainLooper);
        mUiThread = mainLooper.getThread();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NewApi")
    public void getAllLocalMusic(final int sortMode, final String search) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyAllLocalMusicListenerFailed();
            return;
        }

        final String requestKey = search + "¤" + sortMode;
        if (mCacheAllLocalMusics.containsKey(requestKey)) {
            notifyAllLocalMusicListenerSucceeded(mCacheAllLocalMusics.get(requestKey), null);
            return;
        }
        if (mIsGetAllLocalMusicLaunched) {
            return;
        }
        mIsGetAllLocalMusicLaunched = true;

        new Thread() {
            @Override
            public void run() {
                final List<FileAudioModel> files = new ArrayList<>();

                final String[] PROJECTION = {MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection += " OR " + MediaStore.Files.FileColumns.DATA + LIKE;
                    searchArray.add("%" + end);
                }
                selection += " )";

                if (search != null && !search.isEmpty()) {
                    searchArray.add("%" + search + "%");
                    selection += " AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + LIKE;
                }

                final Cursor cursor = mContextApp.getContentResolver().query(allSongsUri, PROJECTION, selection, searchArray.toArray(new String[searchArray.size()]), null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            final File file = new File(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                            if (file.exists() && !file.isDirectory()) {
                                files.add(new FileAudioModel.FileMusicModelBuilder()
                                        .file(file).build());
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

                notifyAllLocalMusicListenerSucceeded(files, requestKey);
            }
        }.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getLocalMusic(
            final FileModel fileModelDirectParent,
            final int sortMode,
            final String search) {

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
    public void getLocalMusicFolders(
            final int sortMode,
            final String search) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyLocalMusicFoldersListenerFailed();
            return;
        }

        final String requestKey = search + "¤" + sortMode;
        if (mCacheLocalMusicFolders.containsKey(requestKey)) {
            notifyLocalMusicFoldersListenerSucceeded(mCacheLocalMusicFolders.get(requestKey));
            return;
        }
        if (mIsGetLocalMusicFoldersLaunched) {
            return;
        }
        mIsGetLocalMusicFoldersLaunched = true;
        new AsyncTask<Void, Void, List<FileModel>>() {
            @Override
            protected List<FileModel> doInBackground(Void... params) {
                // Used to count the number of music inside.
                final Map<String, MutableInt> directories = new HashMap<>();

                final String[] PROJECTION = new String[]{MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection += " OR " + MediaStore.Files.FileColumns.DATA + LIKE;
                    searchArray.add("%" + end);
                }
                selection += " )";

                if (search != null && !search.isEmpty()) {
                    searchArray.add("%" + search + "%");
                    selection += " AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + LIKE;
                }

                final Cursor cursor = mContextApp.getContentResolver().query(allSongsUri, PROJECTION, selection, searchArray.toArray(new String[searchArray.size()]), null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            final String parentPath = FileUtils.getParentPathFromPath(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                            final MutableInt count = directories.get(parentPath);
                            if (count == null) {
                                directories.put(parentPath, new MutableInt());
                            } else {
                                count.increment();
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
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
                return result;
            }

            @Override
            protected void onPostExecute(final List<FileModel> fileModels) {
                notifyLocalMusicFoldersListenerSucceeded(fileModels);
                mCacheLocalMusicFolders.put(requestKey, fileModels);
                mIsGetLocalMusicFoldersLaunched = false;
                super.onPostExecute(fileModels);
            }
        }.execute();
    }

    @Override
    @SuppressLint("NewApi")
    public void getAllLocalMusicAlbums(final int sortMode, final String search) {

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

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection += " OR " + MediaStore.Files.FileColumns.DATA + LIKE;
                    searchArray.add("%" + end);
                }
                selection += " )";

                if (search != null && !search.isEmpty()) {
                    searchArray.add("%" + search + "%");
                    selection += " AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + LIKE;
                }

                final Cursor cursor = mContextApp.getContentResolver().query(allSongsUri, PROJECTION, selection, searchArray.toArray(new String[searchArray.size()]), null);
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
    public void getAllLocalMusicArtists(int sortMode, String search) {

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

    @Override
    public boolean registerAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener) {
        synchronized (mGetAllLocalMusicListeners) {
            //noinspection SimplifiableIfStatement
            if (getAllLocalMusicListener == null || mGetAllLocalMusicListeners.contains(getAllLocalMusicListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetAllLocalMusicListeners.add(getAllLocalMusicListener);
        }
    }

    @Override
    public boolean unregisterAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener) {
        synchronized (mGetAllLocalMusicListeners) {
            return mGetAllLocalMusicListeners.remove(getAllLocalMusicListener);
        }
    }

    @Override
    public boolean registerLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalMusicFoldersListeners) {
            //noinspection SimplifiableIfStatement
            if (getLocalImageFoldersListener == null || mGetLocalMusicFoldersListeners.contains(getLocalImageFoldersListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetLocalMusicFoldersListeners.add(getLocalImageFoldersListener);
        }
    }

    @Override
    public boolean unregisterLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalMusicFoldersListeners) {
            return mGetLocalMusicFoldersListeners.remove(getLocalImageFoldersListener);
        }
    }

    @Override
    public boolean registerLocalMusicListener(GetLocalMusicListener getLocalImageListener) {
        synchronized (mGetLocalMusicListeners) {
            //noinspection SimplifiableIfStatement
            if (getLocalImageListener == null || mGetLocalMusicListeners.contains(getLocalImageListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetLocalMusicListeners.add(getLocalImageListener);
        }
    }

    @Override
    public boolean unregisterLocalMusicListener(GetLocalMusicListener getLocalImageListener) {
        synchronized (mGetLocalMusicListeners) {
            return mGetLocalMusicListeners.remove(getLocalImageListener);
        }
    }

    @Override
    public boolean registerOnMusicUpdateListener(MusicsChangeListener musicsChangeListener) {
        synchronized (mMusicsChangeListeners) {
            //noinspection SimplifiableIfStatement
            if (musicsChangeListener == null || mMusicsChangeListeners.contains(musicsChangeListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mMusicsChangeListeners.add(musicsChangeListener);
        }
    }

    @Override
    public boolean unregisterOnMusicUpdateListener(MusicsChangeListener musicsChangeListener) {
        synchronized (mMusicsChangeListeners) {
            return mMusicsChangeListeners.remove(musicsChangeListener);
        }
    }

    @Override
    public boolean registerAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        synchronized (mGetAllLocalMusicArtistsListeners) {
            //noinspection SimplifiableIfStatement
            if (getAllLocalMusicArtistsListener == null || mGetAllLocalMusicArtistsListeners.contains(getAllLocalMusicArtistsListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetAllLocalMusicArtistsListeners.add(getAllLocalMusicArtistsListener);
        }
    }

    @Override
    public boolean unregisterAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        synchronized (mGetAllLocalMusicArtistsListeners) {
            return mGetAllLocalMusicArtistsListeners.remove(getAllLocalMusicArtistsListener);
        }
    }

    @Override
    public boolean registerAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        synchronized (mGetAllLocalMusicAlbumsListeners) {
            //noinspection SimplifiableIfStatement
            if (getAllLocalMusicAlbumsListener == null || mGetAllLocalMusicAlbumsListeners.contains(getAllLocalMusicAlbumsListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetAllLocalMusicAlbumsListeners.add(getAllLocalMusicAlbumsListener);
        }
    }

    @Override
    public boolean unregisterAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        synchronized (mGetAllLocalMusicAlbumsListeners) {
            return mGetAllLocalMusicAlbumsListeners.remove(getAllLocalMusicAlbumsListener);
        }
    }

    //region notify listeners
    private void notifyAllLocalMusicListenerSucceeded(final List<FileAudioModel> fileModels, @Nullable final String requestKey) {
        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyAllLocalMusicListenerSucceeded(fileModels, requestKey);
                }
            });
            return;
        }
        if (requestKey != null) {
            mCacheAllLocalMusics.put(requestKey, fileModels);
            mIsGetAllLocalMusicLaunched = false;
        }

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

    protected void notifyLocalMusicFoldersListenerSucceeded(final List<FileModel> fileModels) {
        synchronized (mGetLocalMusicFoldersListeners) {
            for (int i = 0, size = mGetLocalMusicFoldersListeners.size(); i < size; i++) {
                mGetLocalMusicFoldersListeners.get(i).onLocalMusicFoldersSucceeded(fileModels);
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

    private void notifyOnMusicUpdate() {
        clearCache();
        synchronized (mMusicsChangeListeners) {
            for (int i = 0, size = mMusicsChangeListeners.size(); i < size; i++) {
                mMusicsChangeListeners.get(i).onMusicsContentChange();
            }
        }
    }
    //endregion notify listeners

    private boolean setFileAudioMetaDataPrivate(
            final File file,
            final String newTitle,
            final String newArtist,
            final String newAlbum) {
        Preconditions.checkNotNull(file);
        final boolean result = file.exists() &&
                FileAudioMetaDataUtils.setMetaData(file, newTitle, newArtist, newAlbum);
        if (result) {
            notifyOnMusicUpdate();
        }
        return result;
    }

    private void clearCache() {
        mCacheAllLocalMusics.clear();
        mCacheLocalMusicFolders.clear();
    }
}
