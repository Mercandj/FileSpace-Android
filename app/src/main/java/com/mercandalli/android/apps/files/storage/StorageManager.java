package com.mercandalli.android.apps.files.storage;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class StorageManager {

    @Nullable
    private static StorageManager sInstance;

    @NonNull
    public static StorageManager getInstance() {
        if (sInstance == null) {
            sInstance = new StorageManager();
        }
        return sInstance;
    }

    @Nullable
    private Storage mStorageDisk;

    @Nullable
    private Storage mStorageRam;

    private StorageManager() {
    }

    @NonNull
    public Storage getStorageDisk() {
        if (mStorageDisk == null) {
            final StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            final long blockSize = statFs.getBlockSize();
            final long totalSize = statFs.getBlockCount() * blockSize;
            final long availableSize = statFs.getAvailableBlocks() * blockSize;
            mStorageDisk = new Storage(totalSize, availableSize);
        }
        return mStorageDisk;
    }

    @NonNull
    public Storage getRam(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return new Storage(0, 0);
        }
        if (mStorageRam == null) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long available = mi.availMem;
            long total = mi.totalMem;
            mStorageRam = new Storage(total, available);
        }
        return mStorageRam;
    }
}
