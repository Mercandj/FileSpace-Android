package com.mercandalli.android.apps.files.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;
import com.mercandalli.android.apps.files.file.local.FileLocalApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.apps.files.file.FileUtils.getNameFromPath;

/**
 * A MockUp to test.
 */
public class FileManagerMockImpl extends FileManagerImpl {

    public FileManagerMockImpl(Context contextApp, FileOnlineApi fileOnlineApi, FileLocalApi fileLocalApi, FilePersistenceApi filePersistenceApi) {
        super(contextApp, fileOnlineApi, fileLocalApi, filePersistenceApi);
    }

    /**
     * Delay the call.
     */
    @Override
    public void getLocalMusicFolders(final Context context, final int sortMode, final String search, final ResultCallback<List<FileModel>> resultCallback) {
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
                    selection += " OR " + MediaStore.Files.FileColumns.DATA + " LIKE ?";
                    searchArray.add("%" + end);
                }
                selection += " )";

                if (search != null && !search.isEmpty()) {
                    searchArray.add("%" + search + "%");
                    selection += " AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?";
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

                // WAIT
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.e(getClass().getName(), "Exception", e);
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<FileModel> fileModels) {
                resultCallback.success(fileModels);
                super.onPostExecute(fileModels);
            }
        }.execute();
    }
}
