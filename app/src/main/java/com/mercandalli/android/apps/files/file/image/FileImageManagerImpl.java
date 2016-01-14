package com.mercandalli.android.apps.files.file.image;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.apps.files.file.FileUtils.getNameFromPath;

public class FileImageManagerImpl implements FileImageManager {

    private static final String LIKE = " LIKE ?";
    private final List<LocalImageFoldersListener> mLocalImageFoldersListeners = new ArrayList<>();

    /* Cache */
    private final List<FileModel> mCacheLocalImagesFolders = new ArrayList<>();

    public FileImageManagerImpl(Application application) {

    }

    @Override
    public void getLocalImageFolders(final Context context, final int sortMode, final String search) {
        if (!mCacheLocalImagesFolders.isEmpty()) {
            notifyLocalImageFoldersListenerSucceeded(mCacheLocalImagesFolders);
            return;
        }

        new AsyncTask<Void, Void, List<FileModel>>() {
            @Override
            protected List<FileModel> doInBackground(Void... params) {
                // Used to count the number of music inside.
                final Map<String, MutableInt> directories = new HashMap<>();

                final String[] PROJECTION = new String[]{MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

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
                notifyLocalImageFoldersListenerSucceeded(fileModels);
                mCacheLocalImagesFolders.clear();
                mCacheLocalImagesFolders.addAll(fileModels);
                super.onPostExecute(fileModels);
            }
        }.execute();
    }

    @Override
    public boolean registerLocalImageFoldersListener(final LocalImageFoldersListener localImageFoldersListener) {
        synchronized (mLocalImageFoldersListeners) {
            //noinspection SimplifiableIfStatement
            if (localImageFoldersListener == null || mLocalImageFoldersListeners.contains(localImageFoldersListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }

            return mLocalImageFoldersListeners.add(localImageFoldersListener);
        }
    }

    @Override
    public boolean unregisterLocalImageFoldersListener(LocalImageFoldersListener localImageFoldersListener) {
        synchronized (mLocalImageFoldersListeners) {
            return mLocalImageFoldersListeners.remove(localImageFoldersListener);
        }
    }

    private void notifyLocalImageFoldersListenerSucceeded(List<FileModel> fileModels) {
        synchronized (mLocalImageFoldersListeners) {
            for (int i = 0, size = mLocalImageFoldersListeners.size(); i < size; i++) {
                mLocalImageFoldersListeners.get(i).onLocalImageFoldersSucceeded(fileModels);
            }
        }
    }

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
