package com.mercandalli.android.apps.files.storage;

import android.app.ActivityManager;
import android.content.Context;
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

    private Storage mStorageDisk;
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
