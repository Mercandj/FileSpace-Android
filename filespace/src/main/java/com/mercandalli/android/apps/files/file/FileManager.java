package com.mercandalli.android.apps.files.file;

import android.app.Activity;
import android.content.Context;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;

import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link FileModel} Manager abstract class.
 */
public abstract class FileManager {

    /**
     * Get the {@link FileModel}s from a parent. (Could be local or online.)
     */
    public abstract void getFiles(final FileModel fileParent, final int sortMode, final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Get the {@link FileModel}s from a parent and a search. (Could be local or online.)
     */
    public abstract void getFiles(final FileModel fileParent, boolean areMyFiles, final String search, final int sortMode, final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Download an online {@link FileModel}.
     */
    public abstract void download(final Activity activity, final FileModel fileModel, final IListener listener);

    public abstract void upload(final FileModel fileModel, int idFileParent, final IListener listener);

    /**
     * Rename a {@link FileModel}.
     */
    public abstract void rename(final FileModel fileModel, final String newName, final IListener listener);

    public abstract void renameLocalByPath(FileModel fileModel, String path);

    /**
     * Delete a {@link FileModel}.
     */
    public abstract void delete(final FileModel fileModel, final IListener listener);

    /**
     * Cut a file.
     */
    public abstract void setParent(final FileModel fileModel, final int newIdFileParent, final IListener listener);

    /**
     * Set the file visibility (now online online files).
     */
    public abstract void setPublic(final FileModel fileModel, final boolean isPublic, final IListener listener);

    public abstract void execute(final Activity activity, final int position, final List fileModelList, View view);

    /**
     * Open local file as... (Open a dialog to select).
     */
    public abstract void openLocalAs(final Activity activity, final FileModel fileModel);

    public abstract Spanned toSpanned(final FileModel fileModel);

    public abstract void copyLocalFile(final Activity activity, final FileModel fileModel, final String outputPath);

    public abstract void copyLocalFile(final Activity activity, final FileModel fileModel, String outputPath, IPostExecuteListener listener);

    /**
     * Is this online file mine.
     */
    public abstract boolean isMine(final FileModel fileModel);

    /**
     * Get all the {@link FileAudioModel} in the device.
     */
    public abstract void getLocalMusic(final Context context, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback);

    /**
     * Get all the {@link FileAudioModel} in a folder.
     */
    public abstract void getLocalMusic(final Context context, final FileModel fileModelDirectParent, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback);

    /**
     * Get all local folders that contain music.
     */
    public abstract void getLocalMusicFolders(final Context context, final int sortMode, final String search, final ResultCallback<List<FileModel>> resultCallback);

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
