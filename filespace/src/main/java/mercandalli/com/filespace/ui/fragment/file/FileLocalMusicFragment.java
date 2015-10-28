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

import javax.inject.Inject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.config.MyAppComponent;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IModelFileListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.listener.IStringListener;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.model.ModelFile;
import mercandalli.com.filespace.model.MusicModelFile;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileMusicModel;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.adapter.AdapterDragMusicModelFile;
import mercandalli.com.filespace.ui.adapter.AdapterGridModelFile;
import mercandalli.com.filespace.ui.adapter.file.FileModelGridAdapter;
import mercandalli.com.filespace.ui.adapter.file.FileMusicModelDragAdapter;
import mercandalli.com.filespace.ui.fragment.BackFragment;
import mercandalli.com.filespace.ui.fragment.FabFragment;
import mercandalli.com.filespace.ui.fragment.InjectedFragment;
import mercandalli.com.filespace.util.DialogUtils;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.StringPair;

public class FileLocalMusicFragment extends InjectedFragment
        implements BackFragment.IListViewMode, BackFragment.ISortMode {

    private RecyclerView mRecyclerView; // http://nhaarman.github.io/ListViewAnimations/
    private GridView mGridView;
    private ArrayList<FileMusicModel> files;
    private ProgressBar mProgressBar;
    private TextView message;

    private int mSortMode = Constants.SORT_DATE_MODIFICATION;
    private int mViewMode = Constants.MODE_LIST;

    private final IListener mRefreshActivityAdapterListener;

    @Inject
    FileManager mFileManager;

    public static FileLocalMusicFragment newInstance() {
        Bundle args = new Bundle();
        FileLocalMusicFragment fragment = new FileLocalMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FileLocalMusicFragment() {
        mRefreshActivityAdapterListener = new IListener() {
            @Override
            public void execute() {
                if(mApplicationCallback != null) {
                    mApplicationCallback.refreshAdapters();
                }
            }
        };
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

                    FileMusicModel.FileMusicModelBuilder fileMusicModelBuilder = new FileMusicModel.FileMusicModelBuilder();
                    fileMusicModelBuilder.file(new File(fullpath));
                    fileMusicModelBuilder.album(album_name);
                    fileMusicModelBuilder.artist(artist_name);
                    /*
                    if (mSortMode == Constants.SORT_SIZE)
                        fileMusicModel.adapterTitleStart = FileUtils.humanReadableByteCount(fileMusicModel.getSize()) + " - ";
                        */
                    files.add(fileMusicModelBuilder.build());

                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        if (mSortMode == Constants.SORT_ABC) {
            Collections.sort(files, new Comparator<FileMusicModel>() {
                @Override
                public int compare(final FileMusicModel f1, final FileMusicModel f2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
                }
            });
        } else if (mSortMode == Constants.SORT_SIZE) {
            Collections.sort(files, new Comparator<FileMusicModel>() {
                @Override
                public int compare(final FileMusicModel f1, final FileMusicModel f2) {
                    return (new Long(f2.getSize())).compareTo(f1.getSize());
                }
            });
        } else {
            final Map<FileModel, Long> staticLastModifiedTimes = new HashMap<>();
            for (FileModel f : files) {
                staticLastModifiedTimes.put(f, f.getLastModified());
            }
            Collections.sort(files, new Comparator<FileMusicModel>() {
                @Override
                public int compare(final FileMusicModel f1, final FileMusicModel f2) {
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

            final FileMusicModelDragAdapter adapter = new FileMusicModelDragAdapter(mActivity, files, new IFileModelListener() {
                @Override
                public void executeFileModel(final FileModel fileModel) {
                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                    String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.cut), getString(R.string.properties)};
                    if (mApplicationCallback.isLogged())
                        menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    if (!mApplicationCallback.isLogged())
                                        item += 2;
                                    switch (item) {
                                        case 0:
                                            if (fileModel.isDirectory()) {
                                                Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                            } else
                                                DialogUtils.alert(mActivity, getString(R.string.upload), "Upload file " + fileModel.getName(), getString(R.string.upload), new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (fileModel.getFile() != null) {
                                                            List<StringPair> parameters = mFileManager.getForUpload(fileModel);
                                                            (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeFile, new IPostExecuteListener() {
                                                                @Override
                                                                public void onPostExecute(JSONObject json, String body) {

                                                                }
                                                            }, parameters, fileModel.getFile())).execute();
                                                        }
                                                    }
                                                }, getString(R.string.cancel), null);
                                            break;
                                        case 1:
                                            mFileManager.openLocalAs(mActivity, fileModel);
                                            break;
                                        case 2:
                                            DialogUtils.prompt(mActivity, "Rename", "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    mFileManager.rename(fileModel, text, mRefreshActivityAdapterListener);
                                                }
                                            }, "Cancel", null, fileModel.getFullName());
                                            break;
                                        case 3:
                                            DialogUtils.alert(mActivity, "Delete", "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
                                                @Override
                                                public void execute() {
                                                    mFileManager.delete(fileModel, mRefreshActivityAdapterListener);
                                                }
                                            }, "No", null);
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
                                    }
                                }
                            });
                    AlertDialog menuDrop = menuAlert.create();
                    menuDrop.show();
                }
            });
            adapter.setOnItemClickListener(new FileMusicModelDragAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    mFileManager.executeLocal(mActivity, files.get(position), files, view);
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

                List<FileModel> tmp = new ArrayList<>();
                tmp.addAll(files);

                this.mGridView.setAdapter(new FileModelGridAdapter(mActivity, tmp));
                this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mFileManager.executeLocal(mActivity, files.get(position), files, view);
                    }
                });
                this.mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= files.size())
                            return false;
                        final FileModel fileModel = files.get(position);

                        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                        String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                        if (mApplicationCallback.isLogged())
                            menuList = new String[]{getString(R.string.upload), getString(R.string.rename), getString(R.string.delete), getString(R.string.properties)};
                        menuAlert.setTitle("Action");
                        menuAlert.setItems(menuList,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        if (!mApplicationCallback.isLogged())
                                            item--;
                                        switch (item) {
                                            case 0:
                                                if (fileModel.isDirectory()) {
                                                    Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                                } else
                                                    DialogUtils.alert(mActivity, getString(R.string.upload), "Upload file " + fileModel.getName(), getString(R.string.upload), new IListener() {
                                                        @Override
                                                        public void execute() {
                                                            if (fileModel.getFile() != null) {
                                                                List<StringPair> parameters = mFileManager.getForUpload(fileModel);
                                                                (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + Config.routeFile, new IPostExecuteListener() {
                                                                    @Override
                                                                    public void onPostExecute(JSONObject json, String body) {

                                                                    }
                                                                }, parameters, fileModel.getFile())).execute();
                                                            }
                                                        }
                                                    }, getString(R.string.cancel), null);
                                                break;
                                            case 1:
                                                DialogUtils.prompt(mActivity, "Rename", "Rename " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        mFileManager.rename(fileModel, text, mRefreshActivityAdapterListener);
                                                    }
                                                }, "Cancel", null, fileModel.getFullName());
                                                break;
                                            case 2:
                                                DialogUtils.alert(mActivity, "Delete", "Delete " + (fileModel.isDirectory() ? "directory" : "file") + " " + fileModel.getName() + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        mFileManager.delete(fileModel, mRefreshActivityAdapterListener);
                                                    }
                                                }, "No", null);
                                                break;
                                            case 3:
                                                DialogUtils.alert(mActivity,
                                                        getString(R.string.properties) + " : " + fileModel.getName(),
                                                        "Name : " + fileModel.getName() + "\nExtension : " + fileModel.getType() + "\nType : " + fileModel.getType().getTitle() + "\nSize : " + FileUtils.humanReadableByteCount(fileModel.getSize()),
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

    @Override
    protected void inject(MyAppComponent myAppComponent) {
        myAppComponent.inject(this);
    }
}
