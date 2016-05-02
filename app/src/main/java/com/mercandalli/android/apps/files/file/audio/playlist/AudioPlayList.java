package com.mercandalli.android.apps.files.file.audio.playlist;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayList {

    private final int mId;

    @NonNull
    private final String mName;

    @NonNull
    private List<String> mFilePaths = new ArrayList<>();

    public AudioPlayList(@NonNull final String name, @NonNull final List<String> filePaths) {
        mId = 0;
        mName = name;
        mFilePaths.clear();
        mFilePaths.addAll(filePaths);
    }

    public AudioPlayList(@NonNull final String name) {
        mId = 0;
        mName = name;
    }

    public AudioPlayList(final int id, @NonNull final String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public List<String> getFilePaths() {
        return new ArrayList<>(mFilePaths);
    }
}
