package com.mercandalli.android.apps.files.file.audio.playlist;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * The playlist manager.
 */
public interface AudioPlayListManager {

    void getPlayLists();

    boolean addGetPlayListsListener(GetPlayListsListener getPlayListsListener);

    boolean removeGetPlayListsListener(GetPlayListsListener getPlayListsListener);

    interface GetPlayListsListener {

        /**
         * Called when the call of {@link #getPlayLists()} succeeded.
         *
         * @param audioPlayLists the {@link List} of result.
         */
        void onGetPlayListsSucceeded(@NonNull final List<AudioPlayList> audioPlayLists);

        void onGetPlayListsFailed();
    }
}
