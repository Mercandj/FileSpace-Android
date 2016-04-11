package com.mercandalli.android.apps.files.file.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;
import com.mercandalli.android.library.baselibrary.precondition.Preconditions;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.apps.files.file.FileUtils.getNameFromPath;

/**
 * {@inheritDoc}
 */
/* package */
class FileImageManagerImpl implements FileImageManager {

    private static final String LIKE = " LIKE ?";

    private final Context mContextApp;
    protected final FileLocalProviderManager mFileLocalProviderManager;

    private final List<GetAllLocalImageListener> mGetAllLocalImageListeners = new ArrayList<>();
    private final List<GetLocalImageFoldersListener> mGetLocalImageFoldersListeners = new ArrayList<>();

    private final List<GetLocalImageListener> mGetLocalImageListeners = new ArrayList<>();
    /* Cache */
    private final List<FileModel> mCacheGetAllLocalImage = new ArrayList<>();
    private final List<FileModel> mCacheGetLocalImagesFolders = new ArrayList<>();

    private boolean mIsGetAllLocalImageLaunched;
    private boolean mIsGetLocalImageFoldersLaunched;

    public FileImageManagerImpl(final Context contextApp, final FileLocalProviderManager fileLocalProviderManager) {
        Preconditions.checkNotNull(contextApp);
        Preconditions.checkNotNull(fileLocalProviderManager);
        mContextApp = contextApp;
        mFileLocalProviderManager = fileLocalProviderManager;
    }

