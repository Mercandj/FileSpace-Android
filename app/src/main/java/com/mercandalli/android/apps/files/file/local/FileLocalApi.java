package com.mercandalli.android.apps.files.file.local;

import com.mercandalli.android.apps.files.file.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An API providing the local {@link FileModel}.
 */
public class FileLocalApi {

    public List<FileModel> getFiles(final File directoryFile) {
        final List<File> fs = Arrays.asList(directoryFile.listFiles());

        final List<FileModel> mFilesList = new ArrayList<>();
        for (File file : fs) {
            FileModel.FileModelBuilder mileModelBuilder = new FileModel.FileModelBuilder();
            mFilesList.add(mileModelBuilder.file(file).build());
        }

        return mFilesList;
    }

}
