package com.mercandalli.android.apps.files.file.audio.playlist;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayList {

    @NonNull
    private List<String> mFilePaths = new ArrayList<>();

    public AudioPlayList(@NonNull final List<String> filePaths) {
        mFilePaths.addAll(filePaths);
    }

    @NonNull
    public List<String> getFilePaths() {
        return new ArrayList<>(mFilePaths);
    }
}
