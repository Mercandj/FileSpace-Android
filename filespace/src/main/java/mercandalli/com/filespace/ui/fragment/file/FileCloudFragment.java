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
package mercandalli.com.filespace.ui.fragment.file;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import javax.inject.Inject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.config.MyAppComponent;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.listener.IStringListener;
import mercandalli.com.filespace.listener.ResultCallback;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.model.ModelFile;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileTypeModelENUM;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.adapter.file.FileModelAdapter;
import mercandalli.com.filespace.ui.adapter.file.FileModelGridAdapter;
import mercandalli.com.filespace.ui.dialog.DialogAddFileManager;
import mercandalli.com.filespace.ui.fragment.BackFragment;
import mercandalli.com.filespace.ui.fragment.FabFragment;
import mercandalli.com.filespace.ui.fragment.InjectedFragment;
import mercandalli.com.filespace.ui.view.DividerItemDecoration;
import mercandalli.com.filespace.util.DialogUtils;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.NetUtils;
import mercandalli.com.filespace.util.StringPair;

/**
 * A {@link FabFragment} used by {@link FileFragment} to display the public cloud {@link ModelFile}.
 */
public class FileCloudFragment extends InjectedFragment implements
        BackFragment.IListViewMode,
        FileModelAdapter.OnItemClickListener,
        FileModelAdapter.OnItemLongClickListener,
        IFileModelListener {

    private RecyclerView mRecyclerView;
    private GridView mGridView;
    private FileModelAdapter mAdapterModelFile;
    private List<FileModel> mFilesList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mMessageTextView;

    private List<FileModel> filesToCut = new ArrayList<>();

    @Inject
    FileManager mFileManager;

    /**
     * {@link Constants#MODE_LIST} or {@link Constants#MODE_GRID}
     */
    private int mViewMode = Constants.MODE_LIST;

    public static FileCloudFragment newInstance() {
        return new FileCloudFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mMessageTextView = (TextView) rootView.findViewById(R.id.message);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mGridView.setVisibility(View.GONE);

        mAdapterModelFile = new FileModelAdapter(mActivity, mFilesList, this);
        mRecyclerView.setAdapter(mAdapterModelFile);
        mRecyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapterModelFile.setOnItemClickListener(this);
        mAdapterModelFile.setOnItemLongClickListener(this);

        refreshList();

        return rootView;
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(String search) {
        if (!isAdded()) {
            return;
        }
        /*
        List<StringPair> parameters = new ArrayList<>();
        if (search != null)
            parameters.add(new StringPair("search", "" + search));
        parameters.add(new StringPair("url", "" + this.url));
        parameters.add(new StringPair("all-public", "" + true));
        */

        if (NetUtils.isInternetConnection(mActivity) && mApplicationCallback.isLogged()) {

            mFileManager.getFiles(
                    new FileModel.FileModelBuilder().id(-1).build(),
                    false,
                    search,
                    Constants.SORT_DATE_MODIFICATION,
                    new ResultCallback<List<FileModel>>() {
                        @Override
                        public void success(List<FileModel> result) {
                            mFilesList.clear();
                            mFilesList.addAll(result);
                            updateAdapter();
                        }

                        @Override
                        public void failure() {

                        }
                    });

            /*
            new TaskGet(
                    mActivity,
                    mApplicationCallback,
                    this.mApplicationCallback.getConfig().getUrlServer() + Config.routeFile,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            if (!isAdded())
                                return;
                            mFilesList = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelFile modelFile = new ModelFile(mActivity, mApplicationCallback, array.getJSONObject(i));
                                            mFilesList.add(modelFile);
                                        }
                                    }
                                } else
                                    Toast.makeText(mActivity, mActivity.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateAdapter();
                        }
                    },
                    parameters
            ).execute();
            */
        } else {
            this.mProgressBar.setVisibility(View.GONE);
            if (isAdded())
                this.mMessageTextView.setText(mApplicationCallback.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            this.mMessageTextView.setVisibility(View.VISIBLE);

            if (!NetUtils.isInternetConnection(mActivity)) {
                this.setListVisibility(false);
                this.refreshFab.execute();
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
        if (mRecyclerView != null && mFilesList != null && this.isAdded() && mActivity != null) {

            mProgressBar.setVisibility(View.GONE);

            if (!NetUtils.isInternetConnection(mActivity))
                mMessageTextView.setText(getString(R.string.no_internet_connection));
            else if (mFilesList.size() == 0) {
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
            } else
                this.mMessageTextView.setVisibility(View.GONE);

            this.mAdapterModelFile.replaceList(mFilesList);

            this.refreshFab.execute();

            if (mViewMode == Constants.MODE_GRID) {
                this.mGridView.setVisibility(View.VISIBLE);
                this.mRecyclerView.setVisibility(View.GONE);

                this.mGridView.setAdapter(new FileModelGridAdapter(mActivity, mFilesList));
                this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*if (hasItemSelected()) {
                            mFilesList.get(position).selected = !mFilesList.get(position).selected;
                            mAdapterModelFile.notifyItemChanged(position);
                        } else */
                        if (mFilesList.get(position).isDirectory()) {
                            Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                            //FileCloudFragment.this.url = mFilesList.get(position).getUrl() + "/";
                            refreshList();
                        } else {
                            mFileManager.executeOnline(mActivity, mFilesList.get(position), mFilesList, view);
                        }
                    }
                });
                this.mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= mFilesList.size())
                            return false;
                        final FileModel fileModel = mFilesList.get(position);

                        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                        String[] menuList = {getString(R.string.download)};
                        if (!fileModel.isDirectory() && mFileManager.isMine(fileModel)) {
                            if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
                                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (fileModel.isPublic()) ? "Become private" : "Become public", "Set as profile"};
                            } else
                                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (fileModel.isPublic()) ? "Become private" : "Become public"};
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
                                                        mApplicationCallback.refreshAdapters();
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
                                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    refreshFab.execute();
                                                                }
                                                                mApplicationCallback.refreshAdapters();
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
                                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    refreshFab.execute();
                                                                }
                                                                mApplicationCallback.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "No", null);
                                                break;

                                            case 3:
                                                FileCloudFragment.this.filesToCut.add(fileModel);
                                                Toast.makeText(mActivity, "File ready to cut.", Toast.LENGTH_SHORT).show();
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
                                                        mApplicationCallback.refreshAdapters();
                                                    }
                                                });
                                                break;

                                            // Picture set as profile
                                            case 6:
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
                                                break;
                                        }
                                    }
                                });
                        AlertDialog menuDrop = menuAlert.create();
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
        } else if (filesToCut != null && filesToCut.size() != 0) {
            filesToCut.clear();
            refreshFab.execute();
            return true;
        }
        return false;
    }

    @Override
    public void onFocus() {
        refreshList();
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

    @Override
    public void onFabClick(int fab_id, final FloatingActionButton fab) {
        switch (fab_id) {
            case 0:
                fab.hide();
                new DialogAddFileManager(mActivity, mApplicationCallback, -1, new IListener() {
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
                break;

            case 1:
                //FileCloudFragment.this.url = "";
                Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                FileCloudFragment.this.refreshList();
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
                return false;
        }
        return false;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        switch (fab_id) {
            case 0:
                if (filesToCut != null && filesToCut.size() != 0)
                    return R.drawable.ic_menu_paste_holo_dark;
                else
                    return R.drawable.add;
            case 1:
                return R.drawable.arrow_up;
        }
        return android.R.drawable.ic_input_add;
    }

    @Override
    public void setViewMode(int viewMode) {
        if (viewMode != mViewMode) {
            mViewMode = viewMode;
            updateAdapter();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        /*if (hasItemSelected()) {
            mFilesList.get(position).selected = !mFilesList.get(position).selected;
            mAdapterModelFile.notifyItemChanged(position);
        } else */
        if (mFilesList.get(position).isDirectory()) {
            //FileCloudFragment.this.url = mFilesList.get(position).getUrl() + "/";
            Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
            refreshList();
        } else {
            mFileManager.executeOnline(mActivity, mFilesList.get(position), mFilesList, view);
        }
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        /*mFilesList.get(position).selected = !mFilesList.get(position).selected;
        mAdapterModelFile.notifyItemChanged(position);
        */
        return true;
    }

    @Override
    public void executeFileModel(final FileModel fileModel) {
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
        String[] menuList = {getString(R.string.download)};
        if (!fileModel.isDirectory() && mFileManager.isMine(fileModel)) {
            if (fileModel.getType().equals(FileTypeModelENUM.PICTURE.type)) {
                menuList = new String[]{
                        getString(R.string.download),
                        getString(R.string.rename),
                        getString(R.string.delete),
                        getString(R.string.cut),
                        getString(R.string.properties),
                        (fileModel.isPublic()) ? "Become private" : "Become public", "Set as profile"};
            } else
                menuList = new String[]{
                        getString(R.string.download),
                        getString(R.string.rename),
                        getString(R.string.delete),
                        getString(R.string.cut),
                        getString(R.string.properties),
                        (fileModel.isPublic()) ? "Become private" : "Become public"};
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
                                        mApplicationCallback.refreshAdapters();
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
                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                    filesToCut.clear();
                                                    refreshFab.execute();
                                                }
                                                mApplicationCallback.refreshAdapters();
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
                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                    filesToCut.clear();
                                                    refreshFab.execute();
                                                }
                                                mApplicationCallback.refreshAdapters();
                                            }
                                        });
                                    }
                                }, "No", null);
                                break;

                            case 3:
                                FileCloudFragment.this.filesToCut.add(fileModel);
                                Toast.makeText(mActivity, "File ready to cut.", Toast.LENGTH_SHORT).show();
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

                            case 5:
                                mFileManager.setPublic(fileModel, !fileModel.isPublic(), new IListener() {
                                    @Override
                                    public void execute() {
                                        mApplicationCallback.refreshAdapters();
                                    }
                                });
                                break;

                            // Picture set as profile
                            case 6:
                                List<StringPair> parameters = new ArrayList<>();
                                parameters.add(new StringPair("id_file_profile_picture", "" + fileModel.getId()));
                                (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUserPut, new IPostExecuteListener() {
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
                                break;
                        }
                    }
                });
        AlertDialog menuDrop = menuAlert.create();
        menuDrop.show();
    }

    @Override
    protected void inject(MyAppComponent myAppComponent) {
        myAppComponent.inject(this);
    }
}
