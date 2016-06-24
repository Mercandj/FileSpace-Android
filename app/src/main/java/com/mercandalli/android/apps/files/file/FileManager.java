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

import java.io.File;
import java.util.List;

/**
 * The {@link FileModel} Manager abstract class.
 */
public interface FileManager {

    /**
     * Get the {@link FileModel}s from a local parent.
     */
    void getFiles(
            final File fileParent,
            final boolean isSuperUser,
            final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Get the {@link FileModel}s from a parent. (Could be local or online.)
     */
    void getFiles(
            final FileModel fileParent,
            final boolean isSuperUser,
            final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Get the {@link FileModel}s from a parent and a search. (Could be local or online.)
     */
    void getFiles(
            final FileModel fileParent,
            final boolean isSuperUser,
            boolean areMyFiles,
            final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Download an online {@link FileModel}.
     */
    void download(
            final Activity activity,
            final FileModel fileModel,
            final IListener listener);

    /**
     * Upload a local {@link FileModel}.
     */
    void upload(
            final FileModel fileModel,
            final int idFileParent,
            final IListener listener);

    /**
     * Rename a {@link FileModel}.
     */
    void rename(
            final FileModel fileModel,
            final String newName,
            final IListener listener);

    void renameLocalByPath(
            final FileModel fileModel,
            final String path);

    /**
     * Delete a {@link FileModel}.
     */
    void delete(
            final FileModel fileModel,
            final IListener listener);

    /**
     * Cut a file.
     */
    void setParent(
            final FileModel fileModel,
            final int newIdFileParent,
            final IListener listener);

    /**
     * Set the file visibility (now online online files).
     */
    void setPublic(
            final FileModel fileModel,
            final boolean isPublic,
            final IListener listener);

    /**
     * Default click action. Call and {@link android.content.Intent} or a specific {@link Activity}.
     */
    void execute(
            final Activity activity,
            final int position,
            final List fileModelList, View view);

    /**
     * Open local file as... (Open a dialog to select).
     */
    void openLocalAs(
            final Activity activity,
            final FileModel fileModel);

    /**
     * Get the {@link FileModel} overview.
     */
    Spanned toSpanned(
            final Context context,
            final FileModel fileModel);

    void copyLocalFile(
            final Activity activity,
            final FileModel fileModel,
            final String outputPath);

    void copyLocalFile(
            final Activity activity,
            final FileModel fileModel,
            final String outputPath,
            final IPostExecuteListener listener);

    /**
     * Is this online file mine.
     */
    boolean isMine(final FileModel fileModel);

    void searchLocal(
            final Context context,
            final String search,
            final ResultCallback<List<FileModel>> resultCallback);

    void getCover(
            final Context context,
            final FileAudioModel fileAudioModel,
            final ImageView imageView);

    /**
     * Get the parameters to upload a local {@link FileModel}.
     *
     * @param fileModel A local {@link FileModel}
     * @return The upload parameters.
     */
    List<StringPair> getForUpload(final FileModel fileModel);
}
