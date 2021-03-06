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
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.file.FileAddDialog;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileModelAdapter;
import com.mercandalli.android.apps.files.file.FileModelListener;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.cloud.fab.FileCloudFabManager;
import com.mercandalli.android.apps.files.file.local.FileLocalPagerFragment;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.library.base.dialog.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link android.support.v4.app.Fragment} used by {@link FileLocalPagerFragment} to buildDisplay the public cloud {@link FileModel}.
 */
public class FileCloudFragment extends BackFragment implements
        FileLocalPagerFragment.ListController,
        FileModelAdapter.OnFileClickListener,
        FileModelAdapter.OnFileLongClickListener,
        FileModelListener,
        SwipeRefreshLayout.OnRefreshListener,
        FileCloudFabManager.FabController {

    /**
     * A key for the view pager position.
     */
    private static final String ARG_POSITION_IN_VIEW_PAGER = "FileCloudFragment.Args.ARG_POSITION_IN_VIEW_PAGER";

    @NonNull
    private final List<FileModel> mFilesList = new ArrayList<>();

    @NonNull
    private final List<FileModel> mFilesToCut = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private FileModelAdapter mAdapterModelFile;

    private ProgressBar mProgressBar;
    private TextView mMessageTextView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mPositionInViewPager;

    private FileManager mFileManager;
    private FileCloudFabManager mFileCloudFabManager;

    public static FileCloudFragment newInstance(final int positionInViewPager) {
        final FileCloudFragment fileCloudFragment = new FileCloudFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_POSITION_IN_VIEW_PAGER, positionInViewPager);
        fileCloudFragment.setArguments(args);
        return fileCloudFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (!args.containsKey(ARG_POSITION_IN_VIEW_PAGER)) {
            throw new IllegalStateException("Missing args. Please use newInstance()");
        }
        mFileManager = FileManager.getInstance(getContext());
        mFileCloudFabManager = FileCloudFabManager.getInstance();
        mPositionInViewPager = args.getInt(ARG_POSITION_IN_VIEW_PAGER);
        mFileCloudFabManager.addFabController(mPositionInViewPager, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);
        final Activity activity = getActivity();

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mMessageTextView = (TextView) rootView.findViewById(R.id.message);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_file_files_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_file_files_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        final int nbColumn = getResources().getInteger(R.integer.column_number_card);
        if (nbColumn <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(activity, nbColumn));
        }

        mAdapterModelFile = new FileModelAdapter(getContext(), mFilesList, this, this, this);
        mRecyclerView.setAdapter(mAdapterModelFile);
        mRecyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        //mRecyclerView.addItemDecoration(new FileDivider(ContextCompat.getColor(mActivity, R.color.file_divider)));

        refreshCurrentList();

        return rootView;
    }

    @Override
    public boolean back() {
        if (hasItemSelected()) {
            deselectAll();
            return true;
        } else if (!mFilesToCut.isEmpty()) {
            mFilesToCut.clear();
            //refreshFab();
            return true;
        }
        return false;
    }


    @Override
    public void onFabClick(
            final @IntRange(from = 0, to = FileCloudFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition,
            final @NonNull FloatingActionButton floatingActionButton) {
        switch (fabPosition) {
            case 0:
                FileCloudFragment.this.mFileCloudFabManager.updateFabButtons();
                new FileAddDialog(getActivity(), -1, new IListener() {
                    @Override
                    public void execute() {
                        refreshCurrentList();
                    }
                }, new IListener() { // Dismiss
                    @Override
                    public void execute() {
                        FileCloudFragment.this.mFileCloudFabManager.updateFabButtons();
                    }
                });
                break;

            case 1:
                //FileCloudFragment.this.url = "";
                Toast.makeText(getActivity(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                FileCloudFragment.this.refreshCurrentList();
                break;
        }
    }

    @Override
    public boolean isFabVisible(
            final @IntRange(from = 0, to = FileCloudFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition) {
        return false;
    }

    @Override
    public int getFabImageResource(
            final @IntRange(from = 0, to = FileCloudFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition) {
        switch (fabPosition) {
            case 0:
                if (!mFilesToCut.isEmpty()) {
                    return R.drawable.ic_menu_paste_holo_dark;
                } else {
                    return R.drawable.add;
                }
            case 1:
                return R.drawable.arrow_up;
        }
        return android.R.drawable.ic_input_add;
    }

    @Override
    public void onFileClick(View view, int position) {
        /*if (hasItemSelected()) {
            mFilesList.get(position).selected = !mFilesList.get(position).selected;
            mAdapterModelFile.notifyItemChanged(position);
        } else */
        if (mFilesList.get(position).isDirectory()) {
            //FileCloudFragment.this.url = mFilesList.get(position).getUrl() + "/";
            Toast.makeText(getActivity(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
            refreshCurrentList();
        } else {
            mFileManager.execute(getActivity(), position, mFilesList, view);
        }
    }

    @Override
    public boolean onFileLongClick(View view, int position) {
        /*mFilesList.get(position).selected = !mFilesList.get(position).selected;
        mAdapterModelFile.notifyItemChanged(position);
        */
        return true;
    }

    @Override
    public void executeFileModel(final FileModel fileModel, final View view) {
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(getActivity());
        String[] menuList = {getString(R.string.download)};
        if (!fileModel.isDirectory() && mFileManager.isMine(fileModel)) {
            if (FileTypeModelENUM.IMAGE.type.equals(fileModel.getType())) {
                menuList = new String[]{
                        getString(R.string.download),
                        getString(R.string.rename),
                        getString(R.string.delete),
                        getString(R.string.cut),
                        getString(R.string.properties),
                        (fileModel.isPublic()) ? "Become private" : "Become public", "Set as profile"};
            } else {
                menuList = new String[]{
                        getString(R.string.download),
                        getString(R.string.rename),
                        getString(R.string.delete),
                        getString(R.string.cut),
                        getString(R.string.properties),
                        (fileModel.isPublic()) ? "Become private" : "Become public"};
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
                                        Toast.makeText(getActivity(), "Download finished.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            case 1:
                                DialogUtils.prompt(
                                        getActivity(),
                                        "Rename",
                                        "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?",
                                        "Ok",
                                        new DialogUtils.OnDialogUtilsStringListener() {
                                            @Override
                                            public void onDialogUtilsStringCalledBack(String text) {
                                                mFileManager.rename(fileModel, text, new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (!mFilesToCut.isEmpty()) {
                                                            mFilesToCut.clear();
                                                            //refreshFab();
                                                        }
                                                    }
                                                });
                                            }
                                        }, "Cancel", null, fileModel.getFullName());
                                break;

                            case 2:
                                DialogUtils.alert(
                                        getContext(),
                                        "Delete",
                                        "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?",
                                        "Yes",
                                        new DialogUtils.OnDialogUtilsListener() {
                                            @Override
                                            public void onDialogUtilsCalledBack() {
                                                mFileManager.delete(fileModel, new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (!mFilesToCut.isEmpty()) {
                                                            mFilesToCut.clear();
                                                            //refreshFab();
                                                        }
                                                    }
                                                });
                                            }
                                        }, "No", null);
                                break;

                            case 3:
                                FileCloudFragment.this.mFilesToCut.add(fileModel);
                                Toast.makeText(getContext(), "File ready to cut.", Toast.LENGTH_SHORT).show();
                                break;

                            case 4:
                                DialogUtils.alert(getContext(),
                                        getString(R.string.properties) + " : " + fileModel.getName(),
                                        mFileManager.toSpanned(getActivity(), fileModel),
                                        "OK",
                                        null,
                                        null,
                                        null);
                                break;

                            case 5:
                                mFileManager.setPublic(fileModel, !fileModel.isPublic(), new IListener() {
                                    @Override
                                    public void execute() {
                                    }
                                });
                                break;

                            // Picture set as profile
                            case 6:
                                List<StringPair> parameters = new ArrayList<>();
                                parameters.add(new StringPair("id_file_profile_picture", "" + fileModel.getId()));
                                (new TaskPost(getActivity(), Constants.URL_DOMAIN + Config.ROUTE_USER_PUT, new IPostExecuteListener() {
                                    @Override
                                    public void onPostExecute(JSONObject json, String body) {
                                        try {
                                            if (json != null && json.has("succeed") && json.getBoolean("succeed")) {
                                                Config.setUserIdFileProfilePicture(getActivity(), fileModel.getId());
                                            }
                                        } catch (JSONException e) {
                                            Log.e(getClass().getName(), "Failed to convert Json", e);
                                        }
                                    }
                                }, parameters)).execute();
                                break;
                        }
                    }
                });
        AlertDialog menuDrop = menuAlert.create();
        menuDrop.show();
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
    public void refreshCurrentList() {
        if (!isAdded()) {
            return;
        }

        if (NetUtils.isInternetConnection(getContext()) && Config.isLogged()) {
            mFileManager.getFiles(
                    new FileModel.FileModelBuilder().id(-1).isOnline(true).build(),
                    false,
                    false,
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
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            this.mProgressBar.setVisibility(View.GONE);
            if (isAdded()) {
                this.mMessageTextView.setText(Config.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            }
            this.mMessageTextView.setVisibility(View.VISIBLE);

            if (!NetUtils.isInternetConnection(getContext())) {
                this.setListVisibility(false);
                //refreshFab();
            }
        }
    }

    public void updateAdapter() {
        if (mRecyclerView != null && this.isAdded()) {

            mProgressBar.setVisibility(View.GONE);

            if (!NetUtils.isInternetConnection(getContext())) {
                mMessageTextView.setText(getString(R.string.no_internet_connection));
            } else if (mFilesList.isEmpty()) {
                /*
                if (this.url == null)
                    this.mMessageTextView.setText(getString(R.string.no_file_server));
                else if (this.url.equals(""))
                    this.mMessageTextView.setText(getString(R.string.no_file_server));
                else
                    this.mMessageTextView.setText(getString(R.string.no_file_directory));
                    */
                mMessageTextView.setText(getString(R.string.no_file_server));
                mMessageTextView.setVisibility(View.VISIBLE);
            } else {
                this.mMessageTextView.setVisibility(View.GONE);
            }

            mAdapterModelFile.setList(mFilesList);

            //refreshFab();
        }
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
        updateAdapter();
        */
    }

    private void setListVisibility(boolean visible) {
        if (this.mRecyclerView != null) {
            this.mRecyclerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
