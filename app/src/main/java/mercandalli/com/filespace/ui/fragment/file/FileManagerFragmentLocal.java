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
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IModelFileListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.listener.IStringListener;
import mercandalli.com.filespace.model.ModelFile;
import mercandalli.com.filespace.model.ModelFileType;
import mercandalli.com.filespace.model.ModelFileTypeENUM;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.adapter.AdapterGridModelFile;
import mercandalli.com.filespace.ui.adapter.AdapterModelFile;
import mercandalli.com.filespace.ui.fragment.Fragment;
import mercandalli.com.filespace.ui.view.DividerItemDecoration;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.StringPair;

public class FileManagerFragmentLocal extends Fragment {
	
	private Application app;
	private RecyclerView listView;
    private GridView gridView;
    private RecyclerView.LayoutManager mLayoutManager;
	private ArrayList<ModelFile> files;
	private ProgressBar circulerProgressBar;
	private File jarvisDirectory;
	private TextView message;
	private SwipeRefreshLayout swipeRefreshLayout, swipeRefreshLayoutGrid;
    Animation animOpen; ImageButton circle, circle2;

    private List<ModelFile> filesToCut = new ArrayList<>();

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.app = (Application) activity;
    }
	
	public FileManagerFragmentLocal() {
		super();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filemanager_files, container, false);
        this.circulerProgressBar = (ProgressBar) rootView.findViewById(R.id.circulerProgressBar);
        this.circulerProgressBar.setVisibility(View.INVISIBLE);
        this.message = (TextView) rootView.findViewById(R.id.message);

        this.listView = (RecyclerView) rootView.findViewById(R.id.listView);
        this.listView.setHasFixedSize(true);
        this.mLayoutManager = new LinearLayoutManager(getActivity());
        this.listView.setLayoutManager(mLayoutManager);
        this.listView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        this.gridView = (GridView) rootView.findViewById(R.id.gridView);
        this.gridView.setVisibility(View.GONE);

        this.circle = (ImageButton) rootView.findViewById(R.id.circle);
        this.circle.setVisibility(View.GONE);
        this.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filesToCut != null && filesToCut.size() != 0) {
                    for (ModelFile file : filesToCut) {
                        file.renameLocalByPath(jarvisDirectory.getAbsolutePath() + File.separator + file.getNameExt());
                    }
                    filesToCut.clear();
                    refreshList();
                } else {
                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileManagerFragmentLocal.this.app);
                    final String[] menuList = {"New Folder or File"};
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            FileManagerFragmentLocal.this.app.prompt("New Folder or File", "Choose a file name with ext or a folder name.", getString(R.string.ok), new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    createFile(jarvisDirectory.getPath() + File.separator, text);
                                                    refreshList();
                                                }
                                            }, getString(R.string.cancel), null, null, "Name");
                                            break;
                                    }
                                }
                            });
                    AlertDialog menuDrop = menuAlert.create();
                    menuDrop.show();
                }
                FileManagerFragmentLocal.this.updateCircle();
            }
        });

        this.animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
        this.circle2 = (ImageButton) rootView.findViewById(R.id.circle2);
        this.circle2.setVisibility(View.GONE);
        this.circle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jarvisDirectory.getParent() != null) {
                    FileManagerFragmentLocal.this.jarvisDirectory = new File(jarvisDirectory.getParentFile().getPath());
                    //Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+FileManagerFragmentLocal.this.app.getConfig().localFolderName);
                    FileManagerFragmentLocal.this.refreshList();
                }
            }
        });

        this.jarvisDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+this.app.getConfig().localFolderName);
		if(!jarvisDirectory.exists())
			jarvisDirectory.mkdir();

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
		
		refreshList();

        this.app.invalidateOptionsMenu();

        return rootView;
    }

    public void refreshList() {
        refreshList(null);
    }
	
	public void refreshList(final String search) {
		if(jarvisDirectory==null)
			return;

        List<File> fs = Arrays.asList((search==null) ? jarvisDirectory.listFiles() : jarvisDirectory.listFiles(
            new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().contains(search.toLowerCase());
                }
            }
        ));

        final Map<File, Long> staticLastModifiedTimes = new HashMap<File,Long>();
        for(final File f : fs) {
            staticLastModifiedTimes.put(f, f.lastModified());
        }
        Collections.sort(fs, new Comparator<File>() {
            @Override
            public int compare(final File f1, final File f2) {
                return staticLastModifiedTimes.get(f2).compareTo(staticLastModifiedTimes.get(f1));
            }
        });

        files = new ArrayList<ModelFile>();
		if(fs!=null) {
            int tmp_id = 0;
            for (File file : fs) {
                ModelFile modelFile = new ModelFile(app);
                modelFile.id = file.hashCode()+tmp_id;
                modelFile.url = file.getAbsolutePath();
                int id= file.getName().lastIndexOf(".");
                modelFile.name = (id==-1) ? file.getName() : file.getName().substring(0, id);
                modelFile.type = new ModelFileType(FileUtils.getExtensionFromPath(file.getAbsolutePath()));
                modelFile.setFile(file);
                files.add(modelFile);
                tmp_id++;
            }
        }
		
		updateAdapter();		
	}
	
	public void updateAdapter() {
		if(listView!=null && files!=null && isAdded()) {

            if( circle.getVisibility()==View.GONE ) {
                circle.setVisibility(View.VISIBLE);
                circle.startAnimation(animOpen);
            }

			if(files.size()==0) {
				message.setText(getString(R.string.no_file_local));
				message.setVisibility(View.VISIBLE);
			}
			else
				message.setVisibility(View.GONE);

            final AdapterModelFile adapter = new AdapterModelFile(app, files, new IModelFileListener() {
				@Override
				public void execute(final ModelFile modelFile) {
					final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileManagerFragmentLocal.this.app);
					String[] menuList = { getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties) };
                    if(app.isLogged())
                        menuList = new String[]{ getString(R.string.upload), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties) };
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {
                                    if(!app.isLogged())
                                        item--;
									switch (item) {
                                        case 0:
                                            if(modelFile.directory) {
                                                Toast.makeText(FileManagerFragmentLocal.this.app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                FileManagerFragmentLocal.this.app.alert(getString(R.string.upload), "Upload file " + modelFile.name, getString(R.string.upload), new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if(modelFile != null)
                                                            if(modelFile.getFile()!=null) {
                                                                List<StringPair> parameters = modelFile.getForUpload();
                                                                (new TaskPost(app, app.getConfig().getUrlServer()+app.getConfig().routeFile, new IPostExecuteListener() {
                                                                    @Override
                                                                    public void execute(JSONObject json, String body) {

                                                                    }
                                                                }, parameters, modelFile.getFile())).execute();
                                                            }
                                                    }
                                                }, getString(R.string.cancel), null);
                                            break;
                                        case 1:
                                            FileManagerFragmentLocal.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    modelFile.rename(text, new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {
                                                            if(filesToCut != null && filesToCut.size() != 0) {
                                                                filesToCut.clear();
                                                                FileManagerFragmentLocal.this.updateCircle();
                                                            }
                                                            FileManagerFragmentLocal.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "Cancel", null, modelFile.getNameExt());
                                            break;
                                        case 2:
                                            FileManagerFragmentLocal.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                @Override
                                                public void execute() {
                                                    modelFile.delete(new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {
                                                            if(filesToCut != null && filesToCut.size() != 0) {
                                                                filesToCut.clear();
                                                                FileManagerFragmentLocal.this.updateCircle();
                                                            }
                                                            FileManagerFragmentLocal.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "No", null);
                                            break;
                                        case 3:
                                            FileManagerFragmentLocal.this.filesToCut.add(modelFile);
                                            Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                            updateCircle();
                                            break;
                                        case 4:
                                            FileManagerFragmentLocal.this.app.alert(
                                                    getString(R.string.properties) + " : " + modelFile.name,
                                                    "Name : " + modelFile.name + "\nExtension : " + modelFile.type + "\nType : " + modelFile.type.getTitle() + "\nSize : " + FileUtils.humanReadableByteCount(modelFile.size),
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

            listView.setAdapter(adapter);

            adapter.setOnItemClickListener(new AdapterModelFile.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if(hasItemSelected()) {
                        files.get(position).selected = !files.get(position).selected;
                        adapter.notifyItemChanged(position);
                    }
                    else if (files.get(position).directory) {
                        jarvisDirectory = new File(files.get(position).url);
                        refreshList();
                    } else
                        files.get(position).executeLocal(files, view);
                }
            });

            adapter.setOnItemLongClickListener(new AdapterModelFile.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(View view, int position) {
                    files.get(position).selected = !files.get(position).selected;
                    adapter.notifyItemChanged(position);
                    return true;
                }
            });

            if(this.jarvisDirectory==null)
                this.circle2.setVisibility(View.GONE);
            /*else if(this.jarvisDirectory.getPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + app.getConfig().localFolderName))
                this.circle2.setVisibility(View.GONE);*/
            else
                this.circle2.setVisibility(View.VISIBLE);



            if(FileManagerFragment.VIEW_MODE == Const.MODE_GRID) {
                this.gridView.setVisibility(View.VISIBLE);
                this.swipeRefreshLayoutGrid.setVisibility(View.VISIBLE);
                this.listView.setVisibility(View.GONE);
                this.swipeRefreshLayout.setVisibility(View.GONE);

                this.gridView.setAdapter(new AdapterGridModelFile(app, files));
                this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(hasItemSelected()) {
                            files.get(position).selected = !files.get(position).selected;
                            adapter.notifyItemChanged(position);
                        }
                        else if (files.get(position).directory) {
                            jarvisDirectory = new File(files.get(position).url);
                            refreshList();
                        } else
                            files.get(position).executeLocal(files, view);
                    }
                });
                this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position>=files.size())
                            return false;
                        final ModelFile modelFile = files.get(position);

                        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileManagerFragmentLocal.this.app);
                        String[] menuList = { getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties) };
                        if(app.isLogged())
                            menuList = new String[]{ getString(R.string.upload), getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties) };
                        menuAlert.setTitle("Action");
                        menuAlert.setItems(menuList,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        if(!app.isLogged())
                                            item--;
                                        switch (item) {
                                            case 0:
                                                if(modelFile.directory) {
                                                    Toast.makeText(FileManagerFragmentLocal.this.app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                    FileManagerFragmentLocal.this.app.alert(getString(R.string.upload), "Upload file " + modelFile.name, getString(R.string.upload), new IListener() {
                                                        @Override
                                                        public void execute() {
                                                            if(modelFile != null)
                                                                if(modelFile.getFile()!=null) {
                                                                    List<StringPair> parameters = modelFile.getForUpload();
                                                                    (new TaskPost(app, app.getConfig().getUrlServer()+app.getConfig().routeFile, new IPostExecuteListener() {
                                                                        @Override
                                                                        public void execute(JSONObject json, String body) {

                                                                        }
                                                                    }, parameters, modelFile.getFile())).execute();
                                                                }
                                                        }
                                                    }, getString(R.string.cancel), null);
                                                break;
                                            case 1:
                                                FileManagerFragmentLocal.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        modelFile.rename(text, new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    FileManagerFragmentLocal.this.updateCircle();
                                                                }
                                                                FileManagerFragmentLocal.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "Cancel", null, modelFile.getNameExt());
                                                break;
                                            case 2:
                                                FileManagerFragmentLocal.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        modelFile.delete(new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                if (filesToCut != null && filesToCut.size() != 0) {
                                                                    filesToCut.clear();
                                                                    FileManagerFragmentLocal.this.updateCircle();
                                                                }
                                                                FileManagerFragmentLocal.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "No", null);
                                                break;
                                            case 3:
                                                FileManagerFragmentLocal.this.filesToCut.add(modelFile);
                                                Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                                updateCircle();
                                                break;
                                            case 4:
                                                FileManagerFragmentLocal.this.app.alert(
                                                        getString(R.string.properties) + " : " + modelFile.name,
                                                        "Name : " + modelFile.name + "\nExtension : " + modelFile.type + "\nType : " + modelFile.type.getTitle() + "\nSize : " + FileUtils.humanReadableByteCount(modelFile.size),
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

			swipeRefreshLayout.setRefreshing(false);
			swipeRefreshLayoutGrid.setRefreshing(false);
		}
	}

    public boolean createFile(String path, String name) {
        int len = path.length();
        if (len < 1 || name.length() < 1)
            return false;
        if (path.charAt(len - 1) != '/')
            path += "/";
        if(!name.contains(".")) {
            if (new File(path + name).mkdir())
                return true;
        }
        else {
            try {
                if (new File(path + name).createNewFile())
                    return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean back() {
        if(hasItemSelected()) {
            deselectAll();
            return true;
        }
        else if(!jarvisDirectory.getPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+this.app.getConfig().localFolderName)) {
            if(jarvisDirectory.getParent() != null) {
                FileManagerFragmentLocal.this.jarvisDirectory = new File(jarvisDirectory.getParentFile().getPath());
                FileManagerFragmentLocal.this.refreshList();
                return true;
            }
        }
        else if(filesToCut != null && filesToCut.size() != 0) {
            filesToCut.clear();
            updateCircle();
            return true;
        }
        return false;
    }

    public View getFab() {
        return circle;
    }

    public void goHome() {
        this.jarvisDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + app.getConfig().localFolderName);
        this.refreshList();
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

    public void updateCircle() {
        if(filesToCut != null && filesToCut.size() != 0)
            this.circle.setImageDrawable(app.getDrawable(R.drawable.ic_menu_paste_holo_dark));
        else
            this.circle.setImageDrawable(app.getDrawable(android.R.drawable.ic_input_add));
    }
}
