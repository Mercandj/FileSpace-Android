package com.mercandalli.android.apps.files.file.local.provider;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public abstract class FileLocalProviderManager {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            LOADING_ERROR_ALREADY_LAUNCHED,
            LOADING_ERROR_ANDROID_API})
    public @interface LoadingError {
    }

    public static final int LOADING_ERROR_ALREADY_LAUNCHED = -1;
    public static final int LOADING_ERROR_ANDROID_API = -2;

    protected final List<FileProviderListener> mFileProviderListeners = new ArrayList<>();

    public abstract void load();

    public abstract void load(@Nullable final FileProviderListener fileProviderListener);

    public abstract boolean isLoaded();

    @NonNull
    public abstract List<String> getFilePaths();

    @NonNull
    public abstract List<String> getFileAudioPaths();

    @NonNull
    public abstract List<String> getFileImagePaths();

    public boolean registerFileProviderListener(final FileProviderListener fileProviderListener) {
        synchronized (mFileProviderListeners) {
            //noinspection SimplifiableIfStatement
            if (fileProviderListener == null || mFileProviderListeners.contains(fileProviderListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mFileProviderListeners.add(fileProviderListener);
        }
    }

    public boolean unregisterFileProviderListener(final FileProviderListener fileProviderListener) {
        synchronized (mFileProviderListeners) {
            return mFileProviderListeners.remove(fileProviderListener);
        }
    }

    public static abstract class FileProviderListener {

        /**
         * Audio, image.
         */
        protected void onFileProviderAllBasicLoaded(final List<String> filePaths) {
            // To override
        }

        /**
         * Audio.
         */
        protected void onFileProviderAudioLoaded(final List<String> fileAudioPaths) {
            // To override
        }

        /**
         * Image.
         */
        protected void onFileProviderImageLoaded(final List<String> fileImagePaths) {
            // To override
        }

        /**
         * Load failed.
         */
        protected void onFileProviderFailed(@LoadingError final int error) {
            // To override
        }
    }
}
