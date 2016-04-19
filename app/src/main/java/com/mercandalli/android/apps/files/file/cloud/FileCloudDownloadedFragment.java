/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p>
 * LICENSE:
 * <p>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.file.cloud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.fragment.InjectedFabFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelAdapter;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.file.cloud.fab.FileCloudFabManager;
import com.mercandalli.android.apps.files.file.local.FileLocalApi;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.FileAppComponent;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * A {@link InjectedFabFragment} used to buildDisplay the local {@link FileModel} provide by the
 * {@link FileLocalApi}.
 */
public class FileCloudDownloadedFragment extends InjectedFabFragment implements
        FileModelAdapter.OnFileClickListener,
        FileModelAdapter.OnFileLongClickListener,
        FileModelListener,
        SwipeRefreshLayout.OnRefreshListener,
        FileCloudFabManager.FabController {

    /**
     * A key for the view pager position.
     */
    private static final String ARG_POSITION_IN_VIEW_PAGER = "FileCloudDownloadedFragment.Args.ARG_POSITION_IN_VIEW_PAGER";

    private RecyclerView mRecyclerView;
    private final List<FileModel> mFilesList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private File mCurrentDirectory;
    private TextView mMessageTextView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<FileModel> mFilesToCutList = new ArrayList<>();
    private List<FileModel> mFilesToCopyList = new ArrayList<>();
    private FileModelAdapter mFileModelAdapter;
    private int mPositionInViewPager;

    @Inject
    FileManager mFileManager;

    @Inject
    FileCloudFabManager mFileCloudFabManager;

    public static FileCloudDownloadedFragment newInstance(final int positionInViewPager) {
        final FileCloudDownloadedFragment fileCloudDownloadedFragment = new FileCloudDownloadedFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION_IN_VIEW_PAGER, positionInViewPager);
        fileCloudDownloadedFragment.setArguments(args);
        return fileCloudDownloadedFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (!args.containsKey(ARG_POSITION_IN_VIEW_PAGER)) {
            throw new IllegalStateException("Missing args. Please use newInstance()");
        }
        mPositionInViewPager = args.getInt(ARG_POSITION_IN_VIEW_PAGER);
        mFileCloudFabManager.addFabController(mPositionInViewPager, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);

        final Context context = getContext();

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mMessageTextView = (TextView) rootView.findViewById(R.id.message);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_file_files_swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_file_files_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        if (context.getResources().getBoolean(R.bool.is_landscape)) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        //mRecyclerView.addItemDecoration(new FileDivider(ContextCompat.getColor(mActivity, R.color.file_divider)));

        mCurrentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Config.LOCAL_FOLDER_NAME_DEFAULT);
        if (!mCurrentDirectory.exists()) {
            mCurrentDirectory.mkdir();
        }

        mFileModelAdapter = new FileModelAdapter(getContext(), mFilesList, this, this, this);
        final ScaleAnimationAdapter scaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelAdapter);
        scaleAnimationAdapter.setDuration(220);
        scaleAnimationAdapter.setOffsetDuration(32);
        mRecyclerView.setAdapter(scaleAnimationAdapter);

        refreshList();

        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).invalidateOptionsMenu();
        }

        return rootView;
    }

    @Override
    public boolean back() {
        if (hasItemSelected()) {
            deselectAll();
            return true;
        } else if (!mCurrentDirectory.getPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            if (mCurrentDirectory.getParent() != null) {
                FileCloudDownloadedFragment.this.mCurrentDirectory = new File(mCurrentDirectory.getParentFile().getPath());
                FileCloudDownloadedFragment.this.refreshList();
                return true;
            }
        } else if ((mFilesToCopyList != null && mFilesToCopyList.size() != 0) || (mFilesToCutList != null && mFilesToCutList.size() != 0)) {
            if (mFilesToCopyList != null) {
                mFilesToCopyList.clear();
            }
            if (mFilesToCutList != null) {
                mFilesToCutList.clear();
            }
            //refreshFab();
            return true;
        }
        return false;
    }


    @Override
    public void onFabClick(
            @IntRange(from = 0, to = FileCloudFabManager.NUMBER_MAX_OF_FAB - 1) final int fabPosition,
            @NonNull final FloatingActionButton floatingActionButton) {
    }

    @Override
    public boolean isFabVisible(@IntRange(from = 0, to = FileCloudFabManager.NUMBER_MAX_OF_FAB - 1) final int fabPosition) {
        return false;
    }

    @Override
    public int getFabImageResource(@IntRange(from = 0, to = FileCloudFabManager.NUMBER_MAX_OF_FAB - 1) final int fabPosition) {
        return 0;
    }

    @Override
    protected void inject(FileAppComponent fileAppComponent) {
        fileAppComponent.inject(this);
    }

    @Override
    public void onFileClick(View view, int position) {
        /*if (hasItemSelected()) {
            mFilesList.get(position).selected = !mFilesList.get(position).selected;
            adapter.notifyItemChanged(position);
        } else */
        if (mFilesList.get(position).isDirectory()) {
            mCurrentDirectory = new File(mFilesList.get(position).getUrl());
            refreshList();
        } else {
            mFileManager.execute(getActivity(), position, mFilesList, view);
        }
    }

    @Override
    public boolean onFileLongClick(View view, int position) {
        /*
        mFilesList.get(position).selected = !mFilesList.get(position).selected;
        adapter.notifyItemChanged(position);
        */
        return true;
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    @Override
    public void executeFileModel(final FileModel fileModel, final View view) {
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(getContext());
        String[] menuList = {getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
        if (Config.isLogged()) {
            menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
        }
        menuAlert.setTitle("Action");
        menuAlert.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (!Config.isLogged()) {
                            item += 1;
                        }
                        switch (item) {
                            case 0:
                                if (fileModel.isDirectory()) {
                                    Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                                } else {
                                    DialogUtils.alert(getActivity(), getString(R.string.upload), "Upload file " + fileModel.getName(), getString(R.string.upload), new IListener() {
                                        @Override
                                        public void execute() {
                                            if (fileModel.getFile() != null) {
                                                mFileManager.upload(fileModel, -1, new IListener() {
                                                    @Override
                                                    public void execute() {

                                                    }
                                                });
                                            }
                                        }
                                    }, getString(android.R.string.cancel), null);
                                }
                                break;
                            case 1:
                                mFileManager.openLocalAs(getActivity(), fileModel);
                                break;
                            case 2:
                                DialogUtils.prompt(getActivity(), "Rename", "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
                                    @Override
                                    public void execute(String text) {
                                        mFileManager.rename(fileModel, text, new IListener() {
                                            @Override
                                            public void execute() {
                                                if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
                                                    mFilesToCutList.clear();
                                                    //refreshFab();
                                                }
                                                if (mFilesToCopyList != null && mFilesToCopyList.size() != 0) {
                                                    mFilesToCopyList.clear();
                                                    //refreshFab();
                                                }
                                            }
                                        });
                                    }
                                }, "Cancel", null, fileModel.getFullName());
                                break;
                            case 3:
                                DialogUtils.alert(getActivity(), "Delete", "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
                                    @Override
                                    public void execute() {
                                        mFileManager.delete(fileModel, new IListener() {
                                            @Override
                                            public void execute() {
                                                if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
                                                    mFilesToCutList.clear();
                                                    //refreshFab();
                                                }
                                                if (mFilesToCopyList != null && mFilesToCopyList.size() != 0) {
                                                    mFilesToCopyList.clear();
                                                    //refreshFab();
                                                }
                                            }
                                        });
                                    }
                                }, "No", null);
                                break;
                            case 4:
                                FileCloudDownloadedFragment.this.mFilesToCopyList.add(fileModel);
                                Toast.makeText(getContext(), "File ready to copy.", Toast.LENGTH_SHORT).show();
                                //refreshFab();
                                break;
                            case 5:
                                FileCloudDownloadedFragment.this.mFilesToCutList.add(fileModel);
                                Toast.makeText(getContext(), "File ready to cut.", Toast.LENGTH_SHORT).show();
                                //refreshFab();
                                break;
                            case 6:
                                DialogUtils.alert(getActivity(),
                                        getString(R.string.properties) + " : " + fileModel.getName(),
                                        mFileManager.toSpanned(getContext(), fileModel),
                                        "OK",
                                        null,
                                        null,
                                        null);
                                break;
                        }
                    }
                });
        AlertDialog menuDrop = menuAlert.create();
        menuDrop.show();
    }

    public void goHome() {
        this.mCurrentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Config.getLocalFolderName());
        this.refreshList();
    }

    public boolean hasItemSelected() {
        /*
        for (FileModel file : mFilesList)
            if (file.selected)
                return true;
                */
        return false;
    }

    public void deselectAll() {
        /*
        for (FileModel file : mFilesList)
            file.selected = false;
            */
        updateAdapter();
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(final String search) {
        if (mCurrentDirectory == null) {
            return;
        }

        final File[] files = (search == null) ? mCurrentDirectory.listFiles() : mCurrentDirectory.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().contains(search.toLowerCase());
                    }
                }
        );
        List<File> fs;
        if (files == null) {
            fs = new ArrayList<>();
        } else {
            fs = Arrays.asList(files);
        }

        mFilesList.clear();
        for (File file : fs) {
            if (file.exists()) {
                mFilesList.add(new FileModel.FileModelBuilder().file(file).build());
            }
        }

        //refreshFab();

        if (mFilesList.size() == 0) {
            mMessageTextView.setText(getString(R.string.no_file_local_folder, "" + mCurrentDirectory.getName()));
            mMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mMessageTextView.setVisibility(View.GONE);
        }

        if (mFilesList.size() == 0) {
            mMessageTextView.setText(getString(R.string.no_file_local_folder, "" + mCurrentDirectory.getName()));
            mMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mMessageTextView.setVisibility(View.GONE);
        }

        updateAdapter();
    }

    public void updateAdapter() {
        if (mRecyclerView != null && isAdded()) {
            //refreshFab();
            mFileModelAdapter.setList(mFilesList);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void refreshCurrentList() {
        refreshList();
    }
}
