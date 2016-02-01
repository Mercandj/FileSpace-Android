package com.mercandalli.android.apps.files.file.audio;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    private final String mName;
    private final List<String> mFilePaths = new ArrayList<>();

    public Artist(final String name, final List<String> filePaths) {
        mName = name;
        mFilePaths.addAll(filePaths);
    }

    public String getName() {
        return mName;
    }

    public List<String> getFilePaths() {
        return mFilePaths;
    }
}
