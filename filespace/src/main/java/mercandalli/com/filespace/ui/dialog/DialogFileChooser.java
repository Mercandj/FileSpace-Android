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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileTypeModel;
import mercandalli.com.filespace.ui.activitiy.ApplicationCallback;
import mercandalli.com.filespace.ui.adapter.file.FileModelAdapter;

public class DialogFileChooser extends Dialog {

    private Activity mActivity;
    private ApplicationCallback mApplicationActivity;
    private RecyclerView files;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<FileModel> listModelFile;
    private File currentFolder;
    private IFileModelListener listener;

    public DialogFileChooser(final Activity activity, final ApplicationCallback applicationCallback, IFileModelListener listener) {
        super(activity);

        this.mActivity = activity;
        this.mApplicationActivity = applicationCallback;
        this.listener = listener;

        this.setContentView(R.layout.dialog_filechooser);
        this.setTitle(R.string.app_name);
        this.setCancelable(true);

        files = (RecyclerView) this.findViewById(R.id.files);
        files.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        files.setLayoutManager(mLayoutManager);

        this.currentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        ((Button) this.findViewById(R.id.up)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File parent = currentFolder.getParentFile();
                if (parent != null)
                    currentFolder = parent;
                updateAdapter();
            }
        });

        updateAdapter();

        DialogFileChooser.this.show();
    }

    private void updateAdapter() {
        getFiles();
        FileModelAdapter adapter = new FileModelAdapter(mActivity, listModelFile, null);
        files.setAdapter(adapter);
        adapter.setOnItemClickListener(new FileModelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View arg1, int position) {
                if (position < listModelFile.size()) {
                    FileModel file = listModelFile.get(position);
                    if (file.isDirectory()) {
                        currentFolder = file.getFile();
                        updateAdapter();
                    } else {
                        listener.executeFileModel(file);
                        dismiss();
                    }
                }
            }
        });
    }

    private void getFiles() {
        String path = this.currentFolder.getAbsolutePath();
        File f = new File(path);
        File fs[] = f.listFiles();
        listModelFile = new ArrayList<>();
        if (fs != null)
            for (File file : fs) {
                FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
                fileModelBuilder.url(file.getAbsolutePath());
                fileModelBuilder.name(file.getName());
                fileModelBuilder.type(new FileTypeModel(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1)));
                fileModelBuilder.size(file.getTotalSpace());
                fileModelBuilder.isDirectory(file.isDirectory());
                fileModelBuilder.file(file);
                listModelFile.add(fileModelBuilder.build());
            }
    }
}
