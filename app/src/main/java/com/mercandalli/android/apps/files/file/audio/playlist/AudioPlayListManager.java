package com.mercandalli.android.apps.files.file.audio.playlist;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * The playlist manager.
 */
public interface AudioPlayListManager {

    void add(@NonNull final AudioPlayList audioPlayList);

    @NonNull
    List<AudioPlayList> get();

    void getPlayLists();

    boolean addGetPlayListsListener(GetPlayListsListener getPlayListsListener);

    boolean removeGetPlayListsListener(GetPlayListsListener getPlayListsListener);

    interface GetPlayListsListener {
        void onGetPlayListsSucceeded(@NonNull final List<AudioPlayList> audioPlayLists);

        void onGetPlayListsFailed();
    }
}
