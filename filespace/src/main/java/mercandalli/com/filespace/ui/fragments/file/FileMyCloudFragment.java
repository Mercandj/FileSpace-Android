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
import android.text.Html;
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
import java.util.Stack;

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

public class FileMyCloudFragment extends FabFragment implements BackFragment.IListViewMode {

    private RecyclerView mRecyclerView;
    private GridView gridView;
    private AdapterModelFile mAdapterModelFile;
    private ArrayList<ModelFile> files = new ArrayList<>();
    private ProgressBar circularProgressBar;
    private TextView message;

    private Stack<Integer> id_file_path = new Stack<>();
    private List<ModelFile> filesToCut = new ArrayList<>();

    private int mViewMode = Const.MODE_LIST;

    public static FileMyCloudFragment newInstance() {
        return new FileMyCloudFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (ApplicationDrawerActivity) activity;
        resetPath();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);

        this.circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        this.message = (TextView) rootView.findViewById(R.id.message);

        this.mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.gridView = (GridView) rootView.findViewById(R.id.gridView);
        this.gridView.setVisibility(View.GONE);

        resetPath();

        this.mAdapterModelFile = new AdapterModelFile(app, files, new IModelFileListener() {
            @Override
            public void execute(final ModelFile modelFile) {
                final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileMyCloudFragment.this.app);
                String[] menuList = {getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties)};
                if (!modelFile.directory) {
                    if (modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
                        menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", "Set as profile"};
                    } else if (modelFile.type.equals(ModelFileTypeENUM.APK.type) && app.getConfig().isUserAdmin()) {
                        menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", (modelFile.is_apk_update) ? "Remove the update" : "Set as update"};
                    } else
                        menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public"};
                }
                menuAlert.setTitle(getString(R.string.action));
                menuAlert.setItems(menuList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        modelFile.download(new IListener() {
                                            @Override
                                            public void execute() {
                                                Toast.makeText(app, "Download finished.", Toast.LENGTH_SHORT).show();
                                                FileMyCloudFragment.this.app.refreshAdapters();
                                            }
                                        });
                                        break;

                                    case 1:
                                        FileMyCloudFragment.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                            @Override
                                            public void execute(String text) {
                                                modelFile.rename(text, new IPostExecuteListener() {
                                                    @Override
                                                    public void execute(JSONObject json, String body) {
                                                        if (filesToCut != null && filesToCut.size() != 0) {
                                                            filesToCut.clear();
                                                            refreshFab();
                                                        }
                                                        FileMyCloudFragment.this.app.refreshAdapters();
                                                    }
                                                });
                                            }
                                        }, "Cancel", null, modelFile.getNameExt());
                                        break;

