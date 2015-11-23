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
package mercandalli.com.filespace.file.cloud;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.common.fragment.BackFragment;
import mercandalli.com.filespace.common.fragment.InjectedFragment;
import mercandalli.com.filespace.common.listener.IListener;
import mercandalli.com.filespace.common.listener.IPostExecuteListener;
import mercandalli.com.filespace.common.listener.IStringListener;
import mercandalli.com.filespace.common.listener.ResultCallback;
import mercandalli.com.filespace.common.net.TaskPost;
import mercandalli.com.filespace.common.util.DialogUtils;
import mercandalli.com.filespace.common.util.FileUtils;
import mercandalli.com.filespace.common.util.NetUtils;
import mercandalli.com.filespace.common.util.StringPair;
import mercandalli.com.filespace.file.FileAddDialog;
import mercandalli.com.filespace.file.FileDivider;
import mercandalli.com.filespace.file.FileManager;
import mercandalli.com.filespace.file.FileModel;
import mercandalli.com.filespace.file.FileModelAdapter;
import mercandalli.com.filespace.file.FileModelGridAdapter;
import mercandalli.com.filespace.file.FileModelListener;
import mercandalli.com.filespace.file.FileTypeModelENUM;
import mercandalli.com.filespace.main.AppComponent;
import mercandalli.com.filespace.main.Config;
import mercandalli.com.filespace.main.Constants;

