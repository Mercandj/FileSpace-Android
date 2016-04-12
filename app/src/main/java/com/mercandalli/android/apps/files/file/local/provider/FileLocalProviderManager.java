package com.mercandalli.android.apps.files.file.local.provider;

import android.support.annotation.IntDef;
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

    void getFilePaths(final GetFileListener getFileListener);

    void getFileAudioPaths(final GetFileAudioListener getFileAudioListener);

    void getFileImagePaths(final GetFileImageListener getFileImageListener);

    void getFileVideoPaths(final GetFileVideoListener getFileVideoListener);

    boolean registerFileProviderListener(final FileProviderListener fileProviderListener);

    boolean unregisterFileProviderListener(final FileProviderListener fileProviderListener);

    void clearCache();

    interface GetFileListener {
        void onGetFile(final List<String> filePaths);
    }

    interface GetFileAudioListener {
        void onGetFileAudio(final List<String> fileAudioPaths);
    }

    interface GetFileImageListener {
        void onGetFileImage(final List<String> fileImagePaths);
    }

    interface GetFileVideoListener {
        void onGetFileVideo(final List<String> fileVideoPaths);
    }

    abstract class FileProviderListener {

        public void onFileProviderReloadStarted() {
            // To override
        }

        /**
         * Audio, image.
         */
        protected void onFileProviderAllBasicLoaded(final List<String> filePaths) {
            // To override
        }

        /**
         * Audio.
         */
        public void onFileProviderAudioLoaded(final List<String> fileAudioPaths) {
            // To override
        }

        /**
         * Image.
         */
        protected void onFileProviderImageLoaded(final List<String> fileImagePaths) {
            // To override
        }

        /**
         * Video.
         */
        protected void onFileProviderVideoLoaded(final List<String> fileImagePaths) {
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
