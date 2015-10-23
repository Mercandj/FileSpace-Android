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
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IModelFileListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.listeners.IStringListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.models.MusicModelFile;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activities.ApplicationCallback;
import mercandalli.com.filespace.ui.adapters.AdapterDragMusicModelFile;
import mercandalli.com.filespace.ui.adapters.AdapterGridModelFile;
import mercandalli.com.filespace.ui.fragments.BackFragment;
import mercandalli.com.filespace.ui.fragments.FabFragment;
import mercandalli.com.filespace.utils.DialogUtils;
import mercandalli.com.filespace.utils.FileUtils;
import mercandalli.com.filespace.utils.StringPair;

public class FileLocalMusicFragment extends FabFragment
        implements BackFragment.IListViewMode, BackFragment.ISortMode {

    private RecyclerView mRecyclerView; // http://nhaarman.github.io/ListViewAnimations/
    private GridView mGridView;
    private ArrayList<MusicModelFile> files;
    private ProgressBar mProgressBar;
    private TextView message;

    private int mSortMode = Constants.SORT_DATE_MODIFICATION;
    private int mViewMode = Constants.MODE_LIST;

    private Activity mActivity;
    private ApplicationCallback mApplicationCallback;

    public static FileLocalMusicFragment newInstance() {
        Bundle args = new Bundle();
        FileLocalMusicFragment fragment = new FileLocalMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        if (context instanceof ApplicationCallback) {
            mApplicationCallback = (ApplicationCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mApplicationCallback = null;
        app = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_drag_drop, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        message = (TextView) rootView.findViewById(R.id.message);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        if (getResources().getBoolean(R.bool.is_tablet)) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mGridView.setVisibility(View.GONE);

        refreshList();

        mApplicationCallback.invalidateMenu();

        return rootView;
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(final String search) {
        if (files == null) {
            files = new ArrayList<>();
        } else {
            files.clear();
        }

        String[] STAR = {"*"};

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] searchArray = null;
        if (search != null) {
            searchArray = new String[]{"%" + search + "%"};
            selection += " AND " + MediaStore.Audio.Media.DISPLAY_NAME + " LIKE ?";
        }

        Cursor cursor = mActivity.getContentResolver().query(allsongsuri, STAR, selection, searchArray, null);

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

                    MusicModelFile modelFile = new MusicModelFile(mActivity, mApplicationCallback, new File(fullpath));
                    modelFile.setAlbum(album_name);
                    modelFile.setArtist(artist_name);
                    if (mSortMode == Constants.SORT_SIZE)
                        modelFile.adapterTitleStart = FileUtils.humanReadableByteCount(modelFile.size) + " - ";
                    files.add(modelFile);

                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        if (mSortMode == Constants.SORT_ABC) {
            Collections.sort(files, new Comparator<ModelFile>() {
                @Override
                public int compare(final ModelFile f1, final ModelFile f2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
                }
            });
        } else if (mSortMode == Constants.SORT_SIZE) {
            Collections.sort(files, new Comparator<ModelFile>() {
                @Override
                public int compare(final ModelFile f1, final ModelFile f2) {
                    return (new Long(f2.length())).compareTo(f1.length());
                }
            });
        } else {
            final Map<ModelFile, Long> staticLastModifiedTimes = new HashMap<>();
            for (ModelFile f : files) {
                staticLastModifiedTimes.put(f, f.lastModified());
            }
            Collections.sort(files, new Comparator<ModelFile>() {
                @Override
                public int compare(final ModelFile f1, final ModelFile f2) {
                    return staticLastModifiedTimes.get(f2).compareTo(staticLastModifiedTimes.get(f1));
                }
            });
        }

        updateAdapter();
    }

    public void updateAdapter() {
        if (mRecyclerView != null && files != null && isAdded()) {

            refreshFab();

            if (files.size() == 0) {
                message.setText(getString(R.string.no_music));
                message.setVisibility(View.VISIBLE);
            } else
                message.setVisibility(View.GONE);

            final AdapterDragMusicModelFile adapter = new AdapterDragMusicModelFile(mActivity, files, new IModelFileListener() {
                @Override
                public void executeModelFile(final ModelFile modelFile) {
                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileLocalMusicFragment.this.app);
                    String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties)};
                    if (app.isLogged())
                        menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    if (!mApplicationCallback.isLogged())
                                        item += 2;
                                    switch (item) {
                                        case 0:
                                            if (modelFile.directory) {
                                                Toast.makeText(FileLocalMusicFragment.this.app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                            } else
                                                DialogUtils.alert(mActivity, getString(R.string.upload), "Upload file " + modelFile.name, getString(R.string.upload), new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (modelFile.getFile() != null) {
                                                            List<StringPair> parameters = modelFile.getForUpload();
                                                            (new TaskPost(mActivity, mApplicationCallback, app.getConfig().getUrlServer() + app.getConfig().routeFile, new IPostExecuteListener() {
                                                                @Override
                                                                public void onPostExecute(JSONObject json, String body) {

                                                                }
                                                            }, parameters, modelFile.getFile())).execute();
                                                        }
                                                    }
                                                }, getString(R.string.cancel), null);
                                            break;
                                        case 1:
                                            modelFile.openLocalAs(FileLocalMusicFragment.this.app);
                                            break;
                                        case 2:
                                            DialogUtils.prompt(mActivity, "Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    modelFile.rename(text, new IPostExecuteListener() {
                                                        @Override
                                                        public void onPostExecute(JSONObject json, String body) {
                                                            FileLocalMusicFragment.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "Cancel", null, modelFile.getNameExt());
                                            break;
                                        case 3:
                                            DialogUtils.alert(mActivity, "Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                @Override
                                                public void execute() {
                                                    modelFile.delete(new IPostExecuteListener() {
                                                        @Override
                                                        public void onPostExecute(JSONObject json, String body) {
                                                            FileLocalMusicFragment.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "No", null);
                                            break;
                                        case 4:
                                            DialogUtils.alert(mActivity,
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
            adapter.setOnItemClickListener(new AdapterDragMusicModelFile.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    files.get(position).executeLocal(files, view);
                }
            });

            mRecyclerView.setAdapter(adapter);

            // Extend the Callback class
            ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
                //and in your imlpementaion of
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    // get the viewHolder's and target's positions in your adapter data, swap them
                    Collections.swap(files, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    // and notify the adapter that its dataset has changed
                    adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    //TODO
                }

                //defines the enabled move directions in each state (idle, swiping, dragging).
                @Override
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                }
            };

            // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
            ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
            ith.attachToRecyclerView(mRecyclerView);

            if (mViewMode == Constants.MODE_GRID) {
                this.mGridView.setVisibility(View.VISIBLE);
                this.mRecyclerView.setVisibility(View.GONE);

                List<ModelFile> tmp = new ArrayList<>();
                tmp.addAll(files);

                this.mGridView.setAdapter(new AdapterGridModelFile(app, tmp));
                this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        files.get(position).executeLocal(files, view);
                    }
                });
                this.mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= files.size())
                            return false;
                        final ModelFile modelFile = files.get(position);

                        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileLocalMusicFragment.this.app);
                        String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                        if (app.isLogged())
                            menuList = new String[]{getString(R.string.upload), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                        menuAlert.setTitle("Action");
                        menuAlert.setItems(menuList,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        if (!app.isLogged())
                                            item--;
                                        switch (item) {
                                            case 0:
                                                if (modelFile.directory) {
                                                    Toast.makeText(FileLocalMusicFragment.this.app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                                } else
                                                    DialogUtils.alert(mActivity, getString(R.string.upload), "Upload file " + modelFile.name, getString(R.string.upload), new IListener() {
                                                        @Override
                                                        public void execute() {
                                                            if (modelFile.getFile() != null) {
                                                                List<StringPair> parameters = modelFile.getForUpload();
                                                                (new TaskPost(mActivity, mApplicationCallback, app.getConfig().getUrlServer() + app.getConfig().routeFile, new IPostExecuteListener() {
                                                                    @Override
                                                                    public void onPostExecute(JSONObject json, String body) {

                                                                    }
                                                                }, parameters, modelFile.getFile())).execute();
                                                            }
                                                        }
                                                    }, getString(R.string.cancel), null);
                                                break;
                                            case 1:
                                                DialogUtils.prompt(mActivity, "Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        modelFile.rename(text, new IPostExecuteListener() {
                                                            @Override
                                                            public void onPostExecute(JSONObject json, String body) {
                                                                FileLocalMusicFragment.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "Cancel", null, modelFile.getNameExt());
                                                break;
                                            case 2:
                                                DialogUtils.alert(mActivity, "Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        modelFile.delete(new IPostExecuteListener() {
                                                            @Override
                                                            public void onPostExecute(JSONObject json, String body) {
                                                                FileLocalMusicFragment.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "No", null);
                                                break;
                                            case 3:
                                                DialogUtils.alert(mActivity,
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
            } else {
                this.mGridView.setVisibility(View.GONE);
                this.mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onFabClick(int fab_id, FloatingActionButton fab) {

    }

    @Override
    public boolean isFabVisible(int fab_id) {
        return false;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        return R.drawable.add;
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
    public void setViewMode(int viewMode) {
        if (viewMode != mViewMode) {
            mViewMode = viewMode;
            updateAdapter();
        }
    }
}
