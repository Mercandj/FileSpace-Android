package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;

import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.file.FileModel;

import java.util.List;

/**
 * The {@link FileModel} Manager abstract class.
 */
public abstract class FileAudioManager {

    /**
     * Get all the {@link FileAudioModel} in the device.
     */
    public abstract void getAllLocalMusic(final Context context, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback);

    /**
     * Get all the {@link FileAudioModel} in a folder.
     */
    public abstract void getLocalMusic(final Context context, final FileModel fileModelDirectParent, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback);

    /**
     * Get all local folders that contain music.
     */
    public abstract void getLocalMusicFolders(final Context context, final int sortMode, final String search, final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Class used to count.
     * See {@link #getLocalMusicFolders(Context, int, String, ResultCallback)}.
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
