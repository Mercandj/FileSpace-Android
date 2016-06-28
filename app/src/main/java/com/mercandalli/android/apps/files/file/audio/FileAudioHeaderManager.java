package com.mercandalli.android.apps.files.file.audio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.FileModelCardHeaderItem;

import java.util.ArrayList;
import java.util.List;

/* package */ class FileAudioHeaderManager {

    @Nullable
    private static FileAudioHeaderManager sInstance;

    @NonNull
    public static FileAudioHeaderManager getInstance() {
        if (sInstance == null) {
            sInstance = new FileAudioHeaderManager();
        }
        return sInstance;
    }

    @NonNull
    private final List<FileModelCardHeaderItem> mHeaderIds = new ArrayList<>();

    private FileAudioHeaderManager() {
        mHeaderIds.clear();
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_folder, true));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_playlist, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_recent, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_artist, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_album, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_all, false));
    }

    public List<FileModelCardHeaderItem> getHeaderIds() {
        return new ArrayList<>(mHeaderIds);
    }

    public void setHeaderIds(@NonNull final List<FileModelCardHeaderItem> headerIds) {
        mHeaderIds.clear();
        mHeaderIds.addAll(headerIds);
    }
}
