package com.mercandalli.android.apps.files.file.audio;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private final String mUuid;
    private final String mName;
    private final List<String> mFilePaths = new ArrayList<>();

    public Album(final String uuid, final String name) {
        mUuid = uuid;
        mName = name;
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
