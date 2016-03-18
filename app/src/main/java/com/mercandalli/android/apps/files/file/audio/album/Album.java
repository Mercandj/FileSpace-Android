package com.mercandalli.android.apps.files.file.audio.album;

import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.precondition.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class Album {

    protected final String mUuid;
    protected final String mName;
    protected final List<String> mFilePaths = new ArrayList<>();

    public Album(final String uuid, final String name) {
        Preconditions.checkNotNull(uuid);
        mUuid = uuid;
        mName = name;
    }

    @Nullable
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
