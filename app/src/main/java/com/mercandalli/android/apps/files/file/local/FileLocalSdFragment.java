package com.mercandalli.android.apps.files.file.local;

import android.support.annotation.NonNull;

import com.mercandalli.android.apps.files.main.Config;

import java.io.File;

import static com.mercandalli.android.apps.files.file.FileUtils.getSdCardPath;

public class FileLocalSdFragment extends FileLocalFragment {

    public static FileLocalSdFragment newInstance() {
        return new FileLocalSdFragment();
    }

    @NonNull
    @Override
    protected File createInitialDirectory() {
        final File file = new File(getSdCardPath() + File.separator + Config.getLocalFolderName());
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    @Override
    protected String initialPath() {
        return getSdCardPath();
    }
}
