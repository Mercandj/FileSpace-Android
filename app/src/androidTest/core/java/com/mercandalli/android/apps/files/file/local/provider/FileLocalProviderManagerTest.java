package com.mercandalli.android.apps.files.file.local.provider;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.util.Log;

import java.util.List;

/**
 * Block espresso with {@link CountingIdlingResource} during load.
 */
/* package */
class FileLocalProviderManagerTest implements FileLocalProviderManager {

    private static final String TAG = "FileLocalProvManTest";

    private final FileLocalProviderManager mFileLocalProviderManager;
    private final CountingIdlingResource mCountingIdlingResource;

    /**
     * The manager constructor.
     *
     * @param contextApp The {@link Context} of this application.
     */
    public FileLocalProviderManagerTest(Context contextApp) {
        mFileLocalProviderManager = new FileLocalProviderManagerImpl(contextApp);
        mCountingIdlingResource = new CountingIdlingResource(TAG + "#load", true);
        Espresso.registerIdlingResources(mCountingIdlingResource);
        mFileLocalProviderManager.registerFileProviderListener(new FileProviderListener() {
            @Override
            public void onFileProviderReloadStarted() {
                super.onFileProviderReloadStarted();
            }

            @Override
            protected void onFileProviderAllBasicLoaded(List<String> filePaths) {
                decrement();
                super.onFileProviderAllBasicLoaded(filePaths);
            }

            @Override
            public void onFileProviderAudioLoaded(List<String> fileAudioPaths) {
                super.onFileProviderAudioLoaded(fileAudioPaths);
            }

            @Override
            protected void onFileProviderImageLoaded(List<String> fileImagePaths) {
                super.onFileProviderImageLoaded(fileImagePaths);
            }

            @Override
            protected void onFileProviderFailed(@LoadingError int error) {
                decrement();
                super.onFileProviderFailed(error);
            }

            private void decrement() {
                mCountingIdlingResource.decrement();
                Log.d(TAG, "decrement");
            }
        });
    }

    @Override
    public void load() {
        if (mCountingIdlingResource.isIdleNow()) {
            mCountingIdlingResource.increment();
            Log.d(TAG, "increment load");
        }
        mFileLocalProviderManager.load();
    }

    @Override
    public void load(@Nullable FileProviderListener fileProviderListener) {
        if (mCountingIdlingResource.isIdleNow()) {
            mCountingIdlingResource.increment();
            Log.d(TAG, "increment load");
        }
        mFileLocalProviderManager.load(fileProviderListener);
    }

    @Override
    public void getFilePaths(GetFileListener getFileListener) {
        mFileLocalProviderManager.getFilePaths(getFileListener);
    }

    @Override
    public void getFileAudioPaths(GetFileAudioListener getFileAudioListener) {
        mFileLocalProviderManager.getFileAudioPaths(getFileAudioListener);
    }

    @Override
    public void getFileImagePaths(GetFileImageListener getFileImageListener) {
        mFileLocalProviderManager.getFileImagePaths(getFileImageListener);
    }

    @Override
    public void getFileVideoPaths(final GetFileVideoListener getFileVideoListener) {
        mFileLocalProviderManager.getFileVideoPaths(getFileVideoListener);
    }

    @Override
    public boolean registerFileProviderListener(FileProviderListener fileProviderListener) {
        return mFileLocalProviderManager.registerFileProviderListener(fileProviderListener);
    }

    @Override
    public boolean unregisterFileProviderListener(FileProviderListener fileProviderListener) {
        return mFileLocalProviderManager.unregisterFileProviderListener(fileProviderListener);
    }

    @Override
    public void clearCache() {
        mFileLocalProviderManager.clearCache();
    }
}
