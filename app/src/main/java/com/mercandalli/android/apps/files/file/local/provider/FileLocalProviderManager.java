package com.mercandalli.android.apps.files.file.local.provider;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public abstract class FileLocalProviderManager {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            LOADING_ERROR_ALREADY_LAUNCHED,
            LOADING_ERROR_ANDROID_API})
    @interface LoadingError {
    }

    public static final int LOADING_ERROR_ALREADY_LAUNCHED = -1;
    public static final int LOADING_ERROR_ANDROID_API = -2;

    @Nullable
    private static FileLocalProviderManager sInstance;

    @NonNull
    public static FileLocalProviderManager getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new FileLocalProviderManagerImpl(context);
        }
        return sInstance;
    }

    public abstract void load();

    public abstract void load(@Nullable final FileProviderListener fileProviderListener);

    @NonNull
    public abstract List<String> getFilePaths();
    public abstract  void getFilePaths(final GetFilePathsListener getFilePathsListener);
    public abstract void removeGetFilePathsListener(final GetFilePathsListener getFilePathsListener);

    public abstract void getFileAudioPaths(final GetFileAudioListener getFileAudioListener);
    public abstract void removeGetFileAudioListener(final GetFileAudioListener getFileAudioListener);

    public abstract void getFileImagePaths(final GetFileImageListener getFileImageListener);
    public abstract void removeGetFileImageListener(final GetFileImageListener getFileImageListener);

    public abstract void getFileVideoPaths(final GetFileVideoListener getFileVideoListener);
    public abstract void removeGetFileVideoListener(final GetFileVideoListener getFileVideoListener);

    public abstract boolean registerFileProviderListener(final FileProviderListener fileProviderListener);

    public abstract boolean unregisterFileProviderListener(final FileProviderListener fileProviderListener);

    public abstract void clearCache();

    public interface GetFilePathsListener {
        void onGetFile(@NonNull final List<String> filePaths);
    }

    public interface GetFileAudioListener {
        void onGetFileAudio(@NonNull final List<String> fileAudioPaths);
    }

    public interface GetFileImageListener {
        void onGetFileImage(@NonNull final List<String> fileImagePaths);
    }

    public interface GetFileVideoListener {
        void onGetFileVideo(@NonNull final List<String> fileVideoPaths);
    }

    public static abstract class FileProviderListener {

        public void onFileProviderReloadStarted() {
            // To override
        }

        /**
         * Audio, image.
         */
        protected void onFileProviderAllBasicLoaded(@NonNull final List<String> filePaths) {
            // To override
        }

        /**
         * Audio.
         */
        public void onFileProviderAudioLoaded(@NonNull final List<String> fileAudioPaths) {
            // To override
        }

        /**
         * Image.
         */
        protected void onFileProviderImageLoaded(@NonNull final List<String> fileImagePaths) {
            // To override
        }

        /**
         * Video.
         */
        protected void onFileProviderVideoLoaded(@NonNull final List<String> fileImagePaths) {
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
