/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.App;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.listener.ResultCallback;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.ui.adapter.animation.ScaleAnimationAdapter;
import mercandalli.com.filespace.ui.adapter.file.FileModelAdapter;
import mercandalli.com.filespace.ui.view.divider.SpacesItemDecoration;

public class DialogFileChooser extends Dialog {

    @Inject
    FileManager mFileManager;

    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private List<FileModel> mFileModelList;
    private File mCurrentFile;
    private IFileModelListener mIFileModelListener;
    private final String mRootPath;

    public DialogFileChooser(final Activity activity, IFileModelListener listener) {
        super(activity);

        App.get(activity).getAppComponent().inject(this);

        mActivity = activity;
        mIFileModelListener = listener;
        mFileModelList = new ArrayList<>();

        setContentView(R.layout.dialog_filechooser);
        setTitle(R.string.app_name);
        setCancelable(true);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        mRecyclerView = (RecyclerView) findViewById(R.id.files);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
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

        DialogFileChooser.this.show();
    }

    private void updateAdapter() {
        FileModelAdapter adapter = new FileModelAdapter(mActivity, mFileModelList, R.layout.tab_file_light, null);
        ScaleAnimationAdapter scaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, adapter);
        scaleAnimationAdapter.setDuration(220);
        scaleAnimationAdapter.setOffsetDuration(32);

        mRecyclerView.setAdapter(scaleAnimationAdapter);
        adapter.setOnItemClickListener(new FileModelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View arg1, int position) {
                if (position < mFileModelList.size()) {
                    FileModel file = mFileModelList.get(position);
                    if (file.isDirectory()) {
                        if (file.getCount() == 0) {
                            Toast.makeText(mActivity, "No files", Toast.LENGTH_SHORT).show();
                        } else {
                            mCurrentFile = file.getFile();
                            refreshList();
                        }
                    } else {
                        mIFileModelListener.executeFileModel(file);
                        dismiss();
                    }
                }
            }
        });
    }

    private void refreshList() {
        mFileManager.getFiles(
                new FileModel.FileModelBuilder().file(mCurrentFile).build(),
                Constants.SORT_ABC,
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
}
