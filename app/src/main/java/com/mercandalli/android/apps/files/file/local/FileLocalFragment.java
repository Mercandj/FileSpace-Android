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
package com.mercandalli.android.apps.files.file.local;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.fragment.FabFragment;
import com.mercandalli.android.apps.files.common.fragment.InjectedFabFragment;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelAdapter;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.FileApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mercandalli.android.apps.files.file.FileUtils.createFile;

/**
 * A {@link InjectedFabFragment} used to buildDisplay the local {@link FileModel} provide by the
 * {@link FileLocalApi}.
 */
public class FileLocalFragment extends FabFragment implements
        FileLocalPagerFragment.ListController,
        FileModelAdapter.OnFileClickListener,
        FileModelAdapter.OnFileLongClickListener,
        FileModelListener,
        FileLocalPagerFragment.HomeIconVisible,
        FileLocalOverflowActions.FileLocalActionCallback {

    private RecyclerView mRecyclerView;
    private final List<FileModel> mFilesList = new ArrayList<>();
    private ProgressBar mProgressBar;
    protected File mCurrentDirectory;
    private TextView mMessageTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<FileModel> mFilesToCutList = new ArrayList<>();
    private List<FileModel> mFilesToCopyList = new ArrayList<>();

    private FileManager mFileManager;
    private FileModelAdapter mFileModelAdapter;
    private FileLocalOverflowActions mFileLocalOverflowActions;

    private ScaleAnimationAdapter mScaleAnimationAdapter;

    private boolean mIsFabAnimating = false;
    private final Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mIsFabAnimating = false;
        }
    };

    public static FileLocalFragment newInstance() {
        return new FileLocalFragment();
    }

    /**
     * Default Constructor.
     * <p/>
     * <p/>
     * lint [ValidFragment]
     * http://developer.android.com/reference/android/app/Fragment.html#Fragment()
     * Every fragment must have an empty constructor, so it can be instantiated when restoring its activity's state.
     */
    public FileLocalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);

        findViews(rootView);
        initViews();
        refreshCurrentList();
        mFileLocalOverflowActions = new FileLocalOverflowActions(getContext(), this);

        mApplicationCallback.invalidateMenu();
        return rootView;
    }

    @Override
    public boolean back() {
        if (hasItemSelected()) {
            deselectAll();
            return true;
        } else if (!mCurrentDirectory.getPath().equals(initialPath())) {
            if (mCurrentDirectory.getParent() != null) {
                mCurrentDirectory = new File(mCurrentDirectory.getParentFile().getPath());
                refreshCurrentList();
                return true;
            }
        } else if ((mFilesToCopyList != null && mFilesToCopyList.size() != 0) || (mFilesToCutList != null && mFilesToCutList.size() != 0)) {
            if (mFilesToCopyList != null) {
                mFilesToCopyList.clear();
            }
            if (mFilesToCutList != null) {
                mFilesToCutList.clear();
            }
            refreshFab();
            return true;
        }
        return false;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onFabClick(int fab_id, FloatingActionButton fab) {
        final Context context = getContext();
        switch (fab_id) {
            case 0:
                if ((mFilesToCopyList != null && mFilesToCopyList.size() != 0) || (mFilesToCutList != null && mFilesToCutList.size() != 0)) {
                    if (mFilesToCopyList != null) {
                        initFileManager();
                        for (FileModel file : mFilesToCopyList) {
                            mFileManager.copyLocalFile((Activity) context, file, mCurrentDirectory.getAbsolutePath() + File.separator);
                        }
                        mFilesToCopyList.clear();
                    }
                    if (mFilesToCutList != null) {
                        initFileManager();
                        for (FileModel file : mFilesToCutList) {
                            mFileManager.renameLocalByPath(file, mCurrentDirectory.getAbsolutePath() + File.separator + file.getFullName());
                        }
                        mFilesToCutList.clear();
                    }
                    refreshCurrentList();
                } else {
                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(context);
                    final String[] menuList = {context.getString(R.string.file_model_local_new_folder_file)};
                    menuAlert.setTitle(context.getString(R.string.file_model_local_new_title));
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            DialogUtils.prompt(context,
                                                    context.getString(R.string.file_model_local_new_folder_file),
                                                    context.getString(R.string.file_model_local_new_folder_file_description),
                                                    getString(R.string.ok), new IStringListener() {
                                                        @Override
                                                        public void execute(String text) {
                                                            createFile(mCurrentDirectory.getPath() + File.separator, text);
                                                            refreshCurrentList();
                                                        }
                                                    }, getString(android.R.string.cancel), null, null, context.getString(R.string.name));
                                            break;
                                    }
                                }
                            });
                    menuAlert.create().show();
                }
                refreshFab();
                break;

            case 1:
                if (mCurrentDirectory.getParent() != null) {
                    mCurrentDirectory = new File(mCurrentDirectory.getParentFile().getPath());
                    refreshCurrentList();
                }
                refreshFab();
                break;
        }
    }

    @Override
    public boolean isFabVisible(int fab_id) {
        switch (fab_id) {
            case 0:
                return true;
            case 1:
                return this.mCurrentDirectory != null &&
                        mCurrentDirectory.getParent() != null;
        }
        return false;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        switch (fab_id) {
            case 0:
                if (mFilesToCopyList != null && mFilesToCopyList.size() != 0) {
                    return R.drawable.ic_menu_paste_holo_dark;
                } else if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
                    return R.drawable.ic_menu_paste_holo_dark;
                } else {
                    return R.drawable.add;
                }
            case 1:
                return R.drawable.arrow_up;
        }
        return R.drawable.add;
    }

    @Override
    public void onFileClick(View view, int position) {
        /*if (hasItemSelected()) {
            mFilesList.get(position).selected = !mFilesList.get(position).selected;
            adapter.notifyItemChanged(position);
        } else */
        if (mFilesList.get(position).isDirectory()) {
            mCurrentDirectory = new File(mFilesList.get(position).getUrl());
            refreshCurrentList();
        } else {
            initFileManager();
            mFileManager.execute((Activity) getContext(), position, mFilesList, view);
        }
    }

    private void initFileManager() {
        if (mFileManager == null) {
            mFileManager = FileApp.get().getFileAppComponent().provideFileManager();
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
    public void executeFileModel(final FileModel fileModel, final View view) {
        mFileLocalOverflowActions.show(fileModel, view, mApplicationCallback.isLogged());
    }

    @Override
    public boolean isHomeVisible() {
        return !mCurrentDirectory.getPath().equals(initialPath());
    }

    @Override
    public void refreshData() {
        mApplicationCallback.refreshData();
    }

    @Override
    public void addCopyFile(FileModel fileModel) {
        mFilesToCopyList.add(fileModel);
    }

    @Override
    public void addCutFile(FileModel fileModel) {
        mFilesToCutList.add(fileModel);
    }

    @Override
    public boolean isFileToCut() {
        return mFilesToCutList != null && mFilesToCutList.size() != 0;
    }

    @Override
    public boolean isFileToCopy() {
        return mFilesToCopyList != null && mFilesToCopyList.size() != 0;
    }

    @Override
    public void clearFileToCut() {
        mFilesToCutList.clear();
    }

    @Override
    public void clearFileToCopy() {
        mFilesToCopyList.clear();
    }

    public void goHome() {
        initCurrentDirectory();
        this.refreshCurrentList();
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

    @Override
    public void refreshCurrentList() {
        if (mCurrentDirectory == null) {
            return;
        }
        mApplicationCallback.invalidateMenu();

        final File[] files = mCurrentDirectory.listFiles();
        List<File> fs;
        if (files == null) {
            fs = new ArrayList<>();
        } else {
            fs = Arrays.asList(files);
        }

        Collections.sort(fs, new Comparator<File>() {
            @Override
            public int compare(final File f1, final File f2) {
                return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
            }
        });

        mFilesList.clear();
        for (File file : fs) {
            FileModel tmpFileModel = new FileModel.FileModelBuilder().file(file).build();
            /*
            if (mSortMode == SharedAudioPlayerUtils.SORT_SIZE)
                tmpFileModel.adapterTitleStart = FileUtils.humanReadableByteCount(tmpFileModel.size) + " - ";
            */
            mFilesList.add(tmpFileModel);
        }

        if (mFilesList.size() == 0) {
            mMessageTextView.setText(getString(R.string.no_file_local_folder, "" + mCurrentDirectory.getName()));
            mMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mMessageTextView.setVisibility(View.GONE);
        }

        updateAdapter();
    }

    @Override
    public void updateAdapter() {
        if (mRecyclerView != null && isAdded()) {
            refreshFab();
            final Parcelable recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
            mFileModelAdapter.setList(mFilesList);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelAdapter);
                mScaleAnimationAdapter.setDuration(220);
                mScaleAnimationAdapter.setOffsetDuration(32);
                mRecyclerView.setAdapter(mScaleAnimationAdapter);
            }
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
    }

    private void findViews(final View rootView) {
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mMessageTextView = (TextView) rootView.findViewById(R.id.message);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_file_files_swipe_refresh_layout);
    }

    private void initViews() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout.setEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        final Activity activity = getActivity();
        final int nbColumn = activity.getResources().getInteger(R.integer.column_number_card);
        if (nbColumn <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(activity, nbColumn));
        }

        initCurrentDirectory();

        mFileModelAdapter = new FileModelAdapter(getContext(), mFilesList, this, this, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelAdapter);
            mScaleAnimationAdapter.setDuration(220);
            mScaleAnimationAdapter.setOffsetDuration(32);
            mRecyclerView.setAdapter(mScaleAnimationAdapter);
        } else {
            mRecyclerView.setAdapter(mFileModelAdapter);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) {
                    if (!mIsFabAnimating) {
                        mIsFabAnimating = true;
                        mRefreshFabCallback.showFab(0);
                        mRefreshFabCallback.showFab(1);
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.postDelayed(mRunnable, 250);
                    }
                } else {
                    if (!mIsFabAnimating) {
                        mIsFabAnimating = true;
                        mRefreshFabCallback.hideFab(0);
                        mRefreshFabCallback.hideFab(1);
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.postDelayed(mRunnable, 250);
                    }
                }
            }
        });
    }

    protected void initCurrentDirectory() {
        mCurrentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Config.getLocalFolderName());
        if (!mCurrentDirectory.exists()) {
            mCurrentDirectory.mkdir();
        }
    }

    protected String initialPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
