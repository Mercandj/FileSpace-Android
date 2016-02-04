package com.mercandalli.android.apps.files.file.audio.artist;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    protected final String mUuid;
    protected final String mName;
    protected final List<String> mFilePaths = new ArrayList<>();

    public Artist(final String uuid, final String name) {
        mUuid = uuid;
        mName = name;
    }

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