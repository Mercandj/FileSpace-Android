package com.mercandalli.android.apps.files.file.audio.artist;

import android.support.annotation.NonNull;

import com.mercandalli.android.library.base.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    @NonNull
    protected final String mUuid;
    protected final String mName;

    @NonNull
    protected final List<String> mFilePaths = new ArrayList<>();

    public Artist(final String uuid, final String name) {
        Preconditions.checkNotNull(uuid);
        mUuid = uuid;
        mName = name;
    }

    @NonNull
    public String getUuid() {
        return mUuid;
    }

    public String getName() {
        return mName;
    }

    public List<String> getFilePaths() {
        return mFilePaths;
    }

    public boolean addFilePath(final String filePath) {
        return mFilePaths.add(filePath);
    }
}