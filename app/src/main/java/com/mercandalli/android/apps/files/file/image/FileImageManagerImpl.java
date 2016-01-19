package com.mercandalli.android.apps.files.file.image;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
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

public class FileImageManagerImpl implements FileImageManager {

    private static final String LIKE = " LIKE ?";

    private final List<GetAllLocalImageListener> mGetAllLocalImageListeners = new ArrayList<>();
    private final List<GetLocalImageFoldersListener> mGetLocalImageFoldersListeners = new ArrayList<>();
    private final List<GetLocalImageListener> mGetLocalImageListeners = new ArrayList<>();

    /* Cache */
    private final List<FileModel> mCacheGetAllLocalImage = new ArrayList<>();
    private final List<FileModel> mCacheGetLocalImagesFolders = new ArrayList<>();

    private boolean mIsGetAllLocalImageLaunched;
    private boolean mIsGetLocalImageFoldersLaunched;

    public FileImageManagerImpl(Application application) {

    }

    @Override
    public void getAllLocalImage(
            final Context context,
            final int sortMode,
            final String search) {

        if (!mCacheGetAllLocalImage.isEmpty()) {
            notifyAllLocalImageListenerSucceeded(mCacheGetAllLocalImage);
            return;
        }
        if (mIsGetAllLocalImageLaunched) {
            return;
        }
        mIsGetAllLocalImageLaunched = true;
        new AsyncTask<Void, Void, List<FileModel>>() {
            @Override
            protected List<FileModel> doInBackground(Void... params) {
                final List<FileModel> files = new ArrayList<>();

                final String[] PROJECTION = {MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

                for (String end : FileTypeModelENUM.PICTURE.type.getExtensions()) {
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
                            final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                            if (!path.startsWith("/storage/emulated/0/Android/")) {
                                final File file = new File(path);
                                if (file.exists() && !file.isDirectory()) {

                                    FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder()
                                            .file(file);

                                    //if (mSortMode == SharedAudioPlayerUtils.SORT_SIZE)
                                    //    fileMusicModel.adapterTitleStart = FileUtils.humanReadableByteCount(fileMusicModel.getSize()) + " - ";

                                    files.add(fileModelBuilder.build());
                                }
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                if (sortMode == Constants.SORT_ABC) {
                    Collections.sort(files, new Comparator<FileModel>() {
                        @Override
                        public int compare(final FileModel f1, final FileModel f2) {
                            if (f1.getName() == null || f2.getName() == null) {
                                return 0;
                            }
                            return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
                        }
                    });
                } else if (sortMode == Constants.SORT_SIZE) {
                    Collections.sort(files, new Comparator<FileModel>() {
                        @Override
                        public int compare(final FileModel f1, final FileModel f2) {
                            return (new Long(f2.getSize())).compareTo(f1.getSize());
                        }
                    });
                } else {
                    final Map<FileModel, Long> staticLastModifiedTimes = new HashMap<>();
                    for (FileModel f : files) {
                        staticLastModifiedTimes.put(f, f.getLastModified());
                    }
                    Collections.sort(files, new Comparator<FileModel>() {
                        @Override
                        public int compare(final FileModel f1, final FileModel f2) {
                            return staticLastModifiedTimes.get(f2).compareTo(staticLastModifiedTimes.get(f1));
                        }
                    });
                }

                return files;
            }

            @Override
            protected void onPostExecute(List<FileModel> fileModels) {
                notifyAllLocalImageListenerSucceeded(fileModels);
                mCacheGetAllLocalImage.clear();
                mCacheGetAllLocalImage.addAll(fileModels);
                mIsGetAllLocalImageLaunched = false;
                super.onPostExecute(fileModels);
            }
        }.execute();
    }

    //region getLocalImageFolders
    @Override
    public void getLocalImageFolders(
            final Context context,
            final int sortMode,
            final String search) {

        if (!mCacheGetLocalImagesFolders.isEmpty()) {
            notifyLocalImageFoldersListenerSucceeded(mCacheGetLocalImagesFolders);
            return;
        }
        if (mIsGetLocalImageFoldersLaunched) {
            return;
        }
        mIsGetLocalImageFoldersLaunched = true;

        new AsyncTask<Void, Void, List<FileModel>>() {
            @Override
            protected List<FileModel> doInBackground(Void... params) {
                // Used to count the number of music inside.
                final Map<String, MutableInt> directories = new HashMap<>();

                final String[] PROJECTION = new String[]{MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

                for (String end : FileTypeModelENUM.PICTURE.type.getExtensions()) {
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
                    if (!path.startsWith("/storage/emulated/0/Android/")) {
                        result.add(new FileModel.FileModelBuilder()
                                .id(path.hashCode())
                                .url(path)
                                .name(getNameFromPath(path))
                                .isDirectory(true)
                                .countAudio(directories.get(path).value)
                                .isOnline(false)
                                .build());
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(final List<FileModel> fileModels) {
                notifyLocalImageFoldersListenerSucceeded(fileModels);
                mCacheGetLocalImagesFolders.clear();
                mCacheGetLocalImagesFolders.addAll(fileModels);
                mIsGetLocalImageFoldersLaunched = false;
                super.onPostExecute(fileModels);
            }
        }.execute();
    }
    //endregion getLocalImageFolders

    //region getLocalImage
    @Override
    public void getLocalImage(
            final Context context,
            final FileModel fileModelDirectParent,
            final int sortMode,
            final String search) {

        Preconditions.checkNotNull(fileModelDirectParent);
        if (!fileModelDirectParent.isDirectory()) {
            notifyLocalImageListenerFailed();
            return;
        }
        final List<FileModel> files = new ArrayList<>();
        List<File> fs = Arrays.asList(fileModelDirectParent.getFile().listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return (new FileTypeModel(FileUtils.getExtensionFromPath(name))).equals(FileTypeModelENUM.PICTURE.type);
                    }
                }
        ));
        for (File file : fs) {
            final FileModel.FileModelBuilder fileModelBuilder =
                    new FileAudioModel.FileModelBuilder().file(file);
            files.add(fileModelBuilder.build());
        }
        notifyLocalImageListenerSucceeded(files);
    }

    @Override
    public boolean registerAllLocalImageListener(final GetAllLocalImageListener getAllLocalImageListener) {
        synchronized (mGetAllLocalImageListeners) {
            //noinspection SimplifiableIfStatement
            if (getAllLocalImageListener == null || mGetAllLocalImageListeners.contains(getAllLocalImageListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetAllLocalImageListeners.add(getAllLocalImageListener);
        }
    }

    @Override
    public boolean unregisterAllLocalImageListener(final GetAllLocalImageListener getAllLocalImageListener) {
        synchronized (mGetAllLocalImageListeners) {
            return mGetAllLocalImageListeners.remove(getAllLocalImageListener);
        }
    }
    //endregion getLocalImage

    //region Register / Unregister listeners
    @Override
    public boolean registerLocalImageFoldersListener(final GetLocalImageFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalImageFoldersListeners) {
            //noinspection SimplifiableIfStatement
            if (getLocalImageFoldersListener == null || mGetLocalImageFoldersListeners.contains(getLocalImageFoldersListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetLocalImageFoldersListeners.add(getLocalImageFoldersListener);
        }
    }

    @Override
    public boolean unregisterLocalImageFoldersListener(final GetLocalImageFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalImageFoldersListeners) {
            return mGetLocalImageFoldersListeners.remove(getLocalImageFoldersListener);
        }
    }

    @Override
    public boolean registerLocalImageListener(final GetLocalImageListener getLocalImageListener) {
        synchronized (mGetLocalImageListeners) {
            //noinspection SimplifiableIfStatement
            if (getLocalImageListener == null || mGetLocalImageListeners.contains(getLocalImageListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetLocalImageListeners.add(getLocalImageListener);
        }
    }

    @Override
    public boolean unregisterLocalImageListener(final GetLocalImageListener getLocalImageListener) {
        synchronized (mGetLocalImageListeners) {
            return mGetLocalImageListeners.remove(getLocalImageListener);
        }
    }
    //endregion Register / Unregister listeners

    //region notify listeners
    private void notifyAllLocalImageListenerSucceeded(final List<FileModel> fileModels) {
        synchronized (mGetAllLocalImageListeners) {
            for (int i = 0, size = mGetAllLocalImageListeners.size(); i < size; i++) {
                mGetAllLocalImageListeners.get(i).onAllLocalImageSucceeded(fileModels);
            }
        }
    }

    private void notifyLocalImageFoldersListenerSucceeded(final List<FileModel> fileModels) {
        synchronized (mGetLocalImageFoldersListeners) {
            for (int i = 0, size = mGetLocalImageFoldersListeners.size(); i < size; i++) {
                mGetLocalImageFoldersListeners.get(i).onLocalImageFoldersSucceeded(fileModels);
            }
        }
    }

    private void notifyLocalImageListenerSucceeded(final List<FileModel> fileModels) {
        synchronized (mGetLocalImageListeners) {
            for (int i = 0, size = mGetLocalImageListeners.size(); i < size; i++) {
                mGetLocalImageListeners.get(i).onLocalImageSucceeded(fileModels);
            }
        }
    }

    private void notifyLocalImageListenerFailed() {
        synchronized (mGetLocalImageListeners) {
            for (int i = 0, size = mGetLocalImageListeners.size(); i < size; i++) {
                mGetLocalImageListeners.get(i).onLocalImageFailed();
            }
        }
    }
    //endregion notify listeners

    /**
     * Class used to count.
     * See {@link #getLocalImageFolders(Context, int, String)}.
     * http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java
     * Used to count with a map.
     */
    protected class MutableInt {
        int value = 1; // note that we start at 1 since we're counting

        public void increment() {
            ++value;
        }
    }
}
