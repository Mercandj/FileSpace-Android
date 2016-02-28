package com.mercandalli.android.apps.files.file.local;

import com.mercandalli.android.apps.files.file.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An API providing the local {@link FileModel}.
 */
public class FileLocalApi {

    public List<FileModel> getFiles(final File directoryFile) {
        final List<FileModel> mFilesList = new ArrayList<>();
        final File[] files = directoryFile.listFiles();
        final List<File> fs;
        if (files == null) {
            fs = new ArrayList<>();
        } else {
            fs = Arrays.asList(files);
        }
        Collections.sort(fs, new Comparator<File>() {
            @Override
            public int compare(final File f1, final File f2) {
                return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
            }
        });
        mFilesList.clear();
        for (final File file : fs) {
            if (file.exists()) {
                mFilesList.add(new FileModel.FileModelBuilder().file(file).build());
            }
        }
        return mFilesList;
    }

}
