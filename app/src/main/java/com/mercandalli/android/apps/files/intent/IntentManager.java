package com.mercandalli.android.apps.files.intent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class IntentManager {

    @Nullable
    private static IntentManager sInstance;

    @NonNull
    public static IntentManager getInstance() {
        if (sInstance == null) {
            sInstance = new IntentManager();
        }
        return sInstance;
    }

    @Nullable
    private String mInitialFolderPath;

    private IntentManager() {

    }

    public void setInitialFolderPath(@Nullable final String path) {
        mInitialFolderPath = path;
    }

    @Nullable
    public String getInitialFolderPath() {
        return mInitialFolderPath;
    }
}
