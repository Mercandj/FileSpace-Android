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
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.fragment.InjectedFabFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.util.StringUtils;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelCardAdapter;
import com.mercandalli.android.apps.files.file.FileModelCardHeaderItem;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.FileAppComponent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A {@link android.support.v4.app.Fragment} that displays the local {@link FileAudioModel}s.
 */
public class FileAudioLocalFragment extends InjectedFabFragment implements
        BackFragment.ISortMode,
        FileModelCardAdapter.OnFileSubtitleAdapter,
        FileModelCardAdapter.OnHeaderClickListener {

    private RecyclerView mRecyclerView;
    private List<FileModel> mFileModels;
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

    private boolean mIsInsideFolder = false;
    private boolean mIsCard = true;

    private ScaleAnimationAdapter mScaleAnimationAdapter;

    /**
     * A simple {@link Handler}. Called by {@link #showProgressBar()} or {@link #hideProgressBar()}.
     */
    private final Handler mProgressBarActivationHandler;

    /**
     * A simple {@link Runnable}. Called by {@link #showProgressBar()} or {@link #hideProgressBar()}.
     */
    private final Runnable mProgressBarActivationRunnable;

    @Inject
    FileManager mFileManager;

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
        if (mIsCard) {
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
        View rootView = inflater.inflate(R.layout.fragment_file_audio_local, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.fragment_file_audio_local_progress_bar);
        mProgressBar.setVisibility(View.GONE);
        mMessageTextView = (TextView) rootView.findViewById(R.id.fragment_file_audio_local_message);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_file_audio_local_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        updateLayoutManager();

        mFileModels = new ArrayList<>();

        mHeaderIds = new ArrayList<>();
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.header_audio_folder, true));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.header_audio_recent, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.header_audio_artist, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.header_audio_album, false));
        mHeaderIds.add(new FileModelCardHeaderItem(R.id.header_audio_all, false));

        mFileAudioRowAdapter = new FileAudioRowAdapter(mHeaderIds, this, mActivity, mFileModels, new FileModelListener() {
            @Override
            public void executeFileModel(final FileModel fileModel) {
                final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties)};
                if (mApplicationCallback.isLogged()) {
                    menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
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
                                        if (fileModel.isDirectory()) {
                                            Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                        } else {
                                            DialogUtils.alert(mActivity, getString(R.string.upload), "Upload file " + fileModel.getName(), getString(R.string.upload), new IListener() {
                                                @Override
                                                public void execute() {
                                                    if (fileModel.getFile() != null) {
                                                        List<StringPair> parameters = FileManager.getForUpload(fileModel);
                                                        (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile, new IPostExecuteListener() {
                                                            @Override
                                                            public void onPostExecute(JSONObject json, String body) {

                                                            }
                                                        }, parameters, fileModel.getFile())).execute();
                                                    }
                                                }
                                            }, getString(R.string.cancel), null);
                                        }
                                        break;
                                    case 1:
                                        mFileManager.openLocalAs(mActivity, fileModel);
                                        break;
                                    case 2:
                                        DialogUtils.prompt(mActivity, "Rename", "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
                                            @Override
                                            public void execute(String text) {
                                                mFileManager.rename(fileModel, text, mRefreshActivityAdapterListener);
                                            }
                                        }, "Cancel", null, fileModel.getFullName());
                                        break;
                                    case 3:
                                        DialogUtils.alert(mActivity, "Delete", "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
                                            @Override
                                            public void execute() {
                                                mFileManager.delete(fileModel, mRefreshActivityAdapterListener);
                                            }
                                        }, "No", null);
                                        break;
                                    case 4:
                                        DialogUtils.alert(mActivity,
                                                getString(R.string.properties) + " : " + fileModel.getName(),
                                                mFileManager.toSpanned(fileModel),
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
                if (mFileModels.get(position).isDirectory()) {
                    refreshListFoldersInside(mFileModels.get(position));
                } else {
                    mFileManager.execute(mActivity, position, mFileModels, view);
                }
            }
        });

        mFileModelCardAdapter = new FileModelCardAdapter(mHeaderIds, this, mFileModels, null, new FileModelCardAdapter.OnFileClickListener() {
            @Override
            public void onFileCardClick(View view, int position) {
                refreshListFoldersInside(mFileModels.get(position));
            }
        }, null);
        mFileModelCardAdapter.setOnFileSubtitleAdapter(this);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelCardAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);

        refreshListFolders();

        mApplicationCallback.invalidateMenu();

        return rootView;
    }

    public void refreshListFolders() {
        refreshListFolders("");
    }

    public void refreshListFolders(final String search) {
        mIsInsideFolder = false;
        mIsCard = true;
        if (mFileManager == null) {
            return;
        }

        showProgressBar();
        mFileManager.getLocalMusicFolders(mActivity, mSortMode, search, new ResultCallback<List<FileModel>>() {
            @Override
            public void success(List<FileModel> result) {
                hideProgressBar();
                if (mFileModels == null) {
                    mFileModels = new ArrayList<>();
                } else {
                    mFileModels.clear();
                }
                mFileModels.addAll(result);

                mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelCardAdapter);
                mScaleAnimationAdapter.setDuration(220);
                mScaleAnimationAdapter.setOffsetDuration(32);
                mRecyclerView.setAdapter(mScaleAnimationAdapter);
                updateAdapter();
            }

            @Override
            public void failure() {
                hideProgressBar();
            }
        });
    }

    public void refreshListAllMusic() {
        mIsInsideFolder = false;
        mIsCard = false;
        if (mFileManager == null) {
            return;
        }

        showProgressBar();
        mFileManager.getLocalMusic(mActivity, mSortMode, null, new ResultCallback<List<FileAudioModel>>() {
            @Override
            public void success(List<FileAudioModel> result) {
                hideProgressBar();
                if (mFileModels == null) {
                    mFileModels = new ArrayList<>();
                } else {
                    mFileModels.clear();
                }
                mFileModels.addAll(result);
                mFileAudioRowAdapter.setHasHeader(true);

                mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileAudioRowAdapter);
                mScaleAnimationAdapter.setDuration(220);
                mScaleAnimationAdapter.setOffsetDuration(32);
                mRecyclerView.setAdapter(mScaleAnimationAdapter);
                updateAdapter();
            }

            @Override
            public void failure() {
                updateAdapter();
            }
        });
    }

    public void refreshListFoldersInside(final FileModel fileModel) {
        mIsInsideFolder = true;
        mIsCard = false;
        mFileModels.clear();
        mFileManager.getLocalMusic(mActivity, fileModel, mSortMode, null, new ResultCallback<List<FileAudioModel>>() {
            @Override
            public void success(List<FileAudioModel> result) {
                mFileModels.clear();
                mFileModels.addAll(result);
                mFileAudioRowAdapter.setHasHeader(false);

                mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileAudioRowAdapter);
                mScaleAnimationAdapter.setDuration(220);
                mScaleAnimationAdapter.setOffsetDuration(32);
                mRecyclerView.setAdapter(mScaleAnimationAdapter);
                updateAdapter();
            }

            @Override
            public void failure() {
                updateAdapter();
            }
        });
    }

    public void updateAdapter() {
        if (mRecyclerView != null && mFileModels != null && isAdded()) {
            refreshFab();

            if (mFileModels.size() == 0) {
                mMessageTextView.setText(getString(R.string.no_music));
                mMessageTextView.setVisibility(View.VISIBLE);
            } else {
                mMessageTextView.setVisibility(View.GONE);
            }

            if (mIsCard) {
                mFileModelCardAdapter.setList(mFileModels);
            } else {
                mFileAudioRowAdapter.setList(mFileModels);
            }

            updateLayoutManager();
        }
    }

    @Override
    public boolean back() {
        if (mIsInsideFolder) {
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
        return fab_id == 0 && mIsInsideFolder;
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
            refreshListFolders();
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
            return "Directory: " + StringUtils.longToShortString(fileModel.getCountAudio()) + " music" + (fileModel.getCountAudio() > 1 ? "s" : "");
        }
        return null;
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
    public boolean onHeaderClick(View v, List<FileModelCardHeaderItem> fileModelCardHeaderItems) {
        mHeaderIds.clear();
        mHeaderIds.addAll(fileModelCardHeaderItems);
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.header_audio_folder:
                refreshListFolders();
                break;
            case R.id.header_audio_recent:
                //TODO
                break;
            case R.id.header_audio_artist:
                //TODO
                break;
            case R.id.header_audio_album:
                //TODO
                break;
            case R.id.header_audio_all:
                refreshListAllMusic();
                break;
        }
        return false;
    }
}
