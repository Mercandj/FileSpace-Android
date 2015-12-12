/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.file;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.main.ApplicationCallback;

public class FileUploadDialog extends Dialog {

    @Inject
    FileManager mFileManager;

    private final Activity mActivity;
    private final ApplicationCallback mApplicationCallback;
    FileChooserDialog mFileChooserDialog;
    FileModel mFileModel;
    int id_file_parent;

    public FileUploadDialog(final Activity activity, final ApplicationCallback applicationCallback, final int id_file_parent, final FileModel fileModel, final IListener listener) {
        this(activity, applicationCallback, id_file_parent, listener);

        fileModel.setIdFileParent(id_file_parent);
        ((TextView) FileUploadDialog.this.findViewById(R.id.label)).setText(fileModel.getUrl());
        mFileModel = fileModel;
    }

    public FileUploadDialog(final Activity activity, final ApplicationCallback applicationCallback, final int id_file_parent, final IListener listener) {
        super(activity);

        FileApp.get(activity).getFileAppComponent().inject(this);

        mActivity = activity;
        mApplicationCallback = applicationCallback;
        this.id_file_parent = id_file_parent;

        this.setContentView(R.layout.dialog_upload);
        this.setTitle(R.string.app_name);
        this.setCancelable(true);

        this.findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileModel != null && !mFileModel.isDirectory()) {
                    mFileManager.upload(mFileModel, id_file_parent, listener);
                } else {
                    Toast.makeText(mActivity, mActivity.getString(R.string.no_file), Toast.LENGTH_SHORT).show();
                }

                FileUploadDialog.this.dismiss();
            }
        });

        this.findViewById(R.id.fileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileChooserDialog = new FileChooserDialog(mActivity, new FileModelListener() {
                    @Override
                    public void executeFileModel(FileModel fileModel) {
                        fileModel.setIdFileParent(id_file_parent);
                        ((TextView) FileUploadDialog.this.findViewById(R.id.label)).setText(fileModel.getUrl());
                        FileUploadDialog.this.mFileModel = fileModel;
                    }
                });
            }
        });

        show();
    }
}
