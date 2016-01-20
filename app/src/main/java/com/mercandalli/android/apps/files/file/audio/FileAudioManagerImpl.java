package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.apps.files.file.FileUtils.getNameFromPath;

/**
 * A {@link FileModel} Manager.
 */
public class FileAudioManagerImpl extends FileAudioManager {

    private static final String LIKE = " LIKE ?";

    private Context mContextApp;

    private final List<GetAllLocalMusicListener> mGetAllLocalMusicListeners = new ArrayList<>();
    private final List<GetLocalMusicFoldersListener> mGetLocalMusicFoldersListeners = new ArrayList<>();
    private final List<GetLocalMusicListener> mGetLocalMusicListeners = new ArrayList<>();

    /* Cache */
    private final HashMap<String, List<FileAudioModel>> mCacheAllLocalMusics = new HashMap<>();
    private final List<FileModel> mCacheLocalMusicFolders = new ArrayList<>();

    private boolean mIsGetAllLocalMusicLaunched;
    private boolean mIsGetLocalMusicFoldersLaunched;

    public FileAudioManagerImpl(Context contextApp) {
        Preconditions.checkNotNull(contextApp);

        mContextApp = contextApp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getAllLocalMusic(
            final Context context,
            final int sortMode,
            final String search,
            final boolean notifyListeners) {

        final String requestKey = search + "¤" + sortMode;
        if (mCacheAllLocalMusics.containsKey(requestKey)) {
            if (notifyListeners) {
                notifyAllLocalMusicListenerSucceeded(mCacheAllLocalMusics.get(requestKey));
            }
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

                final Cursor cursor = context.getContentResolver().query(allSongsUri, PROJECTION, selection, searchArray.toArray(new String[searchArray.size()]), null);
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
                if (notifyListeners) {
                    notifyAllLocalMusicListenerSucceeded(fileModels);
                }
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
            final Context context,
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
            final Context context,
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

                final Cursor cursor = context.getContentResolver().query(allSongsUri, PROJECTION, selection, searchArray.toArray(new String[searchArray.size()]), null);
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
    //endregion notify listeners
}
