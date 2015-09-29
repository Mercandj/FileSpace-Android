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
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
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
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.adapter.AdapterDragModelFile;
import mercandalli.com.filespace.ui.adapter.AdapterGridModelFile;
import mercandalli.com.filespace.ui.fragment.FragmentFab;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.StringPair;

public class FileManagerFragmentLocalMusic extends FragmentFab {

	private DynamicListView dynamicListView; // http://nhaarman.github.io/ListViewAnimations/
    private GridView gridView;
	private ArrayList<ModelFile> files;
	private ProgressBar circularProgressBar;
	private TextView message;
	private SwipeRefreshLayout swipeRefreshLayoutGrid;

    private int sortMode = Const.SORT_DATE_MODIFICATION;

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.app = (Application) activity;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filemanager_drag_drop, container, false);
        this.circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        this.circularProgressBar.setVisibility(View.INVISIBLE);
        this.message = (TextView) rootView.findViewById(R.id.message);

        this.dynamicListView = (DynamicListView) rootView.findViewById(R.id.listView);

        this.gridView = (GridView) rootView.findViewById(R.id.gridView);
        this.gridView.setVisibility(View.GONE);

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
        List<File> fs = new ArrayList<>();

        String[] STAR = { "*" };
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = app.getContentResolver().query(allsongsuri, STAR, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String song_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    int song_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                    String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    String album_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    int album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    String artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    int artist_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                    fs.add(new File(fullpath));

                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        if(sortMode == Const.SORT_ABC) {
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
                }
            });
        }
        else if(sortMode == Const.SORT_SIZE) {
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return (new Long(f2.length())).compareTo(f1.length());
                }
            });
        }
        else {
            final Map<File, Long> staticLastModifiedTimes = new HashMap<>();
            for(File f : fs) {
                staticLastModifiedTimes.put(f, f.lastModified());
            }
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return staticLastModifiedTimes.get(f2).compareTo(staticLastModifiedTimes.get(f1));
                }
            });
        }

        files = new ArrayList<>();
        for (File file : fs) {
            ModelFile tmpModelFile = new ModelFile(app, file);
            if(sortMode == Const.SORT_SIZE)
                tmpModelFile.adapterTitleStart = FileUtils.humanReadableByteCount(tmpModelFile.size) + " - ";
            files.add(tmpModelFile);
        }
		
		updateAdapter();		
	}

	public void updateAdapter() {
		if(dynamicListView!=null && files!=null && isAdded()) {

            refreshFab();

			if(files.size()==0) {
				message.setText(getString(R.string.no_music));
				message.setVisibility(View.VISIBLE);
			}
			else
				message.setVisibility(View.GONE);

            final AdapterDragModelFile adapter = new AdapterDragModelFile(app, files, new IModelFileListener() {
				@Override
				public void execute(final ModelFile modelFile) {
					final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileManagerFragmentLocalMusic.this.app);
					String[] menuList = { getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties) };
                    if(app.isLogged())
                        menuList = new String[]{ getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties) };
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {
                                    if(!app.isLogged())
                                        item+=2;
									switch (item) {
                                        case 0:
                                            if(modelFile.directory) {
                                                Toast.makeText(FileManagerFragmentLocalMusic.this.app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                FileManagerFragmentLocalMusic.this.app.alert(getString(R.string.upload), "Upload file " + modelFile.name, getString(R.string.upload), new IListener() {
                                                    @Override
                                                    public void execute() {
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
                                            modelFile.openLocalAs(FileManagerFragmentLocalMusic.this.app);
                                            break;
                                        case 2:
                                            FileManagerFragmentLocalMusic.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    modelFile.rename(text, new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {
                                                            FileManagerFragmentLocalMusic.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "Cancel", null, modelFile.getNameExt());
                                            break;
                                        case 3:
                                            FileManagerFragmentLocalMusic.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                @Override
                                                public void execute() {
                                                    modelFile.delete(new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {
                                                            FileManagerFragmentLocalMusic.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "No", null);
                                            break;
                                        case 4:
                                            FileManagerFragmentLocalMusic.this.app.alert(
                                                    getString(R.string.properties) + " : " + modelFile.name,
                                                    modelFile.toSpanned(),
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
            adapter.setOnItemClickListener(new AdapterDragModelFile.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    files.get(position).executeLocal(files, view);
                }
            });

            dynamicListView.setAdapter(adapter);

            dynamicListView.enableDragAndDrop();
            dynamicListView.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                                       final int position, final long id) {
                            dynamicListView.startDragging(position);
                            return true;
                        }
                    }
            );

            if(FileManagerFragment.VIEW_MODE == Const.MODE_GRID) {
                this.gridView.setVisibility(View.VISIBLE);
                this.swipeRefreshLayoutGrid.setVisibility(View.VISIBLE);
                this.dynamicListView.setVisibility(View.GONE);

                this.gridView.setAdapter(new AdapterGridModelFile(app, files));
                this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        files.get(position).executeLocal(files, view);
                    }
                });
                this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position>=files.size())
                            return false;
                        final ModelFile modelFile = files.get(position);

                        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileManagerFragmentLocalMusic.this.app);
                        String[] menuList = { getString(R.string.rename), getString(R.string.delete), getString(R.string.properties) };
                        if(app.isLogged())
                            menuList = new String[]{ getString(R.string.upload), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties) };
                        menuAlert.setTitle("Action");
                        menuAlert.setItems(menuList,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        if(!app.isLogged())
                                            item--;
                                        switch (item) {
                                            case 0:
                                                if(modelFile.directory) {
                                                    Toast.makeText(FileManagerFragmentLocalMusic.this.app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                    FileManagerFragmentLocalMusic.this.app.alert(getString(R.string.upload), "Upload file " + modelFile.name, getString(R.string.upload), new IListener() {
                                                        @Override
                                                        public void execute() {
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
                                                FileManagerFragmentLocalMusic.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        modelFile.rename(text, new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                FileManagerFragmentLocalMusic.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "Cancel", null, modelFile.getNameExt());
                                                break;
                                            case 2:
                                                FileManagerFragmentLocalMusic.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        modelFile.delete(new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                FileManagerFragmentLocalMusic.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "No", null);
                                                break;
                                            case 3:
                                                FileManagerFragmentLocalMusic.this.app.alert(
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
                this.dynamicListView.setVisibility(View.VISIBLE);
            }

			swipeRefreshLayoutGrid.setRefreshing(false);
		}
	}

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() { }

    @Override
    public void onFabClick(int fab_id, FloatingActionButton fab) {

    }

    @Override
    public boolean isFabVisible(int fab_id) {
        return false;
    }

    @Override
    public Drawable getFabDrawable(int fab_id) {
        return app.getDrawable(R.drawable.add);
    }

    public void setSort(int mode) {
        if(mode == Const.SORT_ABC ||
                mode == Const.SORT_DATE_MODIFICATION ||
                mode == Const.SORT_SIZE) {
            this.sortMode = mode;
            refreshList();
        }
    }
}