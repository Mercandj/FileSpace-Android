package com.mercandalli.android.apps.files.file.audio.playlist;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Save and load audio playlist.
 */
public interface AudioPlayListStorage {

    void save(final @NonNull List<String> paths);

    @NonNull
    List<String> load();
}
