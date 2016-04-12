package com.mercandalli.android.apps.files.file.local;

import android.support.annotation.NonNull;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;

import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mercandalli.android.apps.files.file.FileUtils.isAudioPath;

/**
 * An API providing the local {@link FileModel}.
 */
public class FileLocalApi {

    @NonNull
    public List<FileModel> getFiles(final File directoryFile) {
        final List<FileModel> filesList = new ArrayList<>();
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
        filesList.clear();

        final MyID3 myID3 = new MyID3();
        for (final File file : fs) {
            if (file.exists()) {
                if (!file.isDirectory() && isAudioPath(file.getPath().toLowerCase())) {
                    filesList.add(new FileAudioModel.FileAudioModelBuilder().file(file, myID3).build());
                } else {
                    filesList.add(new FileModel.FileModelBuilder().file(file).build());
                }
            }
        }
        return filesList;
    }

}
