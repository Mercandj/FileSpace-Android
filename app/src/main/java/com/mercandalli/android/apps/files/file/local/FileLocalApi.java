package com.mercandalli.android.apps.files.file.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.library.base.su.SuperUserFile;
import com.mercandalli.android.library.base.su.SuperUserManager;

import java.io.File;
import java.lang.ref.WeakReference;
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

    @Nullable
    private static FileLocalApi sInstance;

    @NonNull
    public static FileLocalApi getInstance() {
        if (sInstance == null) {
            sInstance = new FileLocalApi();
        }
        return sInstance;
    }

    private FileLocalApi() {

    }

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

        for (final File file : fs) {
            if (file.exists()) {
                if (!file.isDirectory() && isAudioPath(file.getPath().toLowerCase())) {
                    filesList.add(new FileAudioModel.FileAudioModelBuilder().file(file).build());
                } else {
                    filesList.add(new FileModel.FileModelBuilder().file(file).build());
                }
            }
        }
        return filesList;
    }

    public void getFilesSuperUser(
            final File directoryFile,
            final GetFilesSuperUser getFilesSuperUser) {
        final WeakReference<GetFilesSuperUser> weakReference = new WeakReference<>(getFilesSuperUser);
        SuperUserManager.getInstance().getFolderChildren(
                directoryFile.getAbsolutePath(),
                new SuperUserManager.FolderChildrenListener() {
                    @Override
                    public boolean onGetFolderChildrenSucceeded(@NonNull final String s, @NonNull final List<SuperUserFile> list) {
                        final GetFilesSuperUser reference = weakReference.get();
                        if (reference != null) {
                            List<FileModel> fileModels = new ArrayList<>();
                            for (final SuperUserFile superUserFile : list) {
                                fileModels.add(new FileModel.FileModelBuilder()
                                        .isDirectory(superUserFile.isDirectory())
                                        .nameWithExt(superUserFile.getName())
                                        .url(superUserFile.getAbsolutePath())
                                        .build());
                            }
                            reference.onGetFilesSuperUser(fileModels);
                        }
                        return true;
                    }

                    @Override
                    public boolean onGetFolderChildrenFailed(@NonNull final String s) {
                        final GetFilesSuperUser reference = weakReference.get();
                        if (reference != null) {
                            reference.onGetFilesSuperUser(new ArrayList<FileModel>());
                        }
                        return true;
                    }
                });
    }

    public interface GetFilesSuperUser {
        void onGetFilesSuperUser(List<FileModel> files);
    }
}
