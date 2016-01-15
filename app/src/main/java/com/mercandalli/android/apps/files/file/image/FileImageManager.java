package com.mercandalli.android.apps.files.file.image;

import android.content.Context;

import com.mercandalli.android.apps.files.file.FileModel;

import java.util.List;

public interface FileImageManager {

    /**
     * Get all local folders that contain image.
     */
    void getLocalImageFolders(final Context context, final int sortMode, final String search);

    boolean registerLocalImageFoldersListener(GetLocalImageFoldersListener getLocalImageFoldersListener);

    boolean unregisterLocalImageFoldersListener(GetLocalImageFoldersListener getLocalImageFoldersListener);

    /**
     * Get all the {@link FileModel} image in a folder.
     */
    void getLocalImage(final Context context, final FileModel fileModelDirectParent, final int sortMode, final String search);

    boolean registerLocalImageListener(GetLocalImageListener getLocalImageListener);

    boolean unregisterLocalImageListener(GetLocalImageListener getLocalImageListener);

    interface GetLocalImageFoldersListener {

        /**
         * Called when the call of {@link #getLocalImageFolders(Context, int, String)} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onLocalImageFoldersSucceeded(List<FileModel> fileModels);

        void onLocalImageFoldersFailed();
    }

    interface GetLocalImageListener {

        /**
         * Called when the call of {@link #getLocalImage(Context, FileModel, int, String)} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onLocalImageSucceeded(List<FileModel> fileModels);

        void onLocalImageFailed();
    }
}
