package com.mercandalli.android.apps.files.file.audio;

import android.support.annotation.NonNull;

import com.mercandalli.android.library.base.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

/* package */ class Album {

    protected final int mAlbumId;
    @NonNull
    protected final String mName;
    protected final int mNumberOfSongs;
    @NonNull
    protected final List<String> mFilePaths = new ArrayList<>();

    public Album(
            final int albumId,
            @NonNull final String name,
            final int numberOfSongs) {
        Preconditions.checkNotNull(name);
        mAlbumId = albumId;
        mName = name;
        mNumberOfSongs = numberOfSongs;
    }

    public int getId() {
        return mAlbumId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public int getNumberOfSongs() {
        return Math.max(mNumberOfSongs, mFilePaths.size());
    }

    @NonNull
    public List<String> getFilePaths() {
        return mFilePaths;
    }

    public boolean addFilePath(final String filePath) {
        return mFilePaths.add(filePath);
    }
}
