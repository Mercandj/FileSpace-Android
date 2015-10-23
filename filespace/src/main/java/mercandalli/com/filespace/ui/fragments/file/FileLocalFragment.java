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
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
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
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IModelFileListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.listeners.IStringListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.models.ModelFileTypeENUM;
import mercandalli.com.filespace.models.MusicModelFile;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activities.ApplicationCallback;
import mercandalli.com.filespace.ui.adapters.AdapterGridModelFile;
import mercandalli.com.filespace.ui.adapters.AdapterModelFile;
import mercandalli.com.filespace.ui.fragments.BackFragment;
import mercandalli.com.filespace.ui.fragments.FabFragment;
import mercandalli.com.filespace.ui.views.DividerItemDecoration;
import mercandalli.com.filespace.utils.FileUtils;
import mercandalli.com.filespace.utils.StringPair;

public class FileLocalFragment extends FabFragment
        implements BackFragment.IListViewMode, BackFragment.ISortMode {

    private RecyclerView mRecyclerView;
    private GridView mGridView;
    private ArrayList<ModelFile> mFilesList;
    private ProgressBar mProgressBar;
    private File mCurrentDirectory;
    private TextView mMessageTextView;

    private List<ModelFile> mFilesToCutList = new ArrayList<>();
    private List<ModelFile> mFilesToCopyList = new ArrayList<>();

    private int mSortMode = Constants.SORT_DATE_MODIFICATION;
    private int mViewMode = Constants.MODE_LIST;

    private Activity mActivity;
    private ApplicationCallback mApplicationCallback;

    public static FileLocalFragment newInstance() {
        return new FileLocalFragment();
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
        final View rootView = inflater.inflate(R.layout.fragment_file_files, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mMessageTextView = (TextView) rootView.findViewById(R.id.message);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mGridView.setVisibility(View.GONE);

        mCurrentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + this.mApplicationCallback.getConfig().getLocalFolderName());
        if (!mCurrentDirectory.exists()) {
            mCurrentDirectory.mkdir();
        }

        refreshList();

        mApplicationCallback.invalidateMenu();

        return rootView;
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(final String search) {
        if (mCurrentDirectory == null) return;

        List<File> fs = Arrays.asList((search == null) ? mCurrentDirectory.listFiles() : mCurrentDirectory.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().contains(search.toLowerCase());
                    }
                }
        ));

        if (mSortMode == Constants.SORT_ABC) {
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
                }
            });
        } else if (mSortMode == Constants.SORT_SIZE) {
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return (new Long(f2.length())).compareTo(f1.length());
                }
            });
        } else {
            final Map<File, Long> staticLastModifiedTimes = new HashMap<>();
            for (File f : fs) {
                staticLastModifiedTimes.put(f, f.lastModified());
            }
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return staticLastModifiedTimes.get(f2).compareTo(staticLastModifiedTimes.get(f1));
                }
            });
        }

        mFilesList = new ArrayList<>();
        for (File file : fs) {
            ModelFile tmpModelFile = new ModelFile(mActivity, mApplicationCallback, file);
            if (mSortMode == Constants.SORT_SIZE)
                tmpModelFile.adapterTitleStart = FileUtils.humanReadableByteCount(tmpModelFile.size) + " - ";
            mFilesList.add(tmpModelFile);
        }

        updateAdapter();
    }

    public void updateAdapter() {
        if (mRecyclerView != null && mFilesList != null && isAdded()) {

            refreshFab();

            if (mFilesList.size() == 0) {
                mMessageTextView.setText(getString(R.string.no_file_local_folder, "" + mCurrentDirectory.getName()));
                mMessageTextView.setVisibility(View.VISIBLE);
            } else
                mMessageTextView.setVisibility(View.GONE);

            final AdapterModelFile adapter = new AdapterModelFile(mActivity, mFilesList, new IModelFileListener() {
                @Override
                public void executeModelFile(final ModelFile modelFile) {
                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileLocalFragment.this.app);
                    String[] menuList = {getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
                    if (app.isLogged())
                        menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    if (!mApplicationCallback.isLogged())
                                        item += 1;
                                    switch (item) {
                                        case 0:
                                            if (modelFile.directory) {
                                                Toast.makeText(FileLocalFragment.this.app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                            } else
                                                FileLocalFragment.this.app.alert(getString(R.string.upload), "Upload file " + modelFile.name, getString(R.string.upload), new IListener() {
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
                                            modelFile.openLocalAs(FileLocalFragment.this.app);
                                            break;
                                        case 2:
                                            FileLocalFragment.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    modelFile.rename(text, new IPostExecuteListener() {
                                                        @Override
                                                        public void onPostExecute(JSONObject json, String body) {
                                                            if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
                                                                mFilesToCutList.clear();
                                                                refreshFab();
                                                            }
                                                            if (mFilesToCopyList != null && mFilesToCopyList.size() != 0) {
                                                                mFilesToCopyList.clear();
                                                                refreshFab();
                                                            }
                                                            FileLocalFragment.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "Cancel", null, modelFile.getNameExt());
                                            break;
                                        case 3:
                                            FileLocalFragment.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                @Override
                                                public void execute() {
                                                    modelFile.delete(new IPostExecuteListener() {
                                                        @Override
                                                        public void onPostExecute(JSONObject json, String body) {
                                                            if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
                                                                mFilesToCutList.clear();
                                                                refreshFab();
                                                            }
                                                            if (mFilesToCopyList != null && mFilesToCopyList.size() != 0) {
                                                                mFilesToCopyList.clear();
                                                                refreshFab();
                                                            }
                                                            FileLocalFragment.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "No", null);
                                            break;
                                        case 4:
                                            FileLocalFragment.this.mFilesToCopyList.add(modelFile);
                                            Toast.makeText(app, "File ready to copy.", Toast.LENGTH_SHORT).show();
                                            refreshFab();
                                            break;
                                        case 5:
                                            FileLocalFragment.this.mFilesToCutList.add(modelFile);
                                            Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                            refreshFab();
                                            break;
                                        case 6:
                                            FileLocalFragment.this.app.alert(
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

            mRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new AdapterModelFile.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (hasItemSelected()) {
                        mFilesList.get(position).selected = !mFilesList.get(position).selected;
                        adapter.notifyItemChanged(position);
                    } else if (mFilesList.get(position).directory) {
                        mCurrentDirectory = new File(mFilesList.get(position).url);
                        refreshList();
                    } else {
                        List<MusicModelFile> tmpFiles = new ArrayList<>();
                        for (ModelFile f : mFilesList)
                            if (f.type != null && f.type.equals(ModelFileTypeENUM.AUDIO.type))
                                tmpFiles.add(new MusicModelFile(mActivity, mApplicationCallback, f));
                        mFilesList.get(position).executeLocal(tmpFiles, view);
                    }
                }
            });

            adapter.setOnItemLongClickListener(new AdapterModelFile.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(View view, int position) {
                    mFilesList.get(position).selected = !mFilesList.get(position).selected;
                    adapter.notifyItemChanged(position);
                    return true;
                }
            });


            if (mViewMode == Constants.MODE_GRID) {
                mGridView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);

                mGridView.setAdapter(new AdapterGridModelFile(app, mFilesList));
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (hasItemSelected()) {
                            mFilesList.get(position).selected = !mFilesList.get(position).selected;
                            adapter.notifyItemChanged(position);
                        } else if (mFilesList.get(position).directory) {
                            mCurrentDirectory = new File(mFilesList.get(position).url);
                            refreshList();
                        } else {
                            List<MusicModelFile> tmpFiles = new ArrayList<>();
                            for (ModelFile f : mFilesList)
                                if (f.type != null && f.type.equals(ModelFileTypeENUM.AUDIO.type))
                                    tmpFiles.add(new MusicModelFile(mActivity, mApplicationCallback, f));
                            mFilesList.get(position).executeLocal(tmpFiles, view);
                        }
                    }
                });
                mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= mFilesList.size()) {
                            return false;
                        }
                        final ModelFile modelFile = mFilesList.get(position);

                        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileLocalFragment.this.app);
                        String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
                        if (app.isLogged()) {
                            menuList = new String[]{getString(R.string.upload), getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
                        }
                        menuAlert.setTitle("Action");
                        menuAlert.setItems(menuList,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        if (!app.isLogged())
                                            item--;
                                        switch (item) {
                                            case 0:
                                                if (modelFile.directory) {
                                                    Toast.makeText(FileLocalFragment.this.app, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                                } else
                                                    FileLocalFragment.this.app.alert(getString(R.string.upload), "Upload file " + modelFile.name, getString(R.string.upload), new IListener() {
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
                                                FileLocalFragment.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Ok", new IStringListener() {
                                                    @Override
                                                    public void execute(String text) {
                                                        modelFile.rename(text, new IPostExecuteListener() {
                                                            @Override
                                                            public void onPostExecute(JSONObject json, String body) {
                                                                if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
                                                                    mFilesToCutList.clear();
                                                                    refreshFab();
                                                                }
                                                                FileLocalFragment.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "Cancel", null, modelFile.getNameExt());
                                                break;
                                            case 2:
                                                FileLocalFragment.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.name + " ?", "Yes", new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        modelFile.delete(new IPostExecuteListener() {
                                                            @Override
                                                            public void onPostExecute(JSONObject json, String body) {
                                                                if (mFilesToCutList != null && mFilesToCutList.size() != 0) {
                                                                    mFilesToCutList.clear();
                                                                    refreshFab();
                                                                }
                                                                FileLocalFragment.this.app.refreshAdapters();
                                                            }
                                                        });
                                                    }
                                                }, "No", null);
                                                break;
                                            case 3:
                                                FileLocalFragment.this.mFilesToCopyList.add(modelFile);
                                                Toast.makeText(app, "File ready to copy.", Toast.LENGTH_SHORT).show();
                                                refreshFab();
                                                break;
                                            case 4:
                                                FileLocalFragment.this.mFilesToCutList.add(modelFile);
                                                Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                                refreshFab();
                                                break;
                                            case 5:
                                                FileLocalFragment.this.app.alert(
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
                mGridView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean createFile(String path, String name) {
        int len = path.length();
        if (len < 1 || name.length() < 1)
            return false;
        if (path.charAt(len - 1) != '/')
            path += "/";
        if (!name.contains(".")) {
            if (new File(path + name).mkdir())
                return true;
        } else {
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
        if (hasItemSelected()) {
            deselectAll();
            return true;
        } else if (!mCurrentDirectory.getPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + this.app.getConfig().getLocalFolderName())) {
            if (mCurrentDirectory.getParent() != null) {
                FileLocalFragment.this.mCurrentDirectory = new File(mCurrentDirectory.getParentFile().getPath());
                FileLocalFragment.this.refreshList();
                return true;
            }
        } else if ((mFilesToCopyList != null && mFilesToCopyList.size() != 0) || (mFilesToCutList != null && mFilesToCutList.size() != 0)) {
            if (mFilesToCopyList != null)
                mFilesToCopyList.clear();
            if (mFilesToCutList != null)
                mFilesToCutList.clear();
            refreshFab();
            return true;
        }
        return false;
    }

    public void goHome() {
        this.mCurrentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + app.getConfig().getLocalFolderName());
        this.refreshList();
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
    public void onFocus() {
    }

    @Override
    public void onFabClick(int fab_id, FloatingActionButton fab) {
        switch (fab_id) {
            case 0:
                if ((mFilesToCopyList != null && mFilesToCopyList.size() != 0) || (mFilesToCutList != null && mFilesToCutList.size() != 0)) {
                    if (mFilesToCopyList != null) {
                        for (ModelFile file : mFilesToCopyList) {
                            file.copyFile(mCurrentDirectory.getAbsolutePath() + File.separator);
                        }
                        mFilesToCopyList.clear();
                    }
                    if (mFilesToCutList != null) {
                        for (ModelFile file : mFilesToCutList) {
                            file.renameLocalByPath(mCurrentDirectory.getAbsolutePath() + File.separator + file.getNameExt());
                        }
                        mFilesToCutList.clear();
                    }
                    refreshList();
                } else {
                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(FileLocalFragment.this.app);
                    final String[] menuList = {"New Folder or File"};
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            FileLocalFragment.this.app.prompt("New Folder or File", "Choose a file name with ext or a folder name.", getString(R.string.ok), new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    createFile(mCurrentDirectory.getPath() + File.separator, text);
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
                refreshFab();
                break;

            case 1:
                if (mCurrentDirectory.getParent() != null) {
                    FileLocalFragment.this.mCurrentDirectory = new File(mCurrentDirectory.getParentFile().getPath());
                    //Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+FileManagerFragmentLocal.this.app.getConfig().localFolderName);
                    FileLocalFragment.this.refreshList();
                }
                break;
        }
    }

    @Override
    public boolean isFabVisible(int fab_id) {
        switch (fab_id) {
            case 0:
                return true;
            case 1:
                return this.mCurrentDirectory != null && mCurrentDirectory.getParent() != null;
        }
        return false;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        switch (fab_id) {
            case 0:
                if (mFilesToCopyList != null && mFilesToCopyList.size() != 0)
                    return R.drawable.ic_menu_paste_holo_dark;
                else if (mFilesToCutList != null && mFilesToCutList.size() != 0)
                    return R.drawable.ic_menu_paste_holo_dark;
                else
                    return R.drawable.add;
            case 1:
                return R.drawable.arrow_up;
        }
        return R.drawable.add;
    }

    @Override
    public void setSortMode(int mSortMode) {
        if (mSortMode == Constants.SORT_ABC ||
                mSortMode == Constants.SORT_DATE_MODIFICATION ||
                mSortMode == Constants.SORT_SIZE) {
            this.mSortMode = mSortMode;
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
