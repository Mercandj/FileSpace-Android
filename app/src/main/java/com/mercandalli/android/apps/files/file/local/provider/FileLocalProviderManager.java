package com.mercandalli.android.apps.files.file.local.provider;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface FileLocalProviderManager {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            LOADING_ERROR_ALREADY_LAUNCHED,
            LOADING_ERROR_ANDROID_API})
    @interface LoadingError {
    }

    int LOADING_ERROR_ALREADY_LAUNCHED = -1;
    int LOADING_ERROR_ANDROID_API = -2;

    void load();

    void load(@Nullable final FileProviderListener fileProviderListener);

    @NonNull
    List<String> getFilePaths();
    void getFilePaths(final GetFilePathsListener getFilePathsListener);
    void removeGetFilePathsListener(final GetFilePathsListener getFilePathsListener);

    void getFileAudioPaths(final GetFileAudioListener getFileAudioListener);
    void removeGetFileAudioListener(final GetFileAudioListener getFileAudioListener);

    void getFileImagePaths(final GetFileImageListener getFileImageListener);
    void removeGetFileImageListener(final GetFileImageListener getFileImageListener);

    void getFileVideoPaths(final GetFileVideoListener getFileVideoListener);
    void removeGetFileVideoListener(final GetFileVideoListener getFileVideoListener);

    boolean registerFileProviderListener(final FileProviderListener fileProviderListener);

    boolean unregisterFileProviderListener(final FileProviderListener fileProviderListener);

    void clearCache();

    interface GetFilePathsListener {
        void onGetFile(@NonNull final List<String> filePaths);
    }

    interface GetFileAudioListener {
        void onGetFileAudio(@NonNull final List<String> fileAudioPaths);
    }

    interface GetFileImageListener {
        void onGetFileImage(@NonNull final List<String> fileImagePaths);
    }

    interface GetFileVideoListener {
        void onGetFileVideo(@NonNull final List<String> fileVideoPaths);
    }

    abstract class FileProviderListener {

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
