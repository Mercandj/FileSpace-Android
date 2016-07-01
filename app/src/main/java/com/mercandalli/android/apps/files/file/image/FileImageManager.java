package com.mercandalli.android.apps.files.file.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.file.FileModel;

import java.util.List;

public abstract class FileImageManager {

    @Nullable
    private static FileImageManager sInstance;

    @NonNull
    public static FileImageManager getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new FileImageManagerImpl(context);
        }
        return sInstance;
    }

    /**
     * Get all the {@link FileModel} image in the device.
     */
    public abstract void getAllLocalImage();

    /**
     * Get all local folders that contain image.
     */
    public abstract void getLocalImageFolders();

    /**
     * Get all the {@link FileModel} image in a folder.
     */
    public abstract void getLocalImage(final FileModel fileModelDirectParent);

    public abstract void clearCache();

    public abstract boolean registerAllLocalImageListener(GetAllLocalImageListener getAllLocalImageListener);

    public abstract boolean unregisterAllLocalImageListener(GetAllLocalImageListener getAllLocalImageListener);

    public abstract boolean registerLocalImageFoldersListener(GetLocalImageFoldersListener getLocalImageFoldersListener);

    public abstract boolean unregisterLocalImageFoldersListener(GetLocalImageFoldersListener getLocalImageFoldersListener);

    public abstract boolean registerLocalImageListener(GetLocalImageListener getLocalImageListener);

    public abstract boolean unregisterLocalImageListener(GetLocalImageListener getLocalImageListener);

    public interface GetAllLocalImageListener {

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
