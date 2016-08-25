package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/* package */ final class AlbumManager implements FileAudioManager.GetAllLocalMusicListener {

    @Nullable
    private static AlbumManager sInstance;

    @NonNull
    /* package */ static AlbumManager getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new AlbumManager(context);
        }
        return sInstance;
    }

    @Nullable
    private List<FileAudioModel> mFileAudioModelList;

    private AlbumManager(@NonNull final Context context) {
        final FileAudioManager fileAudioManager = FileAudioManagerNotifier.getInstance(context);
        fileAudioManager.addGetAllLocalMusicListener(this);
        fileAudioManager.getAllLocalMusic();
    }

    /* package */ void onAlbumCardClicked(@NonNull final Album album) {

    }

    @Override
    public void onAllLocalMusicSucceeded(final List<FileAudioModel> fileModels) {
        mFileAudioModelList = new ArrayList<>(fileModels);
    }

    @Override
    public void onAllLocalMusicFailed() {

    }

    public interface OnAlbumClickedListener {

        boolean onAlbumClicked(@NonNull final Album album);

        boolean onAlbumClickedLoadEnded(@NonNull final Album album, final List<FileAudioModel> fileModels);
    }
}
