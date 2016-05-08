package com.mercandalli.android.apps.files.storage;

/**
 * Created by Jonathan on 07/05/2016.
 */
public final class Storage {

    private final long mTotalSize;
    private final long mAvailableSize;

    public Storage(final long totalSize, final long availableSize) {
        mTotalSize = totalSize;
        mAvailableSize = availableSize;
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public long getAvailableSize() {
        return mAvailableSize;
    }
}
