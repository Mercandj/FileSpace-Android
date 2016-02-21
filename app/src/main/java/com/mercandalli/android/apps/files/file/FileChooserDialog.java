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
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.common.view.divider.SpacesItemDecoration;
import com.mercandalli.android.apps.files.main.FileApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FileChooserDialog extends Dialog implements FileModelAdapter.OnFileClickListener {

    @Inject
    protected FileManager mFileManager;

    private RecyclerView mRecyclerView;
    private final List<FileModel> mFileModelList;
    private File mCurrentFile;
    private FileModelListener mFileModelListener;
    private final String mRootPath;

    public FileChooserDialog(final Activity activity, FileModelListener listener) {
        super(activity);

        FileApp.get().getFileAppComponent().inject(this);

        mFileModelListener = listener;
        mFileModelList = new ArrayList<>();

        setContentView(R.layout.dialog_filechooser);
        setTitle(R.string.app_name);
        setCancelable(true);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        mRecyclerView = (RecyclerView) findViewById(R.id.files);
        mRecyclerView.setHasFixedSize(true);
        if (activity.getResources().getBoolean(R.bool.is_landscape)) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(12, 2));

        mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mCurrentFile = new File(mRootPath);

        findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRootPath.equals(mCurrentFile.getAbsolutePath())) {
                    File parent = mCurrentFile.getParentFile();
                    if (parent != null) {
                        mCurrentFile = parent;
                    }
                    refreshList();
                }
            }
        });

        refreshList();

        FileChooserDialog.this.show();
    }

    @Override
    public void onFileClick(View view, int position) {
        if (position < mFileModelList.size()) {
            FileModel file = mFileModelList.get(position);
            if (file.isDirectory()) {
                if (file.getCount() == 0) {
                    Toast.makeText(getContext(), "No files", Toast.LENGTH_SHORT).show();
                } else {
                    mCurrentFile = file.getFile();
                    refreshList();
                }
            } else {
                mFileModelListener.executeFileModel(file, view);
                dismiss();
            }
        }
    }

    private void refreshList() {
        mFileManager.getFiles(
                new FileModel.FileModelBuilder().file(mCurrentFile).build(),
                new ResultCallback<List<FileModel>>() {
                    @Override
                    public void success(List<FileModel> result) {
                        mFileModelList.clear();
                        mFileModelList.addAll(result);
                        updateAdapter();
                    }

                    @Override
                    public void failure() {

                    }
                });
    }

    private void updateAdapter() {
        final FileModelAdapter adapter = new FileModelAdapter(getContext(), mFileModelList, null, this, null);
        ScaleAnimationAdapter scaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, adapter);
        scaleAnimationAdapter.setDuration(220);
        scaleAnimationAdapter.setOffsetDuration(32);

        mRecyclerView.setAdapter(scaleAnimationAdapter);
    }
}
