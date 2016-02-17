package com.mercandalli.android.apps.files.file.local;

import com.mercandalli.android.apps.files.main.Config;

import java.io.File;

import static com.mercandalli.android.apps.files.file.FileUtils.getSdCardPath;

public class FileLocalSdFragment extends FileLocalFragment {

    public static FileLocalSdFragment newInstance() {
        return new FileLocalSdFragment();
    }

    @Override
    protected void initCurrentDirectory() {
        mCurrentDirectory = new File(getSdCardPath() + File.separator + Config.getLocalFolderName());
        if (!mCurrentDirectory.exists()) {
            mCurrentDirectory.mkdir();
        }
    }

    @Override
    protected String initialPath() {
        return getSdCardPath();
    }
}
