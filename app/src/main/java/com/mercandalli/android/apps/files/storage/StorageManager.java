package com.mercandalli.android.apps.files.storage;

import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;

public final class StorageManager {

    private static StorageManager sInstance;

    @NonNull
    public static StorageManager getInstance() {
        if (sInstance == null) {
            sInstance = new StorageManager();
        }
        return sInstance;
    }

    private Storage mStorage;

    private StorageManager() {

    }

    @NonNull
    public Storage getStorage() {
        if (mStorage == null) {
            final StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            final long blockSize = statFs.getBlockSize();
            final long totalSize = statFs.getBlockCount() * blockSize;
            final long availableSize = statFs.getAvailableBlocks() * blockSize;
            mStorage = new Storage(totalSize, availableSize);
        }
        return mStorage;
    }
}