    @Override
    @SuppressLint("NewApi")
    public void getAllLocalImage() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyLocalImageFoldersListenerFailed();
            return;
        }

        if (!mCacheGetAllLocalImage.isEmpty()) {
            notifyAllLocalImageListenerSucceeded(mCacheGetAllLocalImage);
            return;
        }
        if (mIsGetAllLocalImageLaunched) {
            return;
        }
        mIsGetAllLocalImageLaunched = true;

        mFileLocalProviderManager.getFileImagePaths(new FileLocalProviderManager.GetFileImageListener() {
            @Override
            public void onGetFileImage(final List<String> fileImagePaths) {

                final List<FileModel> fileModels = new ArrayList<>();
                for(final String path:fileImagePaths) {
                    if (!path.startsWith("/storage/emulated/0/Android/")) {
                        final File file = new File(path);
                        if (file.exists() && !file.isDirectory()) {
                            fileModels.add(new FileModel.FileModelBuilder().file(file).build());
                        }
                    }
                }

                notifyAllLocalImageListenerSucceeded(fileModels);
                mCacheGetAllLocalImage.clear();
                mCacheGetAllLocalImage.addAll(fileModels);
                mIsGetAllLocalImageLaunched = false;
            }
        });
    }

    //region getLocalImageFolders
    @Override
    @SuppressLint("NewApi")
    public void getLocalImageFolders() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            notifyLocalImageFoldersListenerFailed();
            return;
        }

        if (!mCacheGetLocalImagesFolders.isEmpty()) {
            notifyLocalImageFoldersListenerSucceeded(mCacheGetLocalImagesFolders);
            return;
        }
        if (mIsGetLocalImageFoldersLaunched) {
            return;
        }
        mIsGetLocalImageFoldersLaunched = true;

        mFileLocalProviderManager.getFileImagePaths(new FileLocalProviderManager.GetFileImageListener() {
            @Override
            public void onGetFileImage(final List<String> fileImagePaths) {
                // Used to count the number of music inside.
                final Map<String, MutableInt> directories = new HashMap<>();

                for(final String path:fileImagePaths) {

                    final String parentPath = FileUtils.getParentPathFromPath(path);
                    final MutableInt count = directories.get(parentPath);
                    if (count == null) {
                        directories.put(parentPath, new MutableInt());
                    } else {
                        count.increment();
                    }
                }

                final List<FileModel> result = new ArrayList<>();
                for (String path : directories.keySet()) {
                    if (!path.startsWith("/storage/emulated/0/Android/")) {
                        result.add(new FileModel.FileModelBuilder()
                                .id(path.hashCode())
                                .url(path)
                                .name(getNameFromPath(path))
                                .isDirectory(true)
                                .countAudio(directories.get(path).value)
                                .isOnline(false)
                                .build());
                    }
                }

                notifyLocalImageFoldersListenerSucceeded(result);
                mCacheGetLocalImagesFolders.clear();
                mCacheGetLocalImagesFolders.addAll(result);
                mIsGetLocalImageFoldersLaunched = false;
            }
        });
    }
    //endregion getLocalImageFolders

    //region getLocalImage
    @Override
    public void getLocalImage(final FileModel fileModelDirectParent) {
        Preconditions.checkNotNull(fileModelDirectParent);
        final File fileDirectoryParent = fileModelDirectParent.getFile();
        if (!fileModelDirectParent.isDirectory() || fileDirectoryParent == null) {
            notifyLocalImageListenerFailed();
            return;
        }
        final List<FileModel> files = new ArrayList<>();

        final File[] filesArray = fileDirectoryParent.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return (new FileTypeModel(FileUtils.getExtensionFromPath(name)))
                                .equals(FileTypeModelENUM.IMAGE.type);
                    }
                }
        );
        final List<File> fs = new ArrayList<>();
        if (filesArray != null) {
            fs.addAll(Arrays.asList(filesArray));
        }
        for (final File file : fs) {
            final FileModel.FileModelBuilder fileModelBuilder = new FileAudioModel.FileModelBuilder()
                    .file(file);
            files.add(fileModelBuilder.build());
        }
        notifyLocalImageListenerSucceeded(files);
    }

    @Override
    public void clearCache() {
        mCacheGetAllLocalImage.clear();
        mCacheGetLocalImagesFolders.clear();
    }

    @Override
    public boolean registerAllLocalImageListener(final GetAllLocalImageListener getAllLocalImageListener) {
        synchronized (mGetAllLocalImageListeners) {
            //noinspection SimplifiableIfStatement
            if (getAllLocalImageListener == null || mGetAllLocalImageListeners.contains(getAllLocalImageListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetAllLocalImageListeners.add(getAllLocalImageListener);
        }
    }

    @Override
    public boolean unregisterAllLocalImageListener(final GetAllLocalImageListener getAllLocalImageListener) {
        synchronized (mGetAllLocalImageListeners) {
            return mGetAllLocalImageListeners.remove(getAllLocalImageListener);
        }
    }
    //endregion getLocalImage

    //region Register / Unregister listeners
    @Override
    public boolean registerLocalImageFoldersListener(final GetLocalImageFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalImageFoldersListeners) {
            //noinspection SimplifiableIfStatement
            if (getLocalImageFoldersListener == null || mGetLocalImageFoldersListeners.contains(getLocalImageFoldersListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetLocalImageFoldersListeners.add(getLocalImageFoldersListener);
        }
    }

    @Override
    public boolean unregisterLocalImageFoldersListener(final GetLocalImageFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalImageFoldersListeners) {
            return mGetLocalImageFoldersListeners.remove(getLocalImageFoldersListener);
        }
    }

    @Override
    public boolean registerLocalImageListener(final GetLocalImageListener getLocalImageListener) {
        synchronized (mGetLocalImageListeners) {
            //noinspection SimplifiableIfStatement
            if (getLocalImageListener == null || mGetLocalImageListeners.contains(getLocalImageListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetLocalImageListeners.add(getLocalImageListener);
        }
    }

    @Override
    public boolean unregisterLocalImageListener(final GetLocalImageListener getLocalImageListener) {
        synchronized (mGetLocalImageListeners) {
            return mGetLocalImageListeners.remove(getLocalImageListener);
        }
    }
    //endregion Register / Unregister listeners

    //region notify listeners
    private void notifyAllLocalImageListenerSucceeded(final List<FileModel> fileModels) {
        synchronized (mGetAllLocalImageListeners) {
            for (int i = 0, size = mGetAllLocalImageListeners.size(); i < size; i++) {
                mGetAllLocalImageListeners.get(i).onAllLocalImageSucceeded(fileModels);
            }
        }
    }

    private void notifyLocalImageFoldersListenerSucceeded(final List<FileModel> fileModels) {
        synchronized (mGetLocalImageFoldersListeners) {
            for (int i = 0, size = mGetLocalImageFoldersListeners.size(); i < size; i++) {
                mGetLocalImageFoldersListeners.get(i).onLocalImageFoldersSucceeded(fileModels);
            }
        }
    }

    private void notifyLocalImageFoldersListenerFailed() {
        synchronized (mGetLocalImageFoldersListeners) {
            for (int i = 0, size = mGetLocalImageFoldersListeners.size(); i < size; i++) {
                mGetLocalImageFoldersListeners.get(i).onLocalImageFoldersFailed();
            }
        }
    }

    private void notifyLocalImageListenerSucceeded(final List<FileModel> fileModels) {
        synchronized (mGetLocalImageListeners) {
            for (int i = 0, size = mGetLocalImageListeners.size(); i < size; i++) {
                mGetLocalImageListeners.get(i).onLocalImageSucceeded(fileModels);
            }
        }
    }

    private void notifyLocalImageListenerFailed() {
        synchronized (mGetLocalImageListeners) {
            for (int i = 0, size = mGetLocalImageListeners.size(); i < size; i++) {
                mGetLocalImageListeners.get(i).onLocalImageFailed();
            }
        }
    }
    //endregion notify listeners

    /**
     * Class used to count.
     * See {@link #getLocalImageFolders()}.
     * http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java
     * Used to count with a map.
     */
    protected class MutableInt {
        int value = 1; // note that we start at 1 since we're counting

        public void increment() {
            ++value;
        }
    }
}
