package com.mercandalli.android.apps.files.file.local;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.main.FileApp;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple class that open a Dialog with the actions related to the {@link FileModel}.
 */
public class FileLocalActions {

    private Activity mActivity;

    private String mUploadString;
    private String mOpenAsString;
    private String mRenameString;
    private String mDeleteString;
    private String mCopyString;
    private String mCutString;
    private String mPropertiesString;

    private FileManager mFileManager;
    private FileLocalActionCallback mFileLocalActionCallback;

    public FileLocalActions(final Context context, final FileLocalActionCallback fileLocalActionCallback) {
        mActivity = (Activity) context;
        mFileLocalActionCallback = fileLocalActionCallback;
        mFileManager = FileApp.get(mActivity).getFileAppComponent().provideFileManager();
        initStrings(context);
    }

    /**
     * Show the {@link AlertDialog}.
     */
    public void show(final FileModel fileModel, boolean isLogged) {
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
        final List<String> menuList = new ArrayList<>();
        if (isLogged) {
            menuList.add(mUploadString);
        }
        if (!fileModel.isDirectory()) {
            menuList.add(mOpenAsString);
        }
        menuList.add(mRenameString);
        menuList.add(mDeleteString);
        menuList.add(mCopyString);
        menuList.add(mCutString);
        menuList.add(mPropertiesString);
        menuAlert.setTitle("Action");

        menuAlert.setItems(menuList.toArray(new String[menuList.size()]),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int item) {
                        moreActions(menuList.get(item), fileModel);
                    }
                });
        menuAlert.create().show();
    }

    private void initStrings(final Context context) {
        mUploadString = context.getString(R.string.upload);
        mOpenAsString = context.getString(R.string.open_as);
        mRenameString = context.getString(R.string.rename);
        mDeleteString = context.getString(R.string.delete);
        mCopyString = context.getString(R.string.copy);
        mCutString = context.getString(R.string.cut);
        mPropertiesString = context.getString(R.string.properties);
    }

    //region Actions

    /**
     * Open the dialog with actions on a {@link FileModel}.
     */
    private void moreActions(final String currentString, final FileModel fileModel) {
        if (currentString.equals(mUploadString)) {
            upload(fileModel);
        } else if (currentString.equals(mOpenAsString)) {
            openAs(fileModel);
        } else if (currentString.equals(mRenameString)) {
            rename(fileModel);
        } else if (currentString.equals(mDeleteString)) {
            delete(fileModel);
        } else if (currentString.equals(mCopyString)) {
            copy(fileModel);
        } else if (currentString.equals(mCutString)) {
            cut(fileModel);
        } else if (currentString.equals(mPropertiesString)) {
            properties(fileModel);
        }
    }

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
            }, mActivity.getString(R.string.cancel), null);
        }
    }

    private void openAs(FileModel fileModel) {
        mFileManager.openLocalAs(mActivity, fileModel);
    }

    private void rename(final FileModel fileModel) {
        DialogUtils.prompt(mActivity, mRenameString, "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
            @Override
            public void execute(String text) {
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
        }, "Cancel", null, fileModel.getFullName());
    }

    private void delete(final FileModel fileModel) {
        DialogUtils.alert(mActivity, mDeleteString, "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
            @Override
            public void execute() {
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
        }, "No", null);
    }

    private void copy(FileModel fileModel) {
        mFileLocalActionCallback.addCopyFile(fileModel);
        Toast.makeText(mActivity, "File ready to copy.", Toast.LENGTH_SHORT).show();
        mFileLocalActionCallback.refreshFab();
    }

    private void cut(FileModel fileModel) {
        mFileLocalActionCallback.addCutFile(fileModel);
        Toast.makeText(mActivity, "File ready to cut.", Toast.LENGTH_SHORT).show();
        mFileLocalActionCallback.refreshFab();
    }

    private void properties(FileModel fileModel) {
        DialogUtils.alert(mActivity,
                mPropertiesString + " : " + fileModel.getName(),
                mFileManager.toSpanned(mActivity, fileModel),
                "OK",
                null,
                null,
                null);
    }
    //endregion Actions

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
