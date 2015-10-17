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
package mercandalli.com.filespace.ui.fragments.file;

import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IModelFileListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.listeners.IStringListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.models.ModelFileTypeENUM;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activities.ApplicationDrawerActivity;
import mercandalli.com.filespace.ui.adapters.AdapterGridModelFile;
import mercandalli.com.filespace.ui.adapters.AdapterModelFile;
import mercandalli.com.filespace.ui.dialogs.DialogAddFileManager;
import mercandalli.com.filespace.ui.fragments.BackFragment;
import mercandalli.com.filespace.ui.fragments.FabFragment;
import mercandalli.com.filespace.ui.views.DividerItemDecoration;
import mercandalli.com.filespace.utils.FileUtils;
import mercandalli.com.filespace.utils.NetUtils;
import mercandalli.com.filespace.utils.StringPair;

/**
 * A {@link FabFragment} used by {@link FileFragment} to display the public cloud {@link ModelFile}.
 */
public class FileCloudFragment extends FabFragment implements
        BackFragment.IListViewMode,
        AdapterModelFile.OnItemClickListener,
        AdapterModelFile.OnItemLongClickListener,
        IModelFileListener {

    private RecyclerView mRecyclerView;
    private GridView mGridView;
    private AdapterModelFile mAdapterModelFile;
    private ArrayList<ModelFile> mFilesList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mMessageTextView;

    private String url = "";
    private List<ModelFile> filesToCut = new ArrayList<>();

    /**
     * {@link Const#MODE_LIST} or {@link Const#MODE_GRID}
     */
    private int mViewMode = Const.MODE_LIST;

    public static FileCloudFragment newInstance() {
        return new FileCloudFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (ApplicationDrawerActivity) activity;
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

        mAdapterModelFile = new AdapterModelFile(app, mFilesList, this);
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
        List<StringPair> parameters = new ArrayList<>();
        if (search != null)
            parameters.add(new StringPair("search", "" + search));
        parameters.add(new StringPair("url", "" + this.url));
        parameters.add(new StringPair("all-public", "" + true));

        if (NetUtils.isInternetConnection(app) && app.isLogged())
            new TaskGet(
                    app,
                    this.app.getConfig().getUser(),
                    this.app.getConfig().getUrlServer() + this.app.getConfig().routeFile,
                    new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            if (!isAdded())
                                return;
                            mFilesList = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelFile modelFile = new ModelFile(app, array.getJSONObject(i));
                                            mFilesList.add(modelFile);
                                        }
                                    }
                                } else
                                    Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateAdapter();
                        }
                    },
                    parameters
            ).execute();
        else {
            this.mProgressBar.setVisibility(View.GONE);
            if (isAdded())
                this.mMessageTextView.setText(app.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            this.mMessageTextView.setVisibility(View.VISIBLE);

            if (!NetUtils.isInternetConnection(app)) {
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
        if (mRecyclerView != null && mFilesList != null && this.isAdded()) {

            mProgressBar.setVisibility(View.GONE);

            if (!NetUtils.isInternetConnection(app))
                mMessageTextView.setText(getString(R.string.no_internet_connection));
            else if (mFilesList.size() == 0) {
                if (this.url == null)
                    this.mMessageTextView.setText(getString(R.string.no_file_server));
                else if (this.url.equals(""))
                    this.mMessageTextView.setText(getString(R.string.no_file_server));
                else
                    this.mMessageTextView.setText(getString(R.string.no_file_directory));
                this.mMessageTextView.setVisibility(View.VISIBLE);
            } else
                this.mMessageTextView.setVisibility(View.GONE);

            this.mAdapterModelFile.remplaceList(mFilesList);

            this.refreshFab.execute();

            if (mViewMode == Const.MODE_GRID) {
                this.mGridView.setVisibility(View.VISIBLE);
                this.mRecyclerView.setVisibility(View.GONE);

                this.mGridView.setAdapter(new AdapterGridModelFile(app, mFilesList));
                this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (hasItemSelected()) {
                            mFilesList.get(position).selected = !mFilesList.get(position).selected;
                            mAdapterModelFile.notifyItemChanged(position);
                        } else if (mFilesList.get(position).directory) {
                            FileCloudFragment.this.url = mFilesList.get(position).url + "/";
                            refreshList();
                        } else
                            mFilesList.get(position).executeOnline(mFilesList, view);
                    }
                });
                this.mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= mFilesList.size())
                            return false;
                        final ModelFile modelFile = mFilesList.get(position);

                        final AlertDialog.Builder menuAleart = new AlertDialog.Builder(FileCloudFragment.this.app);
                        String[] menuList = {getString(R.string.download)};
                        if (!modelFile.directory && modelFile.isMine()) {
                            if (modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
                                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", "Set as profile"};
                            } else
                                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public"};
                        }
                        menuAleart.setTitle(getString(R.string.action));
                        menuAleart.setItems(menuList,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        switch (item) {
                                            case 0:
                                                modelFile.download(new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        Toast.makeText(app, "Download finished.", Toast.LENGTH_SHORT).show();
                                                        FileCloudFragment.this.app.refreshAdapters();
                                                    }
                                                });
                                                break;

                                            case 1:
                                                FileCloudFragment.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        modelFile.rename(text, new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    refreshFab.execute();
                                                                }
                                                                FileCloudFragment.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "Cancel", null, modelFile.getNameExt());
                                                break;

                                            case 2:
                                                FileCloudFragment.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        modelFile.delete(new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    refreshFab.execute();
                                                                }
                                                                FileCloudFragment.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "No", null);
                                                break;

                                            case 3:
                                                FileCloudFragment.this.filesToCut.add(modelFile);
                                                Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                                break;

                                            case 4:
                                                FileCloudFragment.this.app.alert(
                                                        getString(R.string.properties) + " : " + modelFile.name,
                                                        "Name : " + modelFile.name + "\nExtension : " + modelFile.type + "\nType : " + modelFile.type.getTitle() + "\nSize : " + FileUtils.humanReadableByteCount(modelFile.size),
                                                        "OK",
                                                        null,
                                                        null,
                                                        null);
                                                break;

                                            case 5:
                                                modelFile.setPublic(!modelFile._public, new IPostExecuteListener() {
                                                    @Override
                                                    public void execute(JSONObject json, String body) {
                                                        FileCloudFragment.this.app.refreshAdapters();
                                                    }
                                                });
                                                break;

                                            // Picture set as profile
                                            case 6:
                                                List<StringPair> parameters = new ArrayList<>();
                                                parameters.add(new StringPair("id_file_profile_picture", "" + modelFile.id));
                                                (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeUserPut, new IPostExecuteListener() {
                                                    @Override
                                                    public void execute(JSONObject json, String body) {
                                                        try {
                                                            if (json != null)
                                                                if (json.has("succeed"))
                                                                    if (json.getBoolean("succeed"))
                                                                        app.getConfig().setUserIdFileProfilePicture(modelFile.id);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, parameters)).execute();
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
        for (ModelFile file : mFilesList)
            if (file.selected)
                return true;
        return false;
    }

    public void deselectAll() {
        for (ModelFile file : mFilesList)
            file.selected = false;
        updateAdapter();
    }

    @Override
    public void onFabClick(int fab_id, final FloatingActionButton fab) {
        switch (fab_id) {
            case 0:
                fab.hide();
                FileCloudFragment.this.app.dialog = new DialogAddFileManager(app, -1, new IPostExecuteListener() {
                    @Override
                    public void execute(JSONObject json, String body) {
                        if (json != null)
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
                FileCloudFragment.this.url = "";
                FileCloudFragment.this.refreshList();
                break;
        }
    }

    @Override
    public boolean isFabVisible(int fab_id) {
        if (!NetUtils.isInternetConnection(app) || !app.isLogged())
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
        if (hasItemSelected()) {
            mFilesList.get(position).selected = !mFilesList.get(position).selected;
            mAdapterModelFile.notifyItemChanged(position);
        } else if (mFilesList.get(position).directory) {
            FileCloudFragment.this.url = mFilesList.get(position).url + "/";
            refreshList();
        } else
            mFilesList.get(position).executeOnline(mFilesList, view);
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        mFilesList.get(position).selected = !mFilesList.get(position).selected;
        mAdapterModelFile.notifyItemChanged(position);
        return true;
    }

    @Override
    public void executeModelFile(final ModelFile modelFile) {
        final AlertDialog.Builder menuAleart = new AlertDialog.Builder(FileCloudFragment.this.app);
        String[] menuList = {getString(R.string.download)};
        if (!modelFile.directory && modelFile.isMine()) {
            if (modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", "Set as profile"};
            } else
                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public"};
        }
        menuAleart.setTitle(getString(R.string.action));
        menuAleart.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                modelFile.download(new IListener() {
                                    @Override
                                    public void execute() {
                                        Toast.makeText(app, "Download finished.", Toast.LENGTH_SHORT).show();
                                        FileCloudFragment.this.app.refreshAdapters();
                                    }
                                });
                                break;

                            case 1:
                                FileCloudFragment.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                    @Override
                                    public void execute(String text) {
                                        modelFile.rename(text, new IPostExecuteListener() {
                                            @Override
                                            public void execute(JSONObject json, String body) {
                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                    filesToCut.clear();
                                                    refreshFab.execute();
                                                }
                                                FileCloudFragment.this.app.refreshAdapters();
                                            }
                                        });
                                    }
                                }, "Cancel", null, modelFile.getNameExt());
                                break;

                            case 2:
                                FileCloudFragment.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                    @Override
                                    public void execute() {
                                        modelFile.delete(new IPostExecuteListener() {
                                            @Override
                                            public void execute(JSONObject json, String body) {
                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                    filesToCut.clear();
                                                    refreshFab.execute();
                                                }
                                                FileCloudFragment.this.app.refreshAdapters();
                                            }
                                        });
                                    }
                                }, "No", null);
                                break;

                            case 3:
                                FileCloudFragment.this.filesToCut.add(modelFile);
                                Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                break;

                            case 4:
                                FileCloudFragment.this.app.alert(
                                        getString(R.string.properties) + " : " + modelFile.name,
                                        modelFile.toSpanned(),
                                        "OK",
                                        null,
                                        null,
                                        null);
                                break;

                            case 5:
                                modelFile.setPublic(!modelFile._public, new IPostExecuteListener() {
                                    @Override
                                    public void execute(JSONObject json, String body) {
                                        FileCloudFragment.this.app.refreshAdapters();
                                    }
                                });
                                break;

                            // Picture set as profile
                            case 6:
                                List<StringPair> parameters = new ArrayList<>();
                                parameters.add(new StringPair("id_file_profile_picture", "" + modelFile.id));
                                (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeUserPut, new IPostExecuteListener() {
                                    @Override
                                    public void execute(JSONObject json, String body) {
                                        try {
                                            if (json != null)
                                                if (json.has("succeed"))
                                                    if (json.getBoolean("succeed"))
                                                        app.getConfig().setUserIdFileProfilePicture(modelFile.id);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, parameters)).execute();
                                break;
                        }
                    }
                });
        AlertDialog menuDrop = menuAleart.create();
        menuDrop.show();
    }
}
