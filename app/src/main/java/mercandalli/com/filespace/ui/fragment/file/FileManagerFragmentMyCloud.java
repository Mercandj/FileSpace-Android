/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.fragment.file;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
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
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IModelFileListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.listener.IStringListener;
import mercandalli.com.filespace.model.ModelFile;
import mercandalli.com.filespace.model.ModelFileTypeENUM;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.adapter.AdapterGridModelFile;
import mercandalli.com.filespace.ui.adapter.AdapterModelFile;
import mercandalli.com.filespace.ui.dialog.DialogAddFileManager;
import mercandalli.com.filespace.ui.fragment.Fragment;
import mercandalli.com.filespace.ui.view.DividerItemDecoration;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.StringPair;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;


public class FileManagerFragmentMyCloud extends Fragment {

	private Application app;
	private RecyclerView listView;
    private GridView gridView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterModelFile adapter;
    private ArrayList<ModelFile> files = new ArrayList<>();
	private ProgressBar circularProgressBar;
	private TextView message;
	private SwipeRefreshLayout swipeRefreshLayout, swipeRefreshLayoutGrid;
    Animation animOpen, animZoomOut, animZoomIn; ImageButton circle, circle2;

    private Stack<Integer> id_file_path = new Stack<>();
    private List<ModelFile> filesToCut = new ArrayList<>();
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (Application) activity;
        resetPath();
    }

	public FileManagerFragmentMyCloud() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_filemanager_files, container, false);
        this.circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        this.message = (TextView) rootView.findViewById(R.id.message);

        this.listView = (RecyclerView) rootView.findViewById(R.id.listView);
        this.listView.setHasFixedSize(true);
        this.mLayoutManager = new LinearLayoutManager(getActivity());
        this.listView.setLayoutManager(mLayoutManager);

        this.gridView = (GridView) rootView.findViewById(R.id.gridView);
        this.gridView.setVisibility(View.GONE);

        resetPath();

        this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        this.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        this.swipeRefreshLayoutGrid = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayoutGrid);
        this.swipeRefreshLayoutGrid.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        this.swipeRefreshLayoutGrid.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        this.circle = ((ImageButton) rootView.findViewById(R.id.circle));
        this.circle.setVisibility(View.GONE);
        this.animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
        this.animZoomOut = AnimationUtils.loadAnimation(this.app, R.anim.zoom_out);
        this.animZoomIn = AnimationUtils.loadAnimation(this.app, R.anim.zoom_in);

        this.circle2 = ((ImageButton) rootView.findViewById(R.id.circle2));
        this.circle2.setVisibility(View.GONE);

        this.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filesToCut != null && filesToCut.size() != 0) {
                    for (ModelFile file : filesToCut)
                        file.setId_file_parent(FileManagerFragmentMyCloud.this.id_file_path.peek(), new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                FileManagerFragmentMyCloud.this.app.refreshAdapters();
                            }
                        });
                    filesToCut.clear();
                } else {
                    circle.startAnimation(animZoomOut);
                    FileManagerFragmentMyCloud.this.app.dialog = new DialogAddFileManager(app, FileManagerFragmentMyCloud.this.id_file_path.peek(), new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            if (json != null)
                                refreshList();
                        }
                    }, new IListener() { // Dismiss
                        @Override
                        public void execute() {
                            circle.startAnimation(animZoomIn);
                        }
                    });
                }

                FileManagerFragmentMyCloud.this.updateCircle();
            }
        });

        this.circle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id_file_path.peek() != -1) {
                    FileManagerFragmentMyCloud.this.id_file_path.pop();
                    FileManagerFragmentMyCloud.this.refreshList();
                }
            }
        });

        this.adapter = new AdapterModelFile(app, files, new IModelFileListener() {
            @Override
            public void execute(final ModelFile modelFile) {
                final AlertDialog.Builder menuAleart = new AlertDialog.Builder(FileManagerFragmentMyCloud.this.app);
                String[] menuList = { getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties) };
                if(!modelFile.directory) {
                    if(modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
                        menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", "Set as profile"};
                    }
                    else if(modelFile.type.equals(ModelFileTypeENUM.APK.type) && app.getConfig().isUserAdmin()) {
                        menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", (modelFile.is_apk_update) ? "Remove the update" : "Set as update"};
                    }
                    else
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
                                                FileManagerFragmentMyCloud.this.app.refreshAdapters();
                                            }
                                        });
                                        break;

                                    case 1:
                                        FileManagerFragmentMyCloud.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                            @Override
                                            public void execute(String text) {
                                                modelFile.rename(text, new IPostExecuteListener() {
                                                    @Override
                                                    public void execute(JSONObject json, String body) {
                                                        if(filesToCut != null && filesToCut.size() != 0) {
                                                            filesToCut.clear();
                                                            FileManagerFragmentMyCloud.this.updateCircle();
                                                        }
                                                        FileManagerFragmentMyCloud.this.app.refreshAdapters();
                                                    }
                                                });
                                            }
                                        }, "Cancel", null, modelFile.getNameExt());
                                        break;

                                    case 2:
                                        FileManagerFragmentMyCloud.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                            @Override
                                            public void execute() {
                                                modelFile.delete(new IPostExecuteListener() {
                                                    @Override
                                                    public void execute(JSONObject json, String body) {
                                                        if(filesToCut != null && filesToCut.size() != 0) {
                                                            filesToCut.clear();
                                                            FileManagerFragmentMyCloud.this.updateCircle();
                                                        }
                                                        FileManagerFragmentMyCloud.this.app.refreshAdapters();
                                                    }
                                                });
                                            }
                                        }, "No", null);
                                        break;

                                    case 3:
                                        FileManagerFragmentMyCloud.this.filesToCut.add(modelFile);
                                        Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                        updateCircle();
                                        break;

                                    case 4:
                                        FileManagerFragmentMyCloud.this.app.alert(
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
                                                FileManagerFragmentMyCloud.this.app.refreshAdapters();
                                            }
                                        });
                                        break;

                                    case 6:
                                        // Picture set as profile
                                        if(modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
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
                                        }
                                        else if(modelFile.type.equals(ModelFileTypeENUM.APK.type) && app.getConfig().isUserAdmin()) {
                                            List<StringPair> parameters = new ArrayList<>();
                                            parameters.add(new StringPair("is_apk_update", "" + !modelFile.is_apk_update));
                                            (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeFile + "/"+modelFile.id, new IPostExecuteListener() {
                                                @Override
                                                public void execute(JSONObject json, String body) {
                                                    try {
                                                        if (json != null)
                                                            if (json.has("succeed"))
                                                                if (json.getBoolean("succeed"))
                                                                    FileManagerFragmentMyCloud.this.app.refreshAdapters();
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
            }
        });

        this.listView.setAdapter(adapter);
        this.listView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        this.listView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        this.adapter.setOnItemClickListener(new AdapterModelFile.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hasItemSelected()) {
                    files.get(position).selected = !files.get(position).selected;
                    adapter.notifyItemChanged(position);
                } else if (files.get(position).directory) {
                    FileManagerFragmentMyCloud.this.id_file_path.add(files.get(position).id);
                    refreshList();
                } else
                    files.get(position).executeOnline(files, view);
            }
        });

        this.adapter.setOnItemLongClickListener(new AdapterModelFile.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                files.get(position).selected = !files.get(position).selected;
                adapter.notifyItemChanged(position);
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

    public void updateCircle() {
        if(filesToCut != null && filesToCut.size() != 0)
            this.circle.setImageDrawable(app.getDrawable(R.drawable.ic_menu_paste_holo_dark));
        else
            this.circle.setImageDrawable(app.getDrawable(android.R.drawable.ic_input_add));
    }

	public void refreshList() {
		refreshList(null);
	}

	public void refreshList(String search) {
		List<StringPair> parameters = new ArrayList<>();
		if(search!=null)
			parameters.add(new StringPair("search", ""+search));
        parameters.add(new StringPair("id_file_parent", "" + this.id_file_path.peek()));
        parameters.add(new StringPair("mine", "" + true));

        if(isInternetConnection(app) && app.isLogged()) {
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
        }
        else {
            this.circularProgressBar.setVisibility(View.GONE);
            if(this.isAdded())
                this.message.setText(app.isLogged()?getString(R.string.no_internet_connection):getString(R.string.no_logged));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
            this.swipeRefreshLayoutGrid.setRefreshing(false);

            if(!isInternetConnection(app)) {
                this.setListVisibility(false);
                this.circle.clearAnimation();
                this.circle.setVisibility(View.GONE);
            }
        }
	}

    private void setListVisibility(boolean visible) {
        if(this.listView != null)
            this.listView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        if(this.gridView != null)
            this.gridView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

	public void updateAdapter() {
		if(this.listView!=null && this.files!=null && this.isAdded()) {

            this.circularProgressBar.setVisibility(View.GONE);
            if( ( this.circle.getVisibility()==View.GONE || this.circle.getVisibility()==View.INVISIBLE )&& isInternetConnection(app)) {
                this.circle.setVisibility(View.VISIBLE);
                this.circle.startAnimation(animOpen);
            }

			if(this.files.size()==0) {
                if(this.id_file_path.peek()==-1)
				    this.message.setText(getString(R.string.no_file_server));
                else
                    this.message.setText(getString(R.string.no_file_directory));
				this.message.setVisibility(View.VISIBLE);
			}
			else
				this.message.setVisibility(View.GONE);

            this.adapter.remplaceList(this.files);

            if(this.id_file_path.peek()==-1) {
                this.circle2.setVisibility(View.GONE);
            }
            else {
                this.circle2.setVisibility(View.VISIBLE);
            }

            if(FileManagerFragment.VIEW_MODE == Const.MODE_GRID) {
                this.gridView.setVisibility(View.VISIBLE);
                this.swipeRefreshLayoutGrid.setVisibility(View.VISIBLE);
                this.listView.setVisibility(View.GONE);
                this.swipeRefreshLayout.setVisibility(View.GONE);

                this.gridView.setAdapter(new AdapterGridModelFile(app, files));
                this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (hasItemSelected()) {
                            files.get(position).selected = !files.get(position).selected;
                            adapter.notifyItemChanged(position);
                        } else if (files.get(position).directory) {
                            FileManagerFragmentMyCloud.this.id_file_path.add(files.get(position).id);
                            refreshList();
                        } else
                            files.get(position).executeOnline(files, view);
                    }
                });
                this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position>=files.size())
                            return false;
                        final ModelFile modelFile = files.get(position);
                        final AlertDialog.Builder menuAleart = new AlertDialog.Builder(FileManagerFragmentMyCloud.this.app);
                        String[] menuList = { getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties) };
                        if(!modelFile.directory) {
                            if(modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
                                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", "Set as profile"};
                            }
                            else if(modelFile.type.equals(ModelFileTypeENUM.APK.type) && app.getConfig().isUserAdmin()) {
                                menuList = new String[]{getString(R.string.download), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties), (modelFile._public) ? "Become private" : "Become public", (modelFile.is_apk_update) ? "Remove the update" : "Set as update"};
                            }
                            else
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
                                                        FileManagerFragmentMyCloud.this.app.refreshAdapters();
                                                    }
                                                });
                                                break;

                                            case 1:
                                                FileManagerFragmentMyCloud.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        modelFile.rename(text, new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                if(filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    FileManagerFragmentMyCloud.this.updateCircle();
                                                                }
                                                                FileManagerFragmentMyCloud.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "Cancel", null, modelFile.getNameExt());
                                                break;

                                            case 2:
                                                FileManagerFragmentMyCloud.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        modelFile.delete(new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                if(filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    FileManagerFragmentMyCloud.this.updateCircle();
                                                                }
                                                                FileManagerFragmentMyCloud.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "No", null);
                                                break;

                                            case 3:
                                                FileManagerFragmentMyCloud.this.filesToCut.add(modelFile);
                                                Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                                updateCircle();
                                                break;

                                            case 4:
                                                FileManagerFragmentMyCloud.this.app.alert(
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
                                                        FileManagerFragmentMyCloud.this.app.refreshAdapters();
                                                    }
                                                });
                                                break;

                                            case 6:
                                                // Picture set as profile
                                                if(modelFile.type.equals(ModelFileTypeENUM.PICTURE.type)) {
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
                                                }
                                                else if(modelFile.type.equals(ModelFileTypeENUM.APK.type) && app.getConfig().isUserAdmin()) {
                                                    List<StringPair> parameters = new ArrayList<>();
                                                    parameters.add(new StringPair("is_apk_update", "" + !modelFile.is_apk_update));
                                                    (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeFile + "/"+modelFile.id, new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {
                                                            try {
                                                                if (json != null)
                                                                    if (json.has("succeed"))
                                                                        if (json.getBoolean("succeed"))
                                                                            FileManagerFragmentMyCloud.this.app.refreshAdapters();
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
            }
            else {
                this.gridView.setVisibility(View.GONE);
                this.swipeRefreshLayoutGrid.setVisibility(View.GONE);
                this.listView.setVisibility(View.VISIBLE);
                this.swipeRefreshLayout.setVisibility(View.VISIBLE);
            }

            this.swipeRefreshLayout.setRefreshing(false);
            this.swipeRefreshLayoutGrid.setRefreshing(false);
		}
	}

    @Override
    public boolean back() {
        if(hasItemSelected()) {
            deselectAll();
            return true;
        }
        else if(this.id_file_path.peek()!=-1) {
            FileManagerFragmentMyCloud.this.id_file_path.pop();
            FileManagerFragmentMyCloud.this.refreshList();
            return true;
        }
        else if(filesToCut != null && filesToCut.size() != 0) {
            filesToCut.clear();
            updateCircle();
            return true;
        }
        else
            return false;
    }

    public View getFab() {
        return circle;
    }

    public boolean hasItemSelected() {
        for(ModelFile file:files)
            if(file.selected)
                return true;
        return false;
    }

    public void deselectAll() {
        for(ModelFile file:files)
            file.selected = false;
        updateAdapter();
    }

    @Override
    public void onFocus() {
        refreshList();
    }
}
