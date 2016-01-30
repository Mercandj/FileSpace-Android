package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.Spanned;

import com.mercandalli.android.apps.files.common.util.HtmlUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.util.TimeUtils;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.FileUtils;
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
    private final List<GetLocalMusicFoldersListener> mGetLocalMusicFoldersListeners = new ArrayList<>();
    private final List<GetLocalMusicListener> mGetLocalMusicListeners = new ArrayList<>();
    private final List<MusicsChangeListener> mMusicsChangeListeners = new ArrayList<>();

    /* Cache */
    private final HashMap<String, List<FileAudioModel>> mCacheAllLocalMusics = new HashMap<>();
    private final List<FileModel> mCacheLocalMusicFolders = new ArrayList<>();

    private boolean mIsGetAllLocalMusicLaunched;
    private boolean mIsGetLocalMusicFoldersLaunched;

    /**
     * The manager constructor.
     *
     * @param contextApp The {@link Context} of this application.
     */
    public FileAudioManagerImpl(final Context contextApp) {
        Preconditions.checkNotNull(contextApp);
        mContextApp = contextApp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getAllLocalMusic(final int sortMode, final String search) {
        final String requestKey = search + "¤" + sortMode;
        if (mCacheAllLocalMusics.containsKey(requestKey)) {
            notifyAllLocalMusicListenerSucceeded(mCacheAllLocalMusics.get(requestKey));
            return;
        }
        if (mIsGetAllLocalMusicLaunched) {
            return;
        }
        mIsGetAllLocalMusicLaunched = true;
        new AsyncTask<Void, Void, List<FileAudioModel>>() {
            @Override
            protected List<FileAudioModel> doInBackground(Void... params) {
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

                return files;
            }

            @Override
            protected void onPostExecute(final List<FileAudioModel> fileModels) {
                notifyAllLocalMusicListenerSucceeded(fileModels);
                mCacheAllLocalMusics.put(requestKey, fileModels);
                mIsGetAllLocalMusicLaunched = false;
                super.onPostExecute(fileModels);
            }
        }.execute();
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
        final List<FileAudioModel> files = new ArrayList<>();
        final List<File> fs = Arrays.asList(fileModelDirectParent.getFile().listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return (new FileTypeModel(FileUtils.getExtensionFromPath(name)))
                                .equals(FileTypeModelENUM.AUDIO.type);
                    }
                }
        ));
        for (File file : fs) {
            files.add(new FileAudioModel.FileMusicModelBuilder()
                    .file(file).build());
        }
        notifyLocalMusicListenerSucceeded(files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getLocalMusicFolders(
            final int sortMode,
            final String search) {

        if (!mCacheLocalMusicFolders.isEmpty()) {
            notifyLocalMusicFoldersListenerSucceeded(mCacheLocalMusicFolders);
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
                mCacheLocalMusicFolders.clear();
                mCacheLocalMusicFolders.addAll(fileModels);
                mIsGetLocalMusicFoldersLaunched = false;
                super.onPostExecute(fileModels);
            }
        }.execute();
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

    //region notify listeners
    private void notifyAllLocalMusicListenerSucceeded(final List<FileAudioModel> fileModels) {
        synchronized (mGetAllLocalMusicListeners) {
            for (int i = 0, size = mGetAllLocalMusicListeners.size(); i < size; i++) {
                mGetAllLocalMusicListeners.get(i).onAllLocalMusicSucceeded(fileModels);
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
