package com.mercandalli.android.apps.files.file.image;

import android.content.Context;

import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.file.FileModel;

import java.util.List;

public interface FileImageManager {

    /**
     * Get all local folders that contain image.
     */
    void getLocalImageFolders(final Context context, final int sortMode, final String search);

    boolean registerLocalImageFoldersListener(LocalImageFoldersListener localImageFoldersListener);

    boolean unregisterLocalImageFoldersListener(LocalImageFoldersListener localImageFoldersListener);

    interface LocalImageFoldersListener {
        void onLocalImageFoldersSucceeded(List<FileModel> fileModels);
    }
}
