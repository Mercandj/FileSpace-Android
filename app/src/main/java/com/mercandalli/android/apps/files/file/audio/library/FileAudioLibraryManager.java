package com.mercandalli.android.apps.files.file.audio.library;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class FileAudioLibraryManager {

    private static final String LIKE = " LIKE ?";

    private final Handler mUiHandler;
    private final Thread mUiThread;
    private final Context mContextApp;

    private final List<String> mFileAudioPaths;
    private final List<LoadListener> mLoadListeners;

    /**
     * The manager constructor.
     *
     * @param contextApp The {@link Context} of this application.
     */
    public FileAudioLibraryManager(final Context contextApp) {
        Preconditions.checkNotNull(contextApp);
        mContextApp = contextApp;

        final Looper mainLooper = Looper.getMainLooper();
        mUiHandler = new Handler(mainLooper);
        mUiThread = mainLooper.getThread();
        mFileAudioPaths = new ArrayList<>();
        mLoadListeners = new ArrayList<>();
    }


    public void load() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                final List<String> paths = new ArrayList<>();

                final String[] PROJECTION = {MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection += " OR " + MediaStore.Files.FileColumns.DATA + LIKE;
                    searchArray.add("%" + end);
                }
                selection += " )";

                final Cursor cursor = mContextApp.getContentResolver().query(allSongsUri, PROJECTION, selection, searchArray.toArray(new String[searchArray.size()]), null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            paths.add(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                notifyLoadSucceeded(paths);
            }
        }.start();
    }

    public boolean registerOnMusicUpdateListener(LoadListener loadListener) {
        synchronized (mLoadListeners) {
            //noinspection SimplifiableIfStatement
            if (loadListener == null || mLoadListeners.contains(loadListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mLoadListeners.add(loadListener);
        }
    }

    public boolean unregisterOnMusicUpdateListener(LoadListener loadListener) {
        synchronized (mLoadListeners) {
            return mLoadListeners.remove(loadListener);
        }
    }

    private void notifyLoadSucceeded(final List<String> paths) {
        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyLoadSucceeded(paths);
                }
            });
            return;
        }
        mFileAudioPaths.clear();
        mFileAudioPaths.addAll(paths);

        synchronized (mLoadListeners) {
            for (int i = 0, size = mLoadListeners.size(); i < size; i++) {
                mLoadListeners.get(i).onFileAudioLibraryLoadSucceeded(paths);
            }
        }
    }

    interface LoadListener {

        void onFileAudioLibraryLoadSucceeded(final List<String> paths);

        void onFileAudioLibraryLoadFailed();
    }

}
