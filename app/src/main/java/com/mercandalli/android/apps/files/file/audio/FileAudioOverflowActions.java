package com.mercandalli.android.apps.files.file.audio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.audio.metadata.FileAudioMetaDataEditionDialog;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.library.mainlibrary.precondition.Preconditions;

/**
 * A simple class that open a Dialog with the actions related to the {@link FileModel}.
 */
public class FileAudioOverflowActions implements PopupMenu.OnMenuItemClickListener {

    private final Activity mActivity;

    private final String mUploadString;
    private final String mRenameString;
    private final String mDeleteString;
    private final String mPropertiesString;

    private final FileManager mFileManager;
    private final FileAudioActionCallback mFileLocalActionCallback;
    private final FragmentManager mFragmentManager;
    private FileAudioModel mFileAudioModel;

    public FileAudioOverflowActions(
            final Context context,
            final FragmentManager fragmentManager,
            final FileAudioActionCallback fileLocalActionCallback) {

        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(fragmentManager);
        Preconditions.checkNotNull(fileLocalActionCallback);

        mActivity = (Activity) context;
        mFragmentManager = fragmentManager;
        mFileLocalActionCallback = fileLocalActionCallback;
        mFileManager = FileApp.get().getFileAppComponent().provideFileManager();
        mUploadString = context.getString(R.string.upload);
        mRenameString = context.getString(R.string.rename);
        mDeleteString = context.getString(R.string.delete);
        mPropertiesString = context.getString(R.string.properties);
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.popup_overflow_file_audio_upload:
                upload(mFileAudioModel);
                return true;
            case R.id.popup_overflow_file_audio_open_as:
                openAs(mFileAudioModel);
                return true;
            case R.id.popup_overflow_file_audio_rename:
                rename(mFileAudioModel);
                return true;
            case R.id.popup_overflow_file_audio_delete:
                delete(mFileAudioModel);
                return true;
            case R.id.popup_overflow_file_audio_meta_data:
                editMetadata(mFileAudioModel);
                return true;
            case R.id.popup_overflow_file_audio_properties:
                properties(mFileAudioModel);
                return true;
        }
        return false;
    }

    /**
     * Show the {@link AlertDialog}.
     */
    public void show(
            final FileAudioModel fileAudioModel,
            final View overflow,
            final boolean isLogged) {

        Preconditions.checkNotNull(fileAudioModel);
        mFileAudioModel = fileAudioModel;

        final PopupMenu popupMenu = new PopupMenu(mActivity, overflow);
        popupMenu.getMenuInflater().inflate(R.menu.popup_overflow_file_audio, popupMenu.getMenu());

        final Menu menu = popupMenu.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            final MenuItem menuItem = menu.getItem(i);
            final int currentId = menuItem.getItemId();
            if (currentId == R.id.popup_overflow_file_audio_upload && !isLogged) {
                menuItem.setVisible(false);
            } else if (currentId == R.id.popup_overflow_file_audio_open_as && fileAudioModel.isDirectory()) {
                menuItem.setVisible(false);
            } else if (currentId == R.id.popup_overflow_file_audio_meta_data) {
                menuItem.setVisible(fileAudioModel.getPath().endsWith(".mp3"));
            }
        }
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    //region Actions
    private void upload(final FileModel fileModel) {
        if (fileModel.isDirectory()) {
            Toast.makeText(mActivity, mActivity.getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
        } else {
            DialogUtils.alert(mActivity, mUploadString, "Upload file " + fileModel.getName(), mUploadString, new IListener() {
                @Override
                public void execute() {
                    if (fileModel.getFile() != null) {
                        mFileManager.upload(fileModel, -1, new IListener() {
                            @Override
                            public void execute() {
                                mFileLocalActionCallback.refreshData();
                            }
                        });
                    }
                }
            }, mActivity.getString(android.R.string.cancel), null);
        }
    }

    /**
     * Open local file as... (Open a dialog to select).
     *
     * @param fileModel The {@link FileModel} to open.
     */
    private void openAs(final FileModel fileModel) {
        mFileManager.openLocalAs(mActivity, fileModel);
    }

    private void rename(final FileModel fileModel) {
        DialogUtils.prompt(mActivity, mRenameString, "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
            @Override
            public void execute(String text) {
                mFileManager.rename(fileModel, text, new IListener() {
                    @Override
                    public void execute() {
                        mFileLocalActionCallback.refreshData();
                    }
                });
            }
        }, "Cancel", null, fileModel.getFullName());
    }

    private void delete(final FileModel fileModel) {
        DialogUtils.alert(
                mActivity,
                mDeleteString,
                "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?",
                mActivity.getResources().getString(android.R.string.yes),
                new IListener() {
                    @Override
                    public void execute() {
                        mFileManager.delete(fileModel, new IListener() {
                            @Override
                            public void execute() {
                                mFileLocalActionCallback.refreshData();
                            }
                        });
                    }
                },
                mActivity.getResources().getString(android.R.string.no),
                null);
    }

    private void editMetadata(final FileAudioModel fileAudioModel) {
        FileAudioMetaDataEditionDialog.newInstance(fileAudioModel).show(mFragmentManager, null);
    }

    private void properties(final FileModel fileModel) {
        DialogUtils.alert(mActivity,
                mPropertiesString + " : " + fileModel.getName(),
                mFileManager.toSpanned(mActivity, fileModel),
                "OK",
                null,
                null,
                null);
    }
    //endregion Actions

    /**
     * An interface to provide methods to do actions (rename, cut...)
     */
    /* package */
    interface FileAudioActionCallback {

        void refreshData();
    }
}
