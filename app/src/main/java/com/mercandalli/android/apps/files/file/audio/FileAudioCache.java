package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;

import com.mercandalli.android.apps.files.file.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileAudioCache {

    private final List<FileModel> mLocalMusicFolders = new ArrayList<>();
    private final List<FileAudioModel> mLocalMusics = new ArrayList<>();

    public void setLocalMusicFolders(final List<FileModel> fileModels) {
        mLocalMusicFolders.clear();
        mLocalMusicFolders.addAll(fileModels);
    }

    public List<FileModel> getLocalMusicFolders() {
        return mLocalMusicFolders;
    }

    public void setLocalMusics(final List<FileAudioModel> localMusics) {
        mLocalMusics.clear();
        mLocalMusics.addAll(localMusics);
    }

    public List<FileAudioModel> getLocalMusics() {
        return mLocalMusics;
    }
}
