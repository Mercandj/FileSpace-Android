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
package com.mercandalli.android.apps.files.file.audio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.animation.ScaleAnimationAdapter;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.fragment.InjectedFabFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.util.StringUtils;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelCardAdapter;
import com.mercandalli.android.apps.files.file.FileModelCardHeaderItem;
import com.mercandalli.android.apps.files.file.local.FileLocalPagerFragment;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.FileAppComponent;
import com.mercandalli.android.apps.files.precondition.Preconditions;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A {@link android.support.v4.app.Fragment} that displays the local {@link FileAudioModel}s.
 */
public class FileAudioLocalFragment extends InjectedFabFragment implements
        FileLocalPagerFragment.ListController,
        BackFragment.ISortMode,
        FileModelCardAdapter.OnFileSubtitleAdapter,
        FileModelCardAdapter.OnHeaderClickListener,
        ScaleAnimationAdapter.NoAnimatedPosition,
        FileAudioManager.GetAllLocalMusicListener,
        FileAudioManager.MusicsChangeListener,
        FileAudioManager.GetLocalMusicFoldersListener,
        FileAudioManager.GetLocalMusicListener {

    private static final String TAG = "FileAudioLocalFragment";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PAGE_FOLDERS,
            PAGE_FOLDER_INSIDE,
            PAGE_ALL})
    public @interface CurrentPage {
    }

    private static final int PAGE_FOLDERS = 0;
    private static final int PAGE_FOLDER_INSIDE = 1;
    private static final int PAGE_ALL = 2;

    @CurrentPage
    private int mCurrentPage = PAGE_FOLDERS;

    private RecyclerView mRecyclerView;
    private final List<FileModel> mFileModels = new ArrayList<>();
    private final List<FileAudioModel> mFileAudioModels = new ArrayList<>();
    private TextView mMessageTextView;

    private List<FileModelCardHeaderItem> mHeaderIds;

    /**
     * A simple {@link ProgressBar}. Call {@link #showProgressBar()} or {@link #hideProgressBar()}.
     */
    private ProgressBar mProgressBar;

    private FileAudioRowAdapter mFileAudioRowAdapter;
    private FileModelCardAdapter mFileModelCardAdapter;

    private int mSortMode = Constants.SORT_DATE_MODIFICATION;

    private final IListener mRefreshActivityAdapterListener;

    private ScaleAnimationAdapter mScaleAnimationAdapter;

    private String mStringDirectory;
    private String mStringMusic;
    private String mStringMusics;

    /**
     * A simple {@link Handler}. Called by {@link #showProgressBar()} or {@link #hideProgressBar()}.
     */
    private final Handler mProgressBarActivationHandler;

    /**
     * A simple {@link Runnable}. Called by {@link #showProgressBar()} or {@link #hideProgressBar()}.
     */
    private final Runnable mProgressBarActivationRunnable;

    private FileModel mCurrentFolder;

    @Inject
    FileManager mFileManager;

    @Inject
    FileAudioManager mFileAudioManager;

    public static FileAudioLocalFragment newInstance() {
        Bundle args = new Bundle();
        FileAudioLocalFragment fragment = new FileAudioLocalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Do not use this constructor. Call {@link #newInstance()} instead.
     */
    public FileAudioLocalFragment() {
        mRefreshActivityAdapterListener = new IListener() {
            @Override
            public void execute() {
                if (mApplicationCallback != null) {
                    mApplicationCallback.refreshData();
                }
            }
        };
        mProgressBarActivationHandler = new Handler();
        mProgressBarActivationRunnable = new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        };
    }

    private void updateLayoutManager() {
        if (mCurrentPage == PAGE_FOLDERS) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.column_number_small_card));
            mRecyclerView.setLayoutManager(gridLayoutManager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return mFileModelCardAdapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        } else {
            final int nbColumn = getResources().getInteger(R.integer.column_number_card);
            if (nbColumn <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), nbColumn);
                mRecyclerView.setLayoutManager(gridLayoutManager);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return mFileAudioRowAdapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_audio_local, container, false);
        final Context context = getContext();

        mStringDirectory = context.getString(R.string.file_audio_model_adapter_directory);
        mStringMusic = context.getString(R.string.file_audio_model_music);
        mStringMusics = context.getString(R.string.file_audio_model_musics);

        mFileAudioManager.registerAllLocalMusicListener(this);
        mFileAudioManager.registerLocalMusicFoldersListener(this);
        mFileAudioManager.registerLocalMusicListener(this);
        mFileAudioManager.registerOnMusicUpdateListener(this);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.fragment_file_audio_local_progress_bar);
        mProgressBar.setVisibility(View.GONE);
        mMessageTextView = (TextView) rootView.findViewById(R.id.fragment_file_audio_local_message);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_file_audio_local_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        updateLayoutManager();

        mHeaderIds = new ArrayList<>();
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_folder, true));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_recent, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_artist, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_album, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.view_file_header_audio_all, false));

        mFileAudioRowAdapter = new FileAudioRowAdapter(mHeaderIds, this, mActivity, mFileAudioModels, new FileAudioModelListener() {
            @Override
            public void executeFileAudioModel(final FileAudioModel fileAudioModel) {
                final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.tags_edition), getString(R.string.properties)};
                if (mApplicationCallback.isLogged()) {
                    menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.tags_edition), getString(R.string.properties)};
                }
                menuAlert.setTitle("Action");
                menuAlert.setItems(menuList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (!mApplicationCallback.isLogged()) {
                                    item += 2;
                                }
                                switch (item) {
                                    case 0:
                                        if (fileAudioModel.isDirectory()) {
                                            Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                        } else {
                                            DialogUtils.alert(mActivity, getString(R.string.upload), "Upload file " + fileAudioModel.getName(), getString(R.string.upload), new IListener() {
                                                @Override
                                                public void execute() {
                                                    if (fileAudioModel.getFile() != null) {
                                                        List<StringPair> parameters = FileManager.getForUpload(fileAudioModel);
                                                        (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile, new IPostExecuteListener() {
                                                            @Override
                                                            public void onPostExecute(JSONObject json, String body) {

                                                            }
                                                        }, parameters, fileAudioModel.getFile())).execute();
                                                    }
                                                }
                                            }, getString(R.string.cancel), null);
                                        }
                                        break;
                                    case 1:
                                        mFileManager.openLocalAs(mActivity, fileAudioModel);
                                        break;
                                    case 2:
                                        DialogUtils.prompt(mActivity, "Rename", "Rename " + (fileAudioModel.isDirectory() ? "directory" : "file") + " " + fileAudioModel.getName() + " ?", "Ok", new IStringListener() {
                                            @Override
                                            public void execute(String text) {
                                                mFileManager.rename(fileAudioModel, text, mRefreshActivityAdapterListener);
                                            }
                                        }, "Cancel", null, fileAudioModel.getFullName());
                                        break;
                                    case 3:
                                        DialogUtils.alert(mActivity, "Delete", "Delete " + (fileAudioModel.isDirectory() ? "directory" : "file") + " " + fileAudioModel.getName() + " ?", "Yes", new IListener() {
                                            @Override
                                            public void execute() {
                                                mFileManager.delete(fileAudioModel, mRefreshActivityAdapterListener);
                                            }
                                        }, "No", null);
                                        break;
                                    case 4:
                                        FileAudioEditTagsDialog.newInstance(fileAudioModel).show(getFragmentManager(), null);
                                        break;
                                    case 5:
                                        DialogUtils.alert(mActivity,
                                                getString(R.string.properties) + " : " + fileAudioModel.getName(),
                                                mFileAudioManager.toSpanned(mActivity, fileAudioModel),
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
        });
        mFileAudioRowAdapter.setOnItemClickListener(new FileAudioRowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFileAudioModels.isEmpty()) {
                    return;
                }
                FileModel fileModel;
                if (position >= mFileAudioModels.size()) {
                    fileModel = mFileAudioModels.get(mFileAudioModels.size() - 1);
                    Log.e(TAG, "onItemClick: position >= size");
                } else {
                    fileModel = mFileAudioModels.get(position);
                }
                if (fileModel.isDirectory()) {
                    refreshListFoldersInside(fileModel);
                } else {
                    mFileManager.execute(mActivity, position, mFileAudioModels, view);
                }
            }
        });

        mFileModelCardAdapter = new FileModelCardAdapter(context, mHeaderIds, this, mFileModels, null, new FileModelCardAdapter.OnFileClickListener() {
            @Override
            public void onFileCardClick(View view, int position) {
                refreshListFoldersInside(mFileModels.get(position));
            }
        }, null);
        mFileModelCardAdapter.setOnFileSubtitleAdapter(this);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelCardAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mScaleAnimationAdapter.setNoAnimatedPosition(FileAudioLocalFragment.this);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);

        refreshListFolders();

        mApplicationCallback.invalidateMenu();

        // Pre-load.
        mFileAudioManager.getAllLocalMusic(mSortMode, null);

        return rootView;
    }

    @Override
    public boolean back() {
        if (mCurrentPage == PAGE_FOLDER_INSIDE) {
            refreshListFolders();
            return true;
        }
        return false;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onFabClick(int fab_id, FloatingActionButton fab) {
        if (fab_id == 0) {
            refreshListFolders();
        }
    }

    @Override
    public boolean isFabVisible(int fab_id) {
        return fab_id == 0 && mCurrentPage == PAGE_FOLDER_INSIDE;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        return R.drawable.arrow_up;
    }

    @Override
    public void setSortMode(int sortMode) {
        if (sortMode == Constants.SORT_ABC ||
                sortMode == Constants.SORT_DATE_MODIFICATION ||
                sortMode == Constants.SORT_SIZE) {
            mSortMode = sortMode;
            switch (mCurrentPage) {
                case PAGE_ALL:
                    refreshListAllMusic();
                    break;
                case PAGE_FOLDER_INSIDE:
                    refreshListFoldersInside(mCurrentFolder);
                    break;
                case PAGE_FOLDERS:
                    refreshListFolders();
                    break;
            }
        }
    }

    @Override
    protected void inject(FileAppComponent fileAppComponent) {
        fileAppComponent.inject(this);
    }

    @Nullable
    @Override
    public String onFileSubtitleModify(FileModel fileModel) {
        if (fileModel != null && fileModel.isDirectory() && fileModel.getCountAudio() != 0) {
            return mStringDirectory + ": " + StringUtils.longToShortString(fileModel.getCountAudio()) + " " + (fileModel.getCountAudio() > 1 ? mStringMusics : mStringMusic);
        }
        return null;
    }

    @Override
    public boolean onHeaderClick(View v, List<FileModelCardHeaderItem> fileModelCardHeaderItems) {
        mHeaderIds.clear();
        mHeaderIds.addAll(fileModelCardHeaderItems);
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.view_file_header_audio_folder:
                refreshListFolders();
                break;
            case R.id.view_file_header_audio_recent:
                //TODO
                break;
            case R.id.view_file_header_audio_artist:
                //TODO
                break;
            case R.id.view_file_header_audio_album:
                //TODO
                break;
            case R.id.view_file_header_audio_all:
                refreshListAllMusic();
                break;
        }
        return false;
    }

    @Override
    public boolean isAnimatedItem(int position) {
        return mCurrentPage == PAGE_FOLDER_INSIDE || position != 0;
    }

    public void refreshListFolders() {
        refreshListFolders("");
    }

    public void refreshListFolders(final String search) {
        mCurrentFolder = null;
        mCurrentPage = PAGE_FOLDERS;
        if (mFileManager == null) {
            return;
        }

        showProgressBar();
        refreshCurrentList(search);
    }

    public void refreshListAllMusic() {
        mCurrentPage = PAGE_ALL;
        if (mFileManager == null) {
            return;
        }

        showProgressBar();
        refreshCurrentList();
    }

    public void refreshListFoldersInside(final FileModel fileModel) {
        mCurrentFolder = fileModel;
        mCurrentPage = PAGE_FOLDER_INSIDE;
        mFileAudioModels.clear();
        refreshCurrentList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMusicsContentChange() {
        refreshCurrentList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshCurrentList() {
        refreshCurrentList(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshCurrentList(String search) {
        switch (mCurrentPage) {
            case PAGE_ALL:
                mFileAudioManager.getAllLocalMusic(mSortMode, search);
                break;
            case PAGE_FOLDERS:
                mFileAudioManager.getLocalMusicFolders(mSortMode, search);
                break;
            case PAGE_FOLDER_INSIDE:
                mFileAudioManager.getLocalMusic(mCurrentFolder, mSortMode, search);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAdapter() {
        if (mRecyclerView != null && isAdded()) {
            refreshFab();

            final boolean isEmpty = (mFileModels.size() == 0 && mCurrentPage == PAGE_FOLDERS) ||
                    (mFileAudioModels.size() == 0 && mCurrentPage != PAGE_FOLDERS);
            if (isEmpty) {
                mMessageTextView.setText(getString(R.string.no_music));
                mMessageTextView.setVisibility(View.VISIBLE);
            } else {
                mMessageTextView.setVisibility(View.GONE);
            }

            if (mCurrentPage == PAGE_FOLDERS) {
                mFileModelCardAdapter.setList(mFileModels);
            } else {
                mFileAudioRowAdapter.setList(mFileAudioModels);
            }

            updateLayoutManager();
        }
    }

    private void showProgressBar() {
        mProgressBarActivationHandler.postDelayed(mProgressBarActivationRunnable, 200);
    }

    private void hideProgressBar() {
        mProgressBarActivationHandler.removeCallbacks(mProgressBarActivationRunnable);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        mFileAudioManager.unregisterAllLocalMusicListener(this);
        mFileAudioManager.unregisterLocalMusicFoldersListener(this);
        mFileAudioManager.unregisterLocalMusicListener(this);
        mFileAudioManager.unregisterOnMusicUpdateListener(this);
        super.onDestroyView();
    }

    @Override
    public void onAllLocalMusicSucceeded(List<FileAudioModel> fileModels) {
        if (mCurrentPage != PAGE_ALL) {
            return;
        }
        Preconditions.checkNotNull(fileModels);
        hideProgressBar();

        mFileAudioModels.clear();
        mFileAudioModels.addAll(fileModels);
        mFileAudioRowAdapter.setHasHeader(true);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileAudioRowAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mScaleAnimationAdapter.setNoAnimatedPosition(FileAudioLocalFragment.this);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);
        updateAdapter();
    }

    @Override
    public void onAllLocalMusicFailed() {
        if (mCurrentPage != PAGE_ALL) {
            return;
        }
        updateAdapter();
    }

    @Override
    public void onLocalMusicFoldersSucceeded(List<FileModel> fileModels) {
        if (mCurrentPage != PAGE_FOLDERS) {
            return;
        }
        hideProgressBar();

        mFileModels.clear();
        mFileModels.addAll(fileModels);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelCardAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mScaleAnimationAdapter.setNoAnimatedPosition(FileAudioLocalFragment.this);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);
        updateAdapter();
    }

    @Override
    public void onLocalMusicFoldersFailed() {
        if (mCurrentPage != PAGE_FOLDERS) {
            return;
        }
        hideProgressBar();
    }

    @Override
    public void onLocalMusicSucceeded(List<FileAudioModel> fileModels) {
        if (mCurrentPage != PAGE_FOLDER_INSIDE) {
            return;
        }
        mFileAudioModels.clear();
        mFileAudioModels.addAll(fileModels);
        mFileAudioRowAdapter.setHasHeader(false);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileAudioRowAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mScaleAnimationAdapter.setNoAnimatedPosition(FileAudioLocalFragment.this);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);
        updateAdapter();
    }

    @Override
    public void onLocalMusicFailed() {
        if (mCurrentPage != PAGE_FOLDER_INSIDE) {
            return;
        }
        updateAdapter();
    }
}