public class FileMyCloudFragment extends InjectedFragment implements BackFragment.IListViewMode, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private GridView mGridView;
    private FileModelAdapter mFileModelAdapter;
    private final ArrayList<FileModel> mFilesList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mMessageTextView;

    private final Stack<Integer> mIdFileDirectoryStack = new Stack<>();
    private final List<FileModel> mFilesToCutList = new ArrayList<>();

    private int mViewMode = Constants.MODE_LIST;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    FileManager mFileManager;

    public static FileMyCloudFragment newInstance() {
        return new FileMyCloudFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);

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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mGridView.setVisibility(View.GONE);

        resetPath();

        mFileModelAdapter = new FileModelAdapter(mFilesList, new FileModelListener() {
            @Override
            public void executeFileModel(final FileModel fileModel) {
                final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
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
                                        mFileManager.download(mActivity, fileModel, new IListener() {
                                            @Override
                                            public void execute() {
                                                Toast.makeText(mActivity, "Download finished.", Toast.LENGTH_SHORT).show();
                                                mApplicationCallback.refreshData();
                                            }
                                        });
                                        break;

                                    case 1:
                                        DialogUtils.prompt(mActivity, "Rename", "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
                                            @Override
                                            public void execute(String text) {
                                                mFileManager.rename(fileModel, text, new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
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
                                        DialogUtils.alert(mActivity, "Delete", "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
                                            @Override
                                            public void execute() {
                                                mFileManager.delete(fileModel, new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
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
                                        Toast.makeText(mActivity, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                        refreshFab();
                                        break;

                                    case 4:
                                        DialogUtils.alert(mActivity,
                                                getString(R.string.properties) + " : " + fileModel.getName(),
                                                mFileManager.toSpanned(fileModel),
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
                                            (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeUserPut, new IPostExecuteListener() {
                                                @Override
                                                public void onPostExecute(JSONObject json, String body) {
                                                    try {
                                                        if (json != null)
                                                            if (json.has("succeed"))
                                                                if (json.getBoolean("succeed"))
                                                                    mApplicationCallback.getConfig().setUserIdFileProfilePicture(fileModel.getId());
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, parameters)).execute();
                                        } else if (fileModel.getType().equals(FileTypeModelENUM.APK.type) && mApplicationCallback.getConfig().isUserAdmin()) {
                                            List<StringPair> parameters = new ArrayList<>();
                                            parameters.add(new StringPair("is_apk_update", "" + !fileModel.isApkUpdate()));
                                            (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile + "/" + fileModel.getId(), new IPostExecuteListener() {
                                                @Override
                                                public void onPostExecute(JSONObject json, String body) {
                                                    try {
                                                        if (json != null)
                                                            if (json.has("succeed"))
                                                                if (json.getBoolean("succeed"))
                                                                    mApplicationCallback.refreshData();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
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
                    refreshList();
                } else {
                    mFileManager.execute(mActivity, position, mFilesList, view);
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
        mRecyclerView.addItemDecoration(new FileDivider(ContextCompat.getColor(mActivity, R.color.file_divider)));

        refreshList();

        return rootView;
    }

    public void resetPath() {
        this.mIdFileDirectoryStack.clear();
        this.mIdFileDirectoryStack.add(-1);
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(String search) {
        if (!isAdded()) {
            return;
        }

        if (NetUtils.isInternetConnection(mActivity) && mApplicationCallback.isLogged()) {

            mFileManager.getFiles(
                    new FileModel.FileModelBuilder().id(mIdFileDirectoryStack.peek()).build(),
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
                            Toast.makeText(mActivity, mActivity.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });

        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            this.mProgressBar.setVisibility(View.GONE);
            if (this.isAdded())
                this.mMessageTextView.setText(mApplicationCallback.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            this.mMessageTextView.setVisibility(View.VISIBLE);

            if (!NetUtils.isInternetConnection(mActivity)) {
                this.setListVisibility(false);
                refreshFab();
            }
        }
    }

    private void setListVisibility(boolean visible) {
        if (this.mRecyclerView != null)
            this.mRecyclerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        if (this.mGridView != null)
            this.mGridView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void updateAdapter() {
        if (this.mRecyclerView != null && mFilesList != null && this.isAdded() && mActivity != null) {

            this.mProgressBar.setVisibility(View.GONE);

            if (mFilesList.size() == 0) {
                if (this.mIdFileDirectoryStack.peek() == -1)
                    this.mMessageTextView.setText(getString(R.string.no_file_server));
                else
                    this.mMessageTextView.setText(getString(R.string.no_file_directory));
                this.mMessageTextView.setVisibility(View.VISIBLE);
            } else
                this.mMessageTextView.setVisibility(View.GONE);

            mFileModelAdapter.replaceList(mFilesList);

            refreshFab();

            if (mViewMode == Constants.MODE_GRID) {
                this.mGridView.setVisibility(View.VISIBLE);
                this.mRecyclerView.setVisibility(View.GONE);

                this.mGridView.setAdapter(new FileModelGridAdapter(mActivity, mFilesList));
                this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*
                        if (hasItemSelected()) {
                            mFilesList.get(position).selected = !mFilesList.get(position).selected;
                            mFileModelAdapter.notifyItemChanged(position);
                        } else */
                        if (mFilesList.get(position).isDirectory()) {
                            FileMyCloudFragment.this.mIdFileDirectoryStack.add(mFilesList.get(position).getId());
                            refreshList();
                        } else {
                            mFileManager.execute(mActivity, position, mFilesList, view);
                        }
                    }
                });
                this.mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        if (position >= mFilesList.size())
                            return false;
                        final FileModel fileModel = mFilesList.get(position);
                        final AlertDialog.Builder menuAleart = new AlertDialog.Builder(mActivity);
                        String[] menuList = {getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties)};
                        if (!fileModel.isDirectory()) {
                            if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
                                menuList = new String[]{
                                        getString(R.string.download),
                                        getString(R.string.rename),
                                        getString(R.string.delete),
                                        getString(R.string.cut),
                                        getString(R.string.properties),
                                        fileModel.isPublic() ? "Become private" : "Become public", "Set as profile"};
                            } else if (fileModel.getType().equals(FileTypeModelENUM.APK.type) && mApplicationCallback.getConfig().isUserAdmin()) {
                                menuList = new String[]{
                                        getString(R.string.download),
                                        getString(R.string.rename),
                                        getString(R.string.delete),
                                        getString(R.string.cut),
                                        getString(R.string.properties),
                                        (fileModel.isPublic()) ? "Become private" : "Become public",
                                        (fileModel.isApkUpdate()) ? "Remove the update" : "Set as update"};
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
                        menuAleart.setTitle(getString(R.string.action));
                        menuAleart.setItems(menuList,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        switch (item) {
                                            case 0:
                                                mFileManager.delete(fileModel, new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        Toast.makeText(mActivity, "Download finished.", Toast.LENGTH_SHORT).show();
                                                        mApplicationCallback.refreshData();
                                                    }
                                                });
                                                break;

                                            case 1:
                                                DialogUtils.prompt(mActivity, "Rename", "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        mFileManager.rename(fileModel, text, new IListener() {
                                                            @Override
                                                            public void execute() {
                                                                if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
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
                                                DialogUtils.alert(mActivity, "Delete", "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        mFileManager.delete(fileModel, new IListener() {
                                                            @Override
                                                            public void execute() {
                                                                if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
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
                                                mFilesToCutList.add(fileModel);
                                                Toast.makeText(mActivity, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                                refreshFab();
                                                break;

                                            case 4:
                                                DialogUtils.alert(mActivity,
                                                        getString(R.string.properties) + " : " + fileModel.getName(),
                                                        "Name : " + fileModel.getName() + "\nExtension : " + fileModel.getType() + "\nType : " + fileModel.getType().getTitle() + "\nSize : " + FileUtils.humanReadableByteCount(fileModel.getSize()),
                                                        "OK",
                                                        null,
                                                        null,
                                                        null);
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
                                                    (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeUserPut, new IPostExecuteListener() {
                                                        @Override
                                                        public void onPostExecute(JSONObject json, String body) {
                                                            try {
                                                                if (json != null && json.has("succeed"))
                                                                    if (json.getBoolean("succeed"))
                                                                        mApplicationCallback.getConfig().setUserIdFileProfilePicture(fileModel.getId());
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, parameters)).execute();
                                                } else if (fileModel.getType().equals(FileTypeModelENUM.APK.type) && mApplicationCallback.getConfig().isUserAdmin()) {
                                                    List<StringPair> parameters = new ArrayList<>();
                                                    parameters.add(new StringPair("is_apk_update", "" + !fileModel.isApkUpdate()));
                                                    (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile + "/" + fileModel.getId(), new IPostExecuteListener() {
                                                        @Override
                                                        public void onPostExecute(JSONObject json, String body) {
                                                            try {
                                                                if (json != null)
                                                                    if (json.has("succeed"))
                                                                        if (json.getBoolean("succeed"))
                                                                            mApplicationCallback.refreshData();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, parameters)).execute();
                                                }
                                                break;
                                        }
                                    }
                                });
                        AlertDialog menuDrop = menuAleart.create();
                        menuDrop.show();

                        return false;
                    }
                });
            } else {
                this.mGridView.setVisibility(View.GONE);
                this.mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean back() {
        if (hasItemSelected()) {
            deselectAll();
            return true;
        } else if (this.mIdFileDirectoryStack.peek() != -1) {
            FileMyCloudFragment.this.mIdFileDirectoryStack.pop();
            FileMyCloudFragment.this.refreshList();
            return true;
        } else if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
            mFilesToCutList.clear();
            refreshFab();
            return true;
        } else
            return false;
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
        refreshList();
    }

    @Override
    public void onFabClick(int fab_id, final FloatingActionButton fab) {
        switch (fab_id) {
            case 0:
                if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
                    for (FileModel file : mFilesToCutList)
                        mFileManager.setParent(file, FileMyCloudFragment.this.mIdFileDirectoryStack.peek(), new IListener() {
                            @Override
                            public void execute() {
                                mApplicationCallback.refreshData();
                            }
                        });
                    mFilesToCutList.clear();
                } else {
                    fab.hide();
                    new FileAddDialog(mActivity, mApplicationCallback, FileMyCloudFragment.this.mIdFileDirectoryStack.peek(), new IListener() {
                        @Override
                        public void execute() {
                            refreshList();
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
                    FileMyCloudFragment.this.refreshList();
                }
                break;
        }
    }

    @Override
    public boolean isFabVisible(int fab_id) {
        if (mActivity != null && mApplicationCallback != null && (!NetUtils.isInternetConnection(mActivity) || !mApplicationCallback.isLogged()))
            return false;
        switch (fab_id) {
            case 0:
                return true;
            case 1:
                return !(mIdFileDirectoryStack == null || mIdFileDirectoryStack.size() == 0) && this.mIdFileDirectoryStack.peek() != -1;
        }
        return false;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        switch (fab_id) {
            case 0:
                if (mFilesToCutList != null && mFilesToCutList.size() != 0)
                    return R.drawable.ic_menu_paste_holo_dark;
                else
                    return R.drawable.add;
            case 1:
                return R.drawable.arrow_up;
        }
        return R.drawable.add;
    }

    @Override
    public void setViewMode(int viewMode) {
        if (viewMode != mViewMode) {
            mViewMode = viewMode;
            updateAdapter();
        }
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void onRefresh() {
        refreshList();
    }
}
