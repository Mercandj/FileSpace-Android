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
import android.support.annotation.DrawableRes;
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

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelAdapter;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.file.local.fab.FileLocalFabManager;
import com.mercandalli.android.apps.files.intent.IntentManager;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.settings.SettingsManager;
import com.mercandalli.android.library.base.dialog.DialogUtils;
import com.mercandalli.android.library.base.event.EventManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.mercandalli.android.apps.files.file.FileUtils.createFile;

/**
 * A {@link BackFragment} used to buildDisplay the local {@link FileModel} provide by the
 * {@link FileLocalApi}.
 */
public class FileLocalFragment extends BackFragment implements
        FileLocalPagerFragment.ListController,
        FileModelAdapter.OnFileClickListener,
        FileModelAdapter.OnFileLongClickListener,
        FileModelListener,
        FileLocalPagerFragment.HomeIconVisible,
        FileLocalOverflowActions.FileLocalActionCallback,
        SwipeRefreshLayout.OnRefreshListener,
        FileLocalPagerFragment.ScrollTop,
        FileLocalFabManager.FabController {

    /**
     * A key for the view pager position.
     */
    @NonNull
    private static final String ARG_POSITION_IN_VIEW_PAGER = "FileLocalFragment.Args.ARG_POSITION_IN_VIEW_PAGER";

    @NonNull
    public static FileLocalFragment newInstance(final int positionInViewPager) {
        final FileLocalFragment fileLocalFragment = new FileLocalFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION_IN_VIEW_PAGER, positionInViewPager);
        fileLocalFragment.setArguments(args);
        return fileLocalFragment;
    }

    private RecyclerView mRecyclerView;
    @NonNull
    private final List<FileModel> mFilesList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private FileModel mCurrentDirectory = createInitialDirectory();
    private TextView mMessageTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @NonNull
    private final List<FileModel> mFilesToCutList = new ArrayList<>();
    @NonNull
    private final List<FileModel> mFilesToCopyList = new ArrayList<>();

    /* Managers */
    private FileManager mFileManager;
    private FileLocalFabManager mFileLocalFabManager;

    private FileModelAdapter mFileModelAdapter;
    private FileLocalOverflowActions mFileLocalOverflowActions;

    private ScaleAnimationAdapter mScaleAnimationAdapter;

    private boolean mIsFabAnimating = false;
    @NonNull
    private final Handler mHandler = new Handler();
    @NonNull
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mIsFabAnimating = false;
        }
    };

    private boolean mIsRefreshing = false;
    private int mPositionInViewPager;

    private boolean mIsFabHidden;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileManager = FileManager.getInstance(getContext());
        mFileLocalFabManager = FileLocalFabManager.getInstance();

        final Bundle args = getArguments();
        if (!args.containsKey(ARG_POSITION_IN_VIEW_PAGER)) {
            throw new IllegalStateException("Missing args. Please use newInstance()");
        }
        mPositionInViewPager = args.getInt(ARG_POSITION_IN_VIEW_PAGER);
        mFileLocalFabManager.addFabController(mPositionInViewPager, this);
    }

    @Override
    public void onDestroy() {
        mFileLocalFabManager.removeFabController(mPositionInViewPager);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);

        findViews(rootView);
        initViews();
        refreshCurrentList();
        mFileLocalOverflowActions = new FileLocalOverflowActions(getContext(), this);

        final Context context = getContext();
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).invalidateOptionsMenu();
        }
        mFileLocalFabManager.updateFabButtons();
        return rootView;
    }

    @Override
    public boolean back() {
        if (hasItemSelected()) {
            deselectAll();
            return true;
        } else if (!mCurrentDirectory.getFile().getPath().equals(initialPath())) {
            if (mCurrentDirectory.getFile().getParent() != null) {
                mCurrentDirectory = new FileModel.FileModelBuilder()
                        .file(new File(mCurrentDirectory.getFile().getParentFile().getPath()))
                        .build();
                refreshCurrentList();
                return true;
            }
        } else if (!mFilesToCopyList.isEmpty() || !mFilesToCutList.isEmpty()) {
            if (!mFilesToCopyList.isEmpty()) {
                mFilesToCopyList.clear();
            }
            if (!mFilesToCutList.isEmpty()) {
                mFilesToCutList.clear();
            }
            refreshFab();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFabClick(
            final @IntRange(from = 0, to = FileLocalFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition,
            final @NonNull FloatingActionButton floatingActionButton) {
        final Context context = getContext();
        switch (fabPosition) {
            case 0:
                if ((mFilesToCopyList.size() != 0) || (mFilesToCutList.size() != 0)) {
                    for (final FileModel file : mFilesToCopyList) {
                        mFileManager.copyLocalFile((Activity) context, file, mCurrentDirectory.getFile().getAbsolutePath() + File.separator);
                    }
                    mFilesToCopyList.clear();
                    for (final FileModel file : mFilesToCutList) {
                        mFileManager.renameLocalByPath(file, mCurrentDirectory.getFile().getAbsolutePath() + File.separator + file.getFullName());
                    }
                    mFilesToCutList.clear();
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
                                            DialogUtils.prompt(
                                                    context,
                                                    context.getString(R.string.file_model_local_new_folder_file),
                                                    context.getString(R.string.file_model_local_new_folder_file_description),
                                                    getString(R.string.ok),
                                                    new DialogUtils.OnDialogUtilsStringListener() {
                                                        @Override
                                                        public void onDialogUtilsStringCalledBack(String text) {
                                                            createFile(mCurrentDirectory.getFile().getPath() + File.separator, text);
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
                if (mCurrentDirectory.getFile() != null &&
                        mCurrentDirectory.getFile().getParent() != null) {
                    mCurrentDirectory = new FileModel.FileModelBuilder()
                            .file(new File(mCurrentDirectory.getFile().getParentFile().getPath()))
                            .build();
                    refreshCurrentList();
                }
                refreshFab();
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFabVisible(
            final @IntRange(from = 0, to = FileLocalFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition) {
        if (mIsFabHidden) {
            return false;
        }
        switch (fabPosition) {
            case 0:
                return true;
            case 1:
                return mCurrentDirectory != null &&
                        mCurrentDirectory.getFile().getParent() != null;
        }
        return false;
    }

    @DrawableRes
    @Override
    public int getFabImageResource(
            final @IntRange(from = 0, to = FileLocalFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition) {
        switch (fabPosition) {
            case 0:
                if (!mFilesToCopyList.isEmpty()) {
                    return R.drawable.ic_menu_paste_holo_dark;
                } else if (!mFilesToCutList.isEmpty()) {
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
            EventManager.getInstance().sendBasicEvent(
                    "key_filespace_folder_local_clicked",
                    "test",
                    "click",
                    "folder",
                    "",
                    this,
                    "");
            mCurrentDirectory = new FileModel.FileModelBuilder()
                    .file(new File(mFilesList.get(position).getUrl()))
                    .build();
            showFab();
            refreshCurrentList();
        } else {
            mFileManager.execute((Activity) getContext(), position, mFilesList, view);
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
        mFileLocalOverflowActions.show(fileModel, view, Config.isLogged());
    }

    @Override
    public boolean isHomeVisible() {
        return !mCurrentDirectory.getUrl().equals(initialPath());
    }

    @Override
    public void refreshFab() {
        mFileLocalFabManager.updateFabButtons();
    }

    @Override
    public void refreshData() {
        refreshCurrentList();
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
        return !mFilesToCutList.isEmpty();
    }

    @Override
    public boolean isFileToCopy() {
        return !mFilesToCopyList.isEmpty();
    }

    @Override
    public void clearFileToCut() {
        mFilesToCutList.clear();
    }

    @Override
    public void clearFileToCopy() {
        mFilesToCopyList.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshCurrentList() {
        if (mIsRefreshing || mCurrentDirectory == null || !isAdded()) {
            return;
        }
        mIsRefreshing = true;
        final Context context = getContext();
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).invalidateOptionsMenu();
        }

        mFilesList.clear();

        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mMessageTextView.setVisibility(View.GONE);
        mRecyclerView.scrollToPosition(0);

        mFileManager.getFiles(
                mCurrentDirectory,
                SettingsManager.getInstance(context).isSuperUser(),
                new ResultCallback<List<FileModel>>() {
                    @Override
                    public void success(final List<FileModel> result) {
                        mFilesList.clear();
                        mFilesList.addAll(result);
                        if (mFilesList.size() == 0) {
                            mMessageTextView.setText(getString(R.string.no_file_local_folder, "" + mCurrentDirectory.getName()));
                            mMessageTextView.setVisibility(View.VISIBLE);
                        } else {
                            mMessageTextView.setVisibility(View.GONE);
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mIsRefreshing = false;
                        updateAdapter();
                    }

                    @Override
                    public void failure() {
                        mFilesList.clear();
                        mMessageTextView.setText("Failed to load");
                        mMessageTextView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mIsRefreshing = false;
                        updateAdapter();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
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
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRefresh() {
        refreshCurrentList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scrollTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    public void goHome() {
        mCurrentDirectory = createInitialDirectory();
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

    private void findViews(final View rootView) {
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mMessageTextView = (TextView) rootView.findViewById(R.id.message);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_file_files_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_file_files_swipe_refresh_layout);
    }

    private void initViews() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        final Activity activity = getActivity();
        final int nbColumn = activity.getResources().getInteger(R.integer.column_number_card);
        if (nbColumn <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(activity, nbColumn));
        }

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
                    showFab();
                } else {
                    hideFab();
                }
            }
        });
    }

    @NonNull
    protected FileModel createInitialDirectory() {
        final String initialFolderPath = IntentManager.getInstance().getInitialFolderPath();
        final File file = new File(initialFolderPath != null ?
                initialFolderPath :
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + Config.getLocalFolderName());
        if (!file.exists()) {
            file.mkdir();
        }
        return new FileModel.FileModelBuilder().file(file).build();
    }

    protected String initialPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private void hideFab() {
        if (!mIsFabAnimating) {
            mIsFabAnimating = true;
            mIsFabHidden = true;
            mFileLocalFabManager.updateFabButtons();
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, 250);
        }
    }

    private void showFab() {
        if (!mIsFabAnimating) {
            mIsFabAnimating = true;
            mIsFabHidden = false;
            mFileLocalFabManager.updateFabButtons();
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, 250);
        }
    }
}
