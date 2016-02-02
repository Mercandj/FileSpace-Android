package com.mercandalli.android.apps.files.file.audio;

import java.util.ArrayList;
import java.util.List;

public class AlbumArtist {

    protected final String mUuid;
    protected final String mName;
    protected final List<String> mFilePaths = new ArrayList<>();

    public AlbumArtist(final String uuid, final String name) {
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
