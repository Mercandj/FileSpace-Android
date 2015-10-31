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

import javax.inject.Inject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.config.MyAppComponent;
import mercandalli.com.filespace.listener.IFileModelListener;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IStringListener;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.ui.adapter.file.FileModelAdapter;
import mercandalli.com.filespace.ui.adapter.file.FileModelGridAdapter;
import mercandalli.com.filespace.ui.fragment.BackFragment;
import mercandalli.com.filespace.ui.fragment.InjectedFragment;
import mercandalli.com.filespace.ui.view.divider.DividerItemDecoration;
import mercandalli.com.filespace.util.DialogUtils;
import mercandalli.com.filespace.util.FileUtils;

public class FileLocalFragment extends InjectedFragment
        implements BackFragment.IListViewMode, BackFragment.ISortMode {

    private RecyclerView mRecyclerView;
    private GridView mGridView;
    private ArrayList<FileModel> mFilesList;
    private ProgressBar mProgressBar;
    private File mCurrentDirectory;
    private TextView mMessageTextView;

    private List<FileModel> mFilesToCutList = new ArrayList<>();
    private List<FileModel> mFilesToCopyList = new ArrayList<>();

    private int mSortMode = Constants.SORT_DATE_MODIFICATION;
    private int mViewMode = Constants.MODE_LIST;

    @Inject
    FileManager mFileManager;

    public static FileLocalFragment newInstance() {
        return new FileLocalFragment();
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
            FileModel tmpFileModel = new FileModel.FileModelBuilder().file(file).build();
            /*
            if (mSortMode == Constants.SORT_SIZE)
                tmpFileModel.adapterTitleStart = FileUtils.humanReadableByteCount(tmpFileModel.size) + " - ";
            */
            mFilesList.add(tmpFileModel);
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

            final FileModelAdapter adapter = new FileModelAdapter(mActivity, mFilesList, new IFileModelListener() {
                @Override
                public void executeFileModel(final FileModel fileModel) {
                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                    String[] menuList = {getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
                    if (mApplicationCallback.isLogged())
                        menuList = new String[]{getString(R.string.upload), getString(R.string.open_as), getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    if (!mApplicationCallback.isLogged())
                                        item += 1;
                                    switch (item) {
                                        case 0:
                                            if (fileModel.isDirectory()) {
                                                Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                            } else {
                                                DialogUtils.alert(mActivity, getString(R.string.upload), "Upload file " + fileModel.getName(), getString(R.string.upload), new IListener() {
                                                    @Override
                                                    public void execute() {
                                                        if (fileModel.getFile() != null) {
                                                            mFileManager.upload(fileModel, -1, new IListener() {
                                                                @Override
                                                                public void execute() {
                                                                    mApplicationCallback.refreshData();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }, getString(R.string.cancel), null);
                                            }
                                            break;
                                        case 1:
                                            mFileManager.openLocalAs(mActivity, fileModel);
                                            break;
                                        case 2:
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
                                                            if (mFilesToCopyList != null && mFilesToCopyList.size() != 0) {
                                                                mFilesToCopyList.clear();
                                                                refreshFab();
                                                            }
                                                            mApplicationCallback.refreshData();
                                                        }
                                                    });
                                                }
                                            }, "Cancel", null, fileModel.getFullName());
                                            break;
                                        case 3:
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
                                                            if (mFilesToCopyList != null && mFilesToCopyList.size() != 0) {
                                                                mFilesToCopyList.clear();
                                                                refreshFab();
                                                            }
                                                            mApplicationCallback.refreshData();
                                                        }
                                                    });
                                                }
                                            }, "No", null);
                                            break;
                                        case 4:
                                            FileLocalFragment.this.mFilesToCopyList.add(fileModel);
                                            Toast.makeText(mActivity, "File ready to copy.", Toast.LENGTH_SHORT).show();
                                            refreshFab();
                                            break;
                                        case 5:
                                            FileLocalFragment.this.mFilesToCutList.add(fileModel);
                                            Toast.makeText(mActivity, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                            refreshFab();
                                            break;
                                        case 6:
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

            mRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new FileModelAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    /*if (hasItemSelected()) {
                        mFilesList.get(position).selected = !mFilesList.get(position).selected;
                        adapter.notifyItemChanged(position);
                    } else */
                    if (mFilesList.get(position).isDirectory()) {
                        mCurrentDirectory = new File(mFilesList.get(position).getUrl());
                        refreshList();
                    } else {
                        mFileManager.executeLocal(mActivity, position, mFilesList, view);
                    }
                }
            });

            adapter.setOnItemLongClickListener(new FileModelAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(View view, int position) {
                    /*
                    mFilesList.get(position).selected = !mFilesList.get(position).selected;
                    adapter.notifyItemChanged(position);
                    */
                    return true;
                }
            });


            if (mViewMode == Constants.MODE_GRID) {
                mGridView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);

                mGridView.setAdapter(new FileModelGridAdapter(mActivity, mFilesList));
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*if (hasItemSelected()) {
                            mFilesList.get(position).selected = !mFilesList.get(position).selected;
                            adapter.notifyItemChanged(position);
                        } else */
                        if (mFilesList.get(position).isDirectory()) {
                            mCurrentDirectory = new File(mFilesList.get(position).getUrl());
                            refreshList();
                        } else {
                            mFileManager.executeLocal(mActivity, position, mFilesList, view);
                        }
                    }
                });
                mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= mFilesList.size()) {
                            return false;
                        }
                        final FileModel fileModel = mFilesList.get(position);

                        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                        String[] menuList = {getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
                        if (mApplicationCallback.isLogged()) {
                            menuList = new String[]{getString(R.string.upload), getString(R.string.rename), getString(R.string.delete), getString(R.string.copy), getString(R.string.cut), getString(R.string.properties)};
                        }
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
                                                                mFileManager.upload(fileModel, -1, new IListener() {
                                                                    @Override
                                                                    public void execute() {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }, getString(R.string.cancel), null);
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
                                                FileLocalFragment.this.mFilesToCopyList.add(fileModel);
                                                Toast.makeText(mActivity, "File ready to copy.", Toast.LENGTH_SHORT).show();
                                                refreshFab();
                                                break;
                                            case 4:
                                                FileLocalFragment.this.mFilesToCutList.add(fileModel);
                                                Toast.makeText(mActivity, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                                refreshFab();
                                                break;
                                            case 5:
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
        } else if (!mCurrentDirectory.getPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + mApplicationCallback.getConfig().getLocalFolderName())) {
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
        this.mCurrentDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + mApplicationCallback.getConfig().getLocalFolderName());
        this.refreshList();
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
            */
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
                        for (FileModel file : mFilesToCopyList) {
                            mFileManager.copyLocalFile(mActivity, file, mCurrentDirectory.getAbsolutePath() + File.separator);
                        }
                        mFilesToCopyList.clear();
                    }
                    if (mFilesToCutList != null) {
                        for (FileModel file : mFilesToCutList) {
                            mFileManager.renameLocalByPath(file, mCurrentDirectory.getAbsolutePath() + File.separator + file.getFullName());
                        }
                        mFilesToCutList.clear();
                    }
                    refreshList();
                } else {
                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                    final String[] menuList = {"New Folder or File"};
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            DialogUtils.prompt(mActivity, "New Folder or File", "Choose a file name with ext or a folder name.", getString(R.string.ok), new IStringListener() {
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

    @Override
    protected void inject(MyAppComponent myAppComponent) {
        myAppComponent.inject(this);
    }
}
