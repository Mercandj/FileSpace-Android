package com.mercandalli.android.apps.files.file.audio.playlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * The playlist manager.
 */
public abstract class AudioPlayListManager {

    @Nullable
    private static AudioPlayListManager sInstance;

    @NonNull
    public static AudioPlayListManager getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new AudioPlayListManagerImpl(context);
        }
        return sInstance;
    }

    public abstract void add(@NonNull final AudioPlayList audioPlayList);

    @NonNull
    public abstract List<AudioPlayList> get();

    public abstract void getPlayLists();

    public abstract boolean addGetPlayListsListener(GetPlayListsListener getPlayListsListener);

    public abstract boolean removeGetPlayListsListener(GetPlayListsListener getPlayListsListener);

    public interface GetPlayListsListener {
        void onGetPlayListsSucceeded(@NonNull final List<AudioPlayList> audioPlayLists);

        void onGetPlayListsFailed();
    }
}