                                    case 2:
                                        FileMyCloudFragment.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                            @Override
                                            public void execute() {
                                                modelFile.delete(new IPostExecuteListener() {
                                                    @Override
                                                    public void execute(JSONObject json, String body) {
                                                        if (filesToCut != null && filesToCut.size() != 0) {
                                                            filesToCut.clear();
                                                            refreshFab();
                                                        }
                                                        FileMyCloudFragment.this.app.refreshAdapters();
                                                    }
                                                });
                                            }
                                        }, "No", null);
                                        break;

                                    case 3:
                                        FileMyCloudFragment.this.filesToCut.add(modelFile);
                                        Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                        refreshFab();
                                        break;

                                    case 4:
                                        FileMyCloudFragment.this.app.alert(
                                                getString(R.string.properties) + " : " + modelFile.name,
                                                modelFile.toSpanned(),
                                                "OK",
                                                null,
                                                null,
                                                null);

                                        Html.fromHtml("");

                                        break;

                                    case 5:
                                        modelFile.setPublic(!modelFile._public, new IPostExecuteListener() {
                                            @Override
                                            public void execute(JSONObject json, String body) {
                                                FileMyCloudFragment.this.app.refreshAdapters();
                                            }
                                        });
                                        break;

                                    case 6:
                                        // Picture set as profile
                                        if (modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
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
                                        } else if (modelFile.type.equals(ModelFileTypeENUM.APK.type) && app.getConfig().isUserAdmin()) {
                                            List<StringPair> parameters = new ArrayList<>();
                                            parameters.add(new StringPair("is_apk_update", "" + !modelFile.is_apk_update));
                                            (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeFile + "/" + modelFile.id, new IPostExecuteListener() {
                                                @Override
                                                public void execute(JSONObject json, String body) {
                                                    try {
                                                        if (json != null)
                                                            if (json.has("succeed"))
                                                                if (json.getBoolean("succeed"))
                                                                    FileMyCloudFragment.this.app.refreshAdapters();
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
        });

        this.mRecyclerView.setAdapter(mAdapterModelFile);
        this.mRecyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        this.mAdapterModelFile.setOnItemClickListener(new AdapterModelFile.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hasItemSelected()) {
                    files.get(position).selected = !files.get(position).selected;
                    mAdapterModelFile.notifyItemChanged(position);
                } else if (files.get(position).directory) {
                    FileMyCloudFragment.this.id_file_path.add(files.get(position).id);
                    refreshList();
                } else
                    files.get(position).executeOnline(files, view);
            }
        });

        this.mAdapterModelFile.setOnItemLongClickListener(new AdapterModelFile.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                files.get(position).selected = !files.get(position).selected;
                mAdapterModelFile.notifyItemChanged(position);
                return true;
            }
        });

        refreshList();

        return rootView;
    }

    public void resetPath() {
        this.id_file_path = new Stack<>();
        this.id_file_path.add(-1);
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(String search) {
        List<StringPair> parameters = new ArrayList<>();
        if (search != null)
            parameters.add(new StringPair("search", "" + search));
        parameters.add(new StringPair("id_file_parent", "" + this.id_file_path.peek()));
        parameters.add(new StringPair("mine", "" + true));

        if (NetUtils.isInternetConnection(app) && app.isLogged()) {
            new TaskGet(
                    app,
                    this.app.getConfig().getUser(),
                    this.app.getConfig().getUrlServer() + this.app.getConfig().routeFile,
                    new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            if (!isAdded())
                                return;
                            files = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelFile modelFile = new ModelFile(app, array.getJSONObject(i));
                                            files.add(modelFile);
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
        } else {
            this.circularProgressBar.setVisibility(View.GONE);
            if (this.isAdded())
                this.message.setText(app.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            this.message.setVisibility(View.VISIBLE);

            if (!NetUtils.isInternetConnection(app)) {
                this.setListVisibility(false);
                refreshFab();
            }
        }
    }

    private void setListVisibility(boolean visible) {
        if (this.mRecyclerView != null)
            this.mRecyclerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        if (this.gridView != null)
            this.gridView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void updateAdapter() {
        if (this.mRecyclerView != null && this.files != null && this.isAdded()) {

            this.circularProgressBar.setVisibility(View.GONE);

            if (this.files.size() == 0) {
                if (this.id_file_path.peek() == -1)
                    this.message.setText(getString(R.string.no_file_server));
                else
                    this.message.setText(getString(R.string.no_file_directory));
                this.message.setVisibility(View.VISIBLE);
            } else
                this.message.setVisibility(View.GONE);

            this.mAdapterModelFile.remplaceList(this.files);

            refreshFab();

            if (mViewMode == Const.MODE_GRID) {
                this.gridView.setVisibility(View.VISIBLE);
                this.mRecyclerView.setVisibility(View.GONE);

                this.gridView.setAdapter(new AdapterGridModelFile(app, files));
                this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (hasItemSelected()) {
                            files.get(position).selected = !files.get(position).selected;
                            mAdapterModelFile.notifyItemChanged(position);
                        } else if (files.get(position).directory) {
                            FileMyCloudFragment.this.id_file_path.add(files.get(position).id);
                            refreshList();
                        } else
                            files.get(position).executeOnline(files, view);
                    }
                });
                this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= files.size())
                            return false;
                        final ModelFile modelFile = files.get(position);
                        final AlertDialog.Builder menuAleart = new AlertDialog.Builder(FileMyCloudFragment.this.app);
                        String[] menuList = {getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties)};
                        if (!modelFile.directory) {
                            if (modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
                                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", "Set as profile"};
                            } else if (modelFile.type.equals(ModelFileTypeENUM.APK.type) && app.getConfig().isUserAdmin()) {
                                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", (modelFile.is_apk_update) ? "Remove the update" : "Set as update"};
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
                                                        FileMyCloudFragment.this.app.refreshAdapters();
                                                    }
                                                });
                                                break;

                                            case 1:
                                                FileMyCloudFragment.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        modelFile.rename(text, new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    refreshFab();
                                                                }
                                                                FileMyCloudFragment.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "Cancel", null, modelFile.getNameExt());
                                                break;

                                            case 2:
                                                FileMyCloudFragment.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        modelFile.delete(new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    refreshFab();
                                                                }
                                                                FileMyCloudFragment.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "No", null);
                                                break;

                                            case 3:
                                                FileMyCloudFragment.this.filesToCut.add(modelFile);
                                                Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                                refreshFab();
                                                break;

                                            case 4:
                                                FileMyCloudFragment.this.app.alert(
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
                                                        FileMyCloudFragment.this.app.refreshAdapters();
                                                    }
                                                });
                                                break;

                                            case 6:
                                                // Picture set as profile
                                                if (modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
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
                                                } else if (modelFile.type.equals(ModelFileTypeENUM.APK.type) && app.getConfig().isUserAdmin()) {
                                                    List<StringPair> parameters = new ArrayList<>();
                                                    parameters.add(new StringPair("is_apk_update", "" + !modelFile.is_apk_update));
                                                    (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeFile + "/" + modelFile.id, new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {
                                                            try {
                                                                if (json != null)
                                                                    if (json.has("succeed"))
                                                                        if (json.getBoolean("succeed"))
                                                                            FileMyCloudFragment.this.app.refreshAdapters();
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
                this.gridView.setVisibility(View.GONE);
                this.mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean back() {
        if (hasItemSelected()) {
            deselectAll();
            return true;
        } else if (this.id_file_path.peek() != -1) {
            FileMyCloudFragment.this.id_file_path.pop();
            FileMyCloudFragment.this.refreshList();
            return true;
        } else if (filesToCut != null && filesToCut.size() != 0) {
            filesToCut.clear();
            refreshFab();
            return true;
        } else
            return false;
    }

    public boolean hasItemSelected() {
        for (ModelFile file : files)
            if (file.selected)
                return true;
        return false;
    }

    public void deselectAll() {
        for (ModelFile file : files)
            file.selected = false;
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
                if (filesToCut != null && filesToCut.size() != 0) {
                    for (ModelFile file : filesToCut)
                        file.setId_file_parent(FileMyCloudFragment.this.id_file_path.peek(), new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                FileMyCloudFragment.this.app.refreshAdapters();
                            }
                        });
                    filesToCut.clear();
                } else {
                    fab.hide();
                    FileMyCloudFragment.this.app.dialog = new DialogAddFileManager(app, FileMyCloudFragment.this.id_file_path.peek(), new IPostExecuteListener() {
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
                }
                refreshFab();
                break;
            case 1:
                if (id_file_path.peek() != -1) {
                    FileMyCloudFragment.this.id_file_path.pop();
                    FileMyCloudFragment.this.refreshList();
                }
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
                return this.id_file_path.peek() != -1;
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
        return R.drawable.add;
    }

    @Override
    public void setViewMode(int viewMode) {
        if (viewMode != mViewMode) {
            mViewMode = viewMode;
            updateAdapter();
        }
    }
}
