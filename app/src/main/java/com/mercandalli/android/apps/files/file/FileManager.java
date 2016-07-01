package com.mercandalli.android.apps.files.file;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
public abstract class FileManager {

    @Nullable
    private static FileManager sInstance;

    @NonNull
    public static FileManager getInstance(@NonNull final Context context) {
        if(sInstance == null) {
            sInstance = new FileManagerImpl(context);
        }
        return sInstance;
    }

    /**
     * Get the {@link FileModel}s from a local parent.
     */
    public abstract void getFiles(
            final File fileParent,
            final boolean isSuperUser,
            final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Get the {@link FileModel}s from a parent. (Could be local or online.)
     */
    public abstract void getFiles(
            final FileModel fileParent,
            final boolean isSuperUser,
            final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Get the {@link FileModel}s from a parent and a search. (Could be local or online.)
     */
    public abstract void getFiles(
            final FileModel fileParent,
            final boolean isSuperUser,
            boolean areMyFiles,
            final ResultCallback<List<FileModel>> resultCallback);

    /**
     * Download an online {@link FileModel}.
     */
    public abstract void download(
            final Activity activity,
            final FileModel fileModel,
            final IListener listener);

    /**
     * Upload a local {@link FileModel}.
     */
    public abstract void upload(
            final FileModel fileModel,
            final int idFileParent,
            final IListener listener);

    /**
     * Rename a {@link FileModel}.
     */
    public abstract void rename(
            final FileModel fileModel,
            final String newName,
            final IListener listener);

    public abstract void renameLocalByPath(
            final FileModel fileModel,
            final String path);

    /**
     * Delete a {@link FileModel}.
     */
    public abstract void delete(
            final FileModel fileModel,
            final IListener listener);

    /**
     * Cut a file.
     */
    public abstract void setParent(
            final FileModel fileModel,
            final int newIdFileParent,
            final IListener listener);

    /**
     * Set the file visibility (now online online files).
     */
    public abstract void setPublic(
            final FileModel fileModel,
            final boolean isPublic,
            final IListener listener);

    /**
     * Default click action. Call and {@link android.content.Intent} or a specific {@link Activity}.
     */
    public abstract void execute(
            @NonNull final Activity activity,
            final int position,
            @NonNull final List<FileModel> fileModelList,
            final View view);

    /**
     * Open local file as... (Open a dialog to select).
     */
    public abstract void openLocalAs(
            final Activity activity,
            final FileModel fileModel);

    /**
     * Get the {@link FileModel} overview.
     */
    public abstract Spanned toSpanned(
            final Context context,
            final FileModel fileModel);

    public abstract void copyLocalFile(
            final Activity activity,
            final FileModel fileModel,
            final String outputPath);

    public abstract void copyLocalFile(
            final Activity activity,
            final FileModel fileModel,
            final String outputPath,
            final IPostExecuteListener listener);

    /**
     * Is this online file mine.
     */
    public abstract boolean isMine(final FileModel fileModel);

    public abstract void searchLocal(
            final Context context,
            final String search,
            final ResultCallback<List<FileModel>> resultCallback);

    public abstract void getCover(
            final Context context,
            final FileAudioModel fileAudioModel,
            final ImageView imageView);

    /**
     * Get the parameters to upload a local {@link FileModel}.
     *
     * @param fileModel A local {@link FileModel}
     * @return The upload parameters.
     */
    public abstract List<StringPair> getForUpload(final FileModel fileModel);
}
