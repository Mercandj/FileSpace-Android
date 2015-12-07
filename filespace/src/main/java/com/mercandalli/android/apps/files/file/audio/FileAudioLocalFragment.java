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
import com.mercandalli.android.apps.files.common.fragment.InjectedFragment;
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
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.main.AppComponent;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FileAudioLocalFragment extends InjectedFragment
        implements BackFragment.ISortMode, FileModelCardAdapter.OnFileSubtitleAdapter {

    private RecyclerView mRecyclerView;
    private List<FileModel> mFileModels;
    private ProgressBar mProgressBar;
    private TextView message;

    private FileAudioDragAdapter mFileAudioDragAdapter;
    private FileModelCardAdapter mFileModelCardAdapter;

    private int mSortMode = Constants.SORT_DATE_MODIFICATION;

    private final IListener mRefreshActivityAdapterListener;

    private boolean mIsInsideFolder = false;

    ScaleAnimationAdapter mScaleAnimationAdapter;

    @Inject
    FileManager mFileManager;

    public static FileAudioLocalFragment newInstance() {
        Bundle args = new Bundle();
        FileAudioLocalFragment fragment = new FileAudioLocalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FileAudioLocalFragment() {
        mRefreshActivityAdapterListener = new IListener() {
            @Override
            public void execute() {
                if (mApplicationCallback != null) {
                    mApplicationCallback.refreshData();
                }
            }
        };
    }

    private void updateLayoutManager() {
        if (!mIsInsideFolder) {
            if (getResources().getBoolean(R.bool.is_landscape)) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            }
        } else {
            if (getResources().getBoolean(R.bool.is_landscape)) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            } else {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_drag_drop, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        message = (TextView) rootView.findViewById(R.id.message);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        updateLayoutManager();

        mFileModels = new ArrayList<>();

        mFileAudioDragAdapter = new FileAudioDragAdapter(mActivity, mFileModels, false, new FileModelListener() {
            @Override
            public void executeFileModel(final FileModel fileModel) {
                final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties)};
                if (mApplicationCallback.isLogged())
                    menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                menuAlert.setTitle("Action");
                menuAlert.setItems(menuList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (!mApplicationCallback.isLogged())
                                    item += 2;
                                switch (item) {
                                    case 0:
                                        if (fileModel.isDirectory()) {
                                            Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                        } else
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
        mFileAudioDragAdapter.setOnItemClickListener(new FileAudioDragAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFileModels.get(position).isDirectory()) {
                    refreshList(mFileModels.get(position));
                } else {
                    mFileManager.execute(mActivity, position, mFileModels, view);
                }
            }
        });

        mFileModelCardAdapter = new FileModelCardAdapter(mFileModels, null, new FileModelCardAdapter.OnFileClickListener() {
            @Override
            public void onFileClick(View view, int position) {
                refreshList(mFileModels.get(position));
            }
        }, null);
        mFileModelCardAdapter.setOnFileSubtitleAdapter(this);

        mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileModelCardAdapter);
        mScaleAnimationAdapter.setDuration(220);
        mScaleAnimationAdapter.setOffsetDuration(32);
        mRecyclerView.setAdapter(mScaleAnimationAdapter);

        refreshList();

        mApplicationCallback.invalidateMenu();

        return rootView;
    }

    public void refreshList() {
        refreshList("");
    }

    public void refreshList(final String search) {
        mIsInsideFolder = false;
        if (mFileManager == null) {
            return;
        }
        mFileManager.getLocalMusicFolders(mActivity, mSortMode, search, new ResultCallback<List<FileModel>>() {
            @Override
            public void success(List<FileModel> result) {
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

            }
        });
    }

    public void refreshList(final FileModel fileModel) {
        mIsInsideFolder = true;
        mFileModels.clear();
        mFileManager.getLocalMusic(mActivity, fileModel, mSortMode, null, new ResultCallback<List<FileAudioModel>>() {
            @Override
            public void success(List<FileAudioModel> result) {
                mFileModels.clear();
                mFileModels.addAll(result);

                mScaleAnimationAdapter = new ScaleAnimationAdapter(mRecyclerView, mFileAudioDragAdapter);
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
                message.setText(getString(R.string.no_music));
                message.setVisibility(View.VISIBLE);
            } else
                message.setVisibility(View.GONE);

            if (mIsInsideFolder) {
                mFileAudioDragAdapter.setList(mFileModels);
            } else {
                mFileModelCardAdapter.setList(mFileModels);
            }

            updateLayoutManager();

            /*
            // Extend the Callback class
            ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
                //and in your imlpementaion of
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    if (viewHolder.getAdapterPosition() >= 0 && target.getAdapterPosition() >= 0) {
                        // get the viewHolder's and target's positions in your adapter data, swap them
                        Collections.swap(mFileModels, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        // and notify the adapter that its dataset has changed
                        if (mIsInsideFolder) {
                            mFileAudioDragAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        } else {
                            mFileModelCardAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        }
                    }
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    //TODO
                }

                //defines the enabled move directions in each state (idle, swiping, dragging).
                @Override
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    if (viewHolder instanceof FileAudioDragAdapter.HeaderViewHolder) {
                        return makeFlag(ItemTouchHelper.ACTION_STATE_IDLE,
                                ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                    }
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                }
            };

            // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
            ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
            ith.attachToRecyclerView(mRecyclerView);
            */
        }
    }

    @Override
    public boolean back() {
        if(mIsInsideFolder) {
            refreshList();
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
            refreshList();
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
            refreshList();
        }
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Nullable
    @Override
    public String onFileSubtitleModify(FileModel fileModel) {
        if (fileModel != null && fileModel.isDirectory() && fileModel.getCountAudio() != 0) {
            return "Directory: " + StringUtils.longToShortString(fileModel.getCountAudio()) + " music" + (fileModel.getCountAudio() > 1 ? "s" : "");
        }
        return null;
    }
}
