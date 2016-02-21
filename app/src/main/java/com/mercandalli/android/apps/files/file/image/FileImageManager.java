package com.mercandalli.android.apps.files.file.image;

import com.mercandalli.android.apps.files.file.FileModel;

import java.util.List;

public interface FileImageManager {

    /**
     * Get all the {@link FileModel} image in the device.
     */
    void getAllLocalImage();

    /**
     * Get all local folders that contain image.
     */
    void getLocalImageFolders();

    /**
     * Get all the {@link FileModel} image in a folder.
     */
    void getLocalImage(final FileModel fileModelDirectParent);

    boolean registerAllLocalImageListener(GetAllLocalImageListener getAllLocalImageListener);

    boolean unregisterAllLocalImageListener(GetAllLocalImageListener getAllLocalImageListener);

    boolean registerLocalImageFoldersListener(GetLocalImageFoldersListener getLocalImageFoldersListener);

    boolean unregisterLocalImageFoldersListener(GetLocalImageFoldersListener getLocalImageFoldersListener);

    boolean registerLocalImageListener(GetLocalImageListener getLocalImageListener);

    boolean unregisterLocalImageListener(GetLocalImageListener getLocalImageListener);

    interface GetAllLocalImageListener {

        /**
         * Called when the call of {@link #getAllLocalImage()} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onAllLocalImageSucceeded(List<FileModel> fileModels);

        void onAllLocalImageFailed();
    }

    interface GetLocalImageFoldersListener {

        /**
         * Called when the call of {@link #getLocalImageFolders()} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onLocalImageFoldersSucceeded(List<FileModel> fileModels);

        void onLocalImageFoldersFailed();
    }

    interface GetLocalImageListener {

        /**
         * Called when the call of {@link #getLocalImage(FileModel)} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onLocalImageSucceeded(List<FileModel> fileModels);

        void onLocalImageFailed();
    }
}
