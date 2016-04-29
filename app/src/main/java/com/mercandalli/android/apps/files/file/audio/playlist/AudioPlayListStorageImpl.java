package com.mercandalli.android.apps.files.file.audio.playlist;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 */
/* package */
class AudioPlayListStorageImpl implements AudioPlayListStorage {

    @Override
    public void save(@NonNull final List<String> paths) {

    }

    @NonNull
    @Override
    public List<String> load() {
        return new ArrayList<>();
    }
}
