package com.mercandalli.android.apps.files.file;

import android.content.Context;

import com.mercandalli.android.apps.files.file.audio.FileAudioModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FilePersistenceApi {

    private final List<FileModel> mLocalMusicFolders = new ArrayList<>();
    private final List<FileAudioModel> mLocalMusics = new ArrayList<>();

    public FileModel get(Context context, int id) {
        return null;
    }

    public void add(Context context, FileModel entity) {

    }

    public FileModel update(Context context, FileModel entity) {
        return null;
    }

    public boolean delete(Context context, FileModel entity) {
        return false;
    }

    public long count(Context context) {
        return 0;
    }

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
