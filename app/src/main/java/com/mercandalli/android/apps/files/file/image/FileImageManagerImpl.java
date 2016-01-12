package com.mercandalli.android.apps.files.file.image;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class FileImageManagerImpl implements FileImageManager {

    private final List<LocalImageFoldersListener> mLocalImageFoldersListeners = new ArrayList<>();

    public FileImageManagerImpl(Application application) {

    }

    @Override
    public void getLocalImageFolders(Context context, int sortMode, String search) {

    }

    @Override
    public void registerLocalImageFoldersListener(LocalImageFoldersListener var1) {

    }

    @Override
    public void unregisterLocalImageFoldersListener(LocalImageFoldersListener var1) {

    }

    private void notifyLocalImageFoldersListenerSucceeded() {

    }
}
