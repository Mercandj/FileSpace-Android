package com.mercandalli.android.filespace.file;

import android.app.Activity;
import android.content.Context;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;

import com.mercandalli.android.filespace.common.listener.IListener;
import com.mercandalli.android.filespace.common.listener.IPostExecuteListener;
import com.mercandalli.android.filespace.common.listener.ResultCallback;
import com.mercandalli.android.filespace.common.util.StringPair;
import com.mercandalli.android.filespace.file.audio.FileAudioModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link FileModel} Manager abstract class.
 */
public abstract class FileManager {

    public abstract void getFiles(final FileModel fileParent, final int sortMode, final ResultCallback<List<FileModel>> resultCallback);

    public abstract void getFiles(final FileModel fileParent, boolean areMyFiles, final String search, final int sortMode, final ResultCallback<List<FileModel>> resultCallback);

    public abstract void download(final Activity activity, final FileModel fileModel, final IListener listener);

    public abstract void upload(final FileModel fileModel, int idFileParent, final IListener listener);

    public abstract void rename(final FileModel fileModel, final String newName, final IListener listener);

    public abstract void renameLocalByPath(FileModel fileModel, String path);

    public abstract void delete(final FileModel fileModel, final IListener listener);

    public abstract void setParent(final FileModel fileModel, final int id_file_parent, final IListener listener);

    public abstract void setPublic(final FileModel fileModel, final boolean isPublic, final IListener listener);

    public abstract void execute(final Activity activity, final int position, final List fileModelList, View view);

    public abstract void openLocalAs(final Activity activity, final FileModel fileModel);

    public abstract Spanned toSpanned(final FileModel fileModel);

    public abstract void copyLocalFile(final Activity activity, final FileModel fileModel, final String outputPath);

    public abstract void copyLocalFile(final Activity activity, final FileModel fileModel, String outputPath, IPostExecuteListener listener);

    public abstract boolean isMine(final FileModel fileModel);

    public abstract void getLocalMusic(final Context context, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback);

    /**
     * Get all local folders that contain music.
     */
    public abstract void getLocalMusicFolder(final Context context, final int sortMode, final String search, final ResultCallback<List<FileModel>> resultCallback);

    public abstract void getCover(final Context context, final FileAudioModel fileAudioModel, final ImageView imageView);

    public static List<StringPair> getForUpload(final FileModel fileModel) {
        List<StringPair> parameters = new ArrayList<>();
        if (fileModel.getName() != null) {
            parameters.add(new StringPair("url", fileModel.getName()));
        }
        if (fileModel.isDirectory()) {
            parameters.add(new StringPair("directory", "true"));
        }
        if (fileModel.getIdFileParent() != -1) {
            parameters.add(new StringPair("id_file_parent", "" + fileModel.getIdFileParent()));
        }
        return parameters;
    }
}
