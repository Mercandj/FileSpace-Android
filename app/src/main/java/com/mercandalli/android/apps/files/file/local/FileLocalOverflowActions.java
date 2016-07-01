package com.mercandalli.android.apps.files.file.local;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.library.base.dialog.DialogUtils;
import com.mercandalli.android.library.base.precondition.Preconditions;

/**
 * A simple class that open a Dialog with the actions related to the {@link FileModel}.
 */
public class FileLocalOverflowActions implements PopupMenu.OnMenuItemClickListener {

    private final Activity mActivity;

    private final String mUploadString;
    private final String mRenameString;
    private final String mDeleteString;
    private final String mPropertiesString;

    private final FileManager mFileManager;
    private final FileLocalActionCallback mFileLocalActionCallback;
    private FileModel mFileModel;

    private boolean mShowCopyCut = true;

    public FileLocalOverflowActions(
            final Context context,
            final FileLocalActionCallback fileLocalActionCallback) {

        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(fileLocalActionCallback);

        mActivity = (Activity) context;
        mFileLocalActionCallback = fileLocalActionCallback;
        mFileManager = FileManager.getInstance(context);
        mUploadString = context.getString(R.string.upload);
        mRenameString = context.getString(R.string.rename);
        mDeleteString = context.getString(R.string.delete);
        mPropertiesString = context.getString(R.string.properties);
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.popup_overflow_file_upload:
                upload(mFileModel);
                return true;
            case R.id.popup_overflow_file_open_as:
                openAs(mFileModel);
                return true;
            case R.id.popup_overflow_file_rename:
                rename(mFileModel);
                return true;
            case R.id.popup_overflow_file_delete:
                delete(mFileModel);
                return true;
            case R.id.popup_overflow_file_copy:
                copy(mFileModel);
                return true;
            case R.id.popup_overflow_file_cut:
                cut(mFileModel);
                return true;
            case R.id.popup_overflow_file_properties:
                properties(mFileModel);
                return true;
        }
        return false;
    }

    /**
     * Show the {@link AlertDialog}.
     */
    public void show(
            final FileModel fileModel,
            final View overflow,
            final boolean isLogged) {

        Preconditions.checkNotNull(fileModel);
        mFileModel = fileModel;

        final PopupMenu popupMenu = new PopupMenu(mActivity, overflow);
        popupMenu.getMenuInflater().inflate(R.menu.popup_overflow_file, popupMenu.getMenu());

        final Menu menu = popupMenu.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            final MenuItem menuItem = menu.getItem(i);
            final int currentId = menuItem.getItemId();
            if (currentId == R.id.popup_overflow_file_upload && !isLogged) {
                menuItem.setVisible(false);
            } else if (currentId == R.id.popup_overflow_file_open_as && fileModel.isDirectory()) {
                menuItem.setVisible(false);
            } else if (currentId == R.id.popup_overflow_file_copy && !mShowCopyCut) {
                menuItem.setVisible(false);
            } else if (currentId == R.id.popup_overflow_file_cut && !mShowCopyCut) {
                menuItem.setVisible(false);
            }
        }
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    public void setShowCopyCut(boolean showCopyCut) {
        mShowCopyCut = showCopyCut;
    }

    //region Actions
    private void upload(final FileModel fileModel) {
        if (fileModel.isDirectory()) {
            Toast.makeText(mActivity, mActivity.getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
        } else {
            DialogUtils.alert(
                    mActivity,
                    mUploadString,
                    "Upload file " + fileModel.getName(),
                    mUploadString,
                    new DialogUtils.OnDialogUtilsListener() {
                        @Override
                        public void onDialogUtilsCalledBack() {
                            if (fileModel.getFile() != null) {
                                mFileManager.upload(fileModel, -1, new IListener() {
                                    @Override
                                    public void execute() {
                                        mFileLocalActionCallback.refreshData();
                                    }
                                });
                            }
                        }
                    }, mActivity.getResources().getString(android.R.string.cancel), null);
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
        DialogUtils.prompt(
                mActivity,
                mRenameString,
                "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?",
                "Ok",
                new DialogUtils.OnDialogUtilsStringListener() {
                    @Override
                    public void onDialogUtilsStringCalledBack(String text) {
                        mFileManager.rename(fileModel, text, new IListener() {
                            @Override
                            public void execute() {
                                if (mFileLocalActionCallback.isFileToCut()) {
                                    mFileLocalActionCallback.clearFileToCut();
                                    mFileLocalActionCallback.refreshFab();
                                }
                                if (mFileLocalActionCallback.isFileToCopy()) {
                                    mFileLocalActionCallback.clearFileToCopy();
                                    mFileLocalActionCallback.refreshFab();
                                }
                                mFileLocalActionCallback.refreshData();
                            }
                        });
                    }
                }, mActivity.getResources().getString(android.R.string.cancel), null, fileModel.getFullName());
    }

    private void delete(final FileModel fileModel) {
        DialogUtils.alert(
                mActivity,
                mDeleteString,
                "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?",
                mActivity.getResources().getString(android.R.string.yes),
                new DialogUtils.OnDialogUtilsListener() {
                    @Override
                    public void onDialogUtilsCalledBack() {
                        mFileManager.delete(fileModel, new IListener() {
                            @Override
                            public void execute() {
                                if (mFileLocalActionCallback.isFileToCut()) {
                                    mFileLocalActionCallback.clearFileToCut();
                                    mFileLocalActionCallback.refreshFab();
                                }
                                if (mFileLocalActionCallback.isFileToCopy()) {
                                    mFileLocalActionCallback.clearFileToCopy();
                                    mFileLocalActionCallback.refreshFab();
                                }
                                mFileLocalActionCallback.refreshData();
                            }
                        });
                    }
                },
                mActivity.getResources().getString(android.R.string.no),
                null);
    }

    private void copy(final FileModel fileModel) {
        mFileLocalActionCallback.addCopyFile(fileModel);
        Toast.makeText(mActivity, "File ready to copy.", Toast.LENGTH_SHORT).show();
        mFileLocalActionCallback.refreshFab();
    }

    private void cut(final FileModel fileModel) {
        mFileLocalActionCallback.addCutFile(fileModel);
        Toast.makeText(mActivity, "File ready to cut.", Toast.LENGTH_SHORT).show();
        mFileLocalActionCallback.refreshFab();
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
    interface FileLocalActionCallback {
        void refreshFab();

        void refreshData();

        void addCopyFile(FileModel fileModel);

        void addCutFile(FileModel fileModel);

        boolean isFileToCut();

        boolean isFileToCopy();

        void clearFileToCut();

        void clearFileToCopy();
    }
}
