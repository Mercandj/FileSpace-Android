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
package com.mercandalli.android.apps.files.file.cloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.InjectedFabFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.common.util.NetUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.file.FileAddDialog;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelAdapter;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.local.FileLocalPagerFragment;
import com.mercandalli.android.apps.files.main.FileAppComponent;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

public class FileMyCloudFragment extends InjectedFabFragment implements
        FileLocalPagerFragment.ListController,
        SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private FileModelAdapter mFileModelAdapter;
    private final ArrayList<FileModel> mFilesList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mMessageTextView;

    private final Stack<Integer> mIdFileDirectoryStack = new Stack<>();
    private final List<FileModel> mFilesToCutList = new ArrayList<>();

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    FileManager mFileManager;

    public static FileMyCloudFragment newInstance() {
        return new FileMyCloudFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);
        final Activity activity = getActivity();
        final String succeed = "succeed";

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mMessageTextView = (TextView) rootView.findViewById(R.id.message);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_file_files_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);

        final int nbColumn = getResources().getInteger(R.integer.column_number_card);
        if (nbColumn <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(activity, nbColumn));
        }

        resetPath();

        mFileModelAdapter = new FileModelAdapter(getContext(), mFilesList, new FileModelListener() {
            @Override
            public void executeFileModel(final FileModel fileModel, final View view) {
                final AlertDialog.Builder menuAlert = new AlertDialog.Builder(getContext());
                String[] menuList = {getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties)};
                if (!fileModel.isDirectory()) {
                    if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
                        menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (fileModel.isPublic()) ? "Become private" : "Become public", "Set as profile"};
                    } else if (fileModel.getType().equals(FileTypeModelENUM.APK.type) && mApplicationCallback.getConfig().isUserAdmin()) {
                        menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (fileModel.isPublic()) ? "Become private" : "Become public", (fileModel.isApkUpdate()) ? "Remove the update" : "Set as update"};
                    } else {
                        menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (fileModel.isPublic()) ? "Become private" : "Become public"};
                    }
                }
                menuAlert.setTitle(getString(R.string.action));
                menuAlert.setItems(menuList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        mFileManager.download(getActivity(), fileModel, new IListener() {
                                            @Override
                                            public void execute() {
                                                Toast.makeText(getContext(), "Download finished.", Toast.LENGTH_SHORT).show();
                                                mApplicationCallback.refreshData();
                                            }
                                        });
                                        break;

                                    case 1:
                                        DialogUtils.prompt(getActivity(), "Rename", "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
                                            @Override
                                            public void execute(String text) {
                                                mFileManager.rename(fileModel, text, new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (mFilesToCutList.size() != 0) {
                                                            mFilesToCutList.clear();
                                                            refreshFab();
                                                        }
                                                        mApplicationCallback.refreshData();
                                                    }
                                                });
                                            }
                                        }, "Cancel", null, fileModel.getFullName());
                                        break;

                                    case 2:
                                        DialogUtils.alert(getActivity(), "Delete", "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
                                            @Override
                                            public void execute() {
                                                mFileManager.delete(fileModel, new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (mFilesToCutList.size() != 0) {
                                                            mFilesToCutList.clear();
                                                            refreshFab();
                                                        }
                                                        mApplicationCallback.refreshData();
                                                    }
                                                });
                                            }
                                        }, "No", null);
                                        break;

                                    case 3:
                                        FileMyCloudFragment.this.mFilesToCutList.add(fileModel);
                                        Toast.makeText(getContext(), "File ready to cut.", Toast.LENGTH_SHORT).show();
                                        refreshFab();
                                        break;

                                    case 4:
                                        DialogUtils.alert(getActivity(),
                                                getString(R.string.properties) + " : " + fileModel.getName(),
                                                mFileManager.toSpanned(getContext(), fileModel),
                                                "OK",
                                                null,
                                                null,
                                                null);

                                        Html.fromHtml("");

                                        break;

                                    case 5:
                                        mFileManager.setPublic(fileModel, !fileModel.isPublic(), new IListener() {
                                            @Override
                                            public void execute() {
                                                mApplicationCallback.refreshData();
                                            }
                                        });
                                        break;

                                    case 6:
                                        // Picture set as profile
                                        if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
                                            List<StringPair> parameters = new ArrayList<>();
                                            parameters.add(new StringPair("id_file_profile_picture", "" + fileModel.getId()));
                                            (new TaskPost(getActivity(), mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeUserPut, new IPostExecuteListener() {
                                                @Override
                                                public void onPostExecute(JSONObject json, String body) {
                                                    try {
                                                        if (json != null && json.has(succeed) && json.getBoolean(succeed)) {
                                                            mApplicationCallback.getConfig().setUserIdFileProfilePicture(getActivity(), fileModel.getId());
                                                        }
                                                    } catch (JSONException e) {
                                                        Log.e(getClass().getName(), "Failed to convert Json", e);
                                                    }
                                                }
                                            }, parameters)).execute();
                                        } else if (fileModel.getType().equals(FileTypeModelENUM.APK.type) && mApplicationCallback.getConfig().isUserAdmin()) {
                                            List<StringPair> parameters = new ArrayList<>();
                                            parameters.add(new StringPair("is_apk_update", "" + !fileModel.isApkUpdate()));
                                            (new TaskPost(getActivity(), mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile + "/" + fileModel.getId(), new IPostExecuteListener() {
                                                @Override
                                                public void onPostExecute(JSONObject json, String body) {
                                                    try {
                                                        if (json != null && json.has(succeed) && json.getBoolean(succeed)) {
                                                            mApplicationCallback.refreshData();
                                                        }
                                                    } catch (JSONException e) {
                                                        Log.e(getClass().getName(), "Failed to convert Json", e);
                                                    }
                                                }
                                            }, parameters)).execute();
                                        }
                                        break;

                                }
                            }
                        });
                AlertDialog menuDrop = menuAlert.create();
                menuDrop.show();
            }
        }, new FileModelAdapter.OnFileClickListener() {
            @Override
            public void onFileClick(View view, int position) {
                /*
                if (hasItemSelected()) {
                    mFilesList.get(position).selected = !mFilesList.get(position).selected;
                    mFileModelAdapter.notifyItemChanged(position);
                }
                else
                */
                if (mFilesList.get(position).isDirectory()) {
                    FileMyCloudFragment.this.mIdFileDirectoryStack.add(mFilesList.get(position).getId());
                    refreshCurrentList();
                } else {
                    mFileManager.execute(getActivity(), position, mFilesList, view);
                }
            }
        }, new FileModelAdapter.OnFileLongClickListener() {
            @Override
            public boolean onFileLongClick(View view, int position) {
                /*
                mFilesList.get(position).selected = !mFilesList.get(position).selected;
                mFileModelAdapter.notifyItemChanged(position);
                */
                return true;
            }
        });

        mRecyclerView.setAdapter(mFileModelAdapter);
        mRecyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        //mRecyclerView.addItemDecoration(new FileDivider(ContextCompat.getColor(mActivity, R.color.file_divider)));

        refreshCurrentList();

        return rootView;
    }

    public void resetPath() {
        this.mIdFileDirectoryStack.clear();
        this.mIdFileDirectoryStack.add(-1);
    }

    @Override
    public void refreshCurrentList() {
        refreshCurrentList(null);
    }

    @Override
    public void refreshCurrentList(String search) {
        if (!isAdded()) {
            return;
        }

        if (NetUtils.isInternetConnection(getContext()) && mApplicationCallback.isLogged()) {

            mFileManager.getFiles(
                    new FileModel.FileModelBuilder().id(mIdFileDirectoryStack.peek()).isOnline(true).build(),
                    true,
                    search,
                    Constants.SORT_DATE_MODIFICATION,
                    new ResultCallback<List<FileModel>>() {
                        @Override
                        public void success(List<FileModel> result) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mFilesList.clear();
                            mFilesList.addAll(result);
                            updateAdapter();
                        }

                        @Override
                        public void failure() {
                            Toast.makeText(getContext(), R.string.action_failed, Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });

        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            this.mProgressBar.setVisibility(View.GONE);
            if (this.isAdded()) {
                this.mMessageTextView.setText(mApplicationCallback.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            }
            this.mMessageTextView.setVisibility(View.VISIBLE);

            if (!NetUtils.isInternetConnection(getContext())) {
                this.setListVisibility(false);
                refreshFab();
            }
        }
    }

    private void setListVisibility(boolean visible) {
        if (this.mRecyclerView != null) {
            this.mRecyclerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void updateAdapter() {
        if (this.mRecyclerView != null && this.isAdded()) {

            this.mProgressBar.setVisibility(View.GONE);

            if (mFilesList.size() == 0) {
                if (this.mIdFileDirectoryStack.peek() == -1) {
                    this.mMessageTextView.setText(getString(R.string.no_file_server));
                } else {
                    this.mMessageTextView.setText(getString(R.string.no_file_directory));
                }
                this.mMessageTextView.setVisibility(View.VISIBLE);
            } else {
                this.mMessageTextView.setVisibility(View.GONE);
            }

            mFileModelAdapter.setList(mFilesList);

            refreshFab();
        }
    }

    @Override
    public boolean back() {
        if (hasItemSelected()) {
            deselectAll();
            return true;
        } else if (this.mIdFileDirectoryStack.peek() != -1) {
            FileMyCloudFragment.this.mIdFileDirectoryStack.pop();
            FileMyCloudFragment.this.refreshCurrentList();
            return true;
        } else if (mFilesToCutList.size() != 0) {
            mFilesToCutList.clear();
            refreshFab();
            return true;
        } else {
            return false;
        }
    }

    public boolean hasItemSelected() {
        /*
        for (ModelFile file : mFilesList)
            if (file.selected)
                return true;
                */
        return false;
    }

    public void deselectAll() {
        /*
        for (ModelFile file : mFilesList)
            file.selected = false;
            */
        updateAdapter();
    }

    @Override
    public void onFocus() {
        refreshCurrentList();
    }

    @Override
    public void onFabClick(int fab_id, final FloatingActionButton fab) {
        switch (fab_id) {
            case 0:
                if (mFilesToCutList.size() != 0) {
                    for (FileModel file : mFilesToCutList) {
                        mFileManager.setParent(file, FileMyCloudFragment.this.mIdFileDirectoryStack.peek(), new IListener() {
                            @Override
                            public void execute() {
                                mApplicationCallback.refreshData();
                            }
                        });
                    }
                    mFilesToCutList.clear();
                } else {
                    fab.hide();
                    new FileAddDialog(getActivity(), mApplicationCallback, FileMyCloudFragment.this.mIdFileDirectoryStack.peek(), new IListener() {
                        @Override
                        public void execute() {
                            refreshCurrentList();
                        }
                    }, new IListener() { // Dismiss
                        @Override
                        public void execute() {
                            fab.show();
                        }
                    });
                }
                refreshFab();
                break;
            case 1:
                if (mIdFileDirectoryStack.peek() != -1) {
                    FileMyCloudFragment.this.mIdFileDirectoryStack.pop();
                    FileMyCloudFragment.this.refreshCurrentList();
                }
                break;
        }
    }

    @Override
    public boolean isFabVisible(int fab_id) {
        if (mApplicationCallback != null && (!NetUtils.isInternetConnection(getContext()) || !mApplicationCallback.isLogged())) {
            return false;
        }
        switch (fab_id) {
            case 0:
                return true;
            case 1:
                return !(mIdFileDirectoryStack.size() == 0) && this.mIdFileDirectoryStack.peek() != -1;
        }
        return false;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        switch (fab_id) {
            case 0:
                if (mFilesToCutList.size() != 0) {
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
    protected void inject(FileAppComponent fileAppComponent) {
        fileAppComponent.inject(this);
    }

    @Override
    public void onRefresh() {
        refreshCurrentList();
    }
}
