package com.mercandalli.android.apps.files.file.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.library.base.java.FileUtils.getNameFromPath;
import static com.mercandalli.android.library.base.java.FileUtils.getParentPathFromPath;

/**
 * A MockUp to test.
 */
@SuppressWarnings("unused")
/* package */ class FileAudioManagerMock extends FileAudioManagerImpl {

    private static final String TAG = "FileAudioManagerMockImp";
    private static final String LIKE = " LIKE ?";

    public FileAudioManagerMock(
            final Context contextApp,
            final FileLocalProviderManager fileLocalProviderManager,
            final FileManager fileManager) {
        super(contextApp, fileLocalProviderManager, fileManager);
    }

    /**
     * Delay the call.
     */
    @Override
    @SuppressLint("NewApi")
    public void getLocalMusicFolders() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyLocalMusicFoldersListenerFailed();
            return;
        }

        if (!mCacheLocalMusicFolders.isEmpty()) {
            notifyLocalMusicFoldersListenerSucceeded(mCacheLocalMusicFolders, false);
            return;
        }
        if (mIsGetLocalMusicFoldersLaunched) {
            return;
        }
        mIsGetLocalMusicFoldersLaunched = true;

        new Thread() {
            @Override
            public void run() {

                // Used to count the number of music inside.
                final Map<String, MutableInt> directories = new HashMap<>();

                final String[] PROJECTION = new String[]{MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                final StringBuilder selection = new StringBuilder("( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection.append(" OR " + MediaStore.Files.FileColumns.DATA + LIKE);
                    searchArray.add("%" + end);
                }
                selection.append(" )");

                final Cursor cursor = mContextApp.getContentResolver().query(allSongsUri, PROJECTION, selection.toString(), searchArray.toArray(new String[searchArray.size()]), null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            final String parentPath = getParentPathFromPath(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
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

                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "getLocalMusicFolders: ", e);
                }

                notifyLocalMusicFoldersListenerSucceeded(result, true);
            }
        }.start();
    }
}
