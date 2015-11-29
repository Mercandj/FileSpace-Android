/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.filespace.file;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.fragment.BackFragment;
import com.mercandalli.android.filespace.common.fragment.FabFragment;
import com.mercandalli.android.filespace.common.listener.IListener;
import com.mercandalli.android.filespace.common.listener.SetToolbarCallback;
import com.mercandalli.android.filespace.common.util.NetUtils;
import com.mercandalli.android.filespace.file.audio.FileAudioLocalFragment;
import com.mercandalli.android.filespace.file.cloud.FileCloudFragment;
import com.mercandalli.android.filespace.file.cloud.FileMyCloudFragment;
import com.mercandalli.android.filespace.file.local.FileLocalFragment;
import com.mercandalli.android.filespace.main.ApplicationCallback;
import com.mercandalli.android.filespace.main.Constants;
import com.mercandalli.android.filespace.search.SearchActivity;

public class FileFragment extends BackFragment implements ViewPager.OnPageChangeListener, FabFragment.RefreshFabCallback {

    private static final String BUNDLE_ARG_TITLE = "FileFragment.Args.BUNDLE_ARG_TITLE";

    private static final int NB_FRAGMENT = 4;
    private static final int INIT_FRAGMENT = 1;
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;

    private FloatingActionButton mFab1;
    private FloatingActionButton mFab2;
    private View coordinatorLayoutView;
    private Snackbar mSnackbar;

    private int mViewMode = Constants.MODE_LIST;

    private String mTitle;
    private SetToolbarCallback mSetToolbarCallback;

    public static FileFragment newInstance(String title) {
        final FileFragment fragment = new FileFragment();
        final Bundle args = new Bundle();
        args.putString(BUNDLE_ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetToolbarCallback) {
            mSetToolbarCallback = (SetToolbarCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSetToolbarCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (!args.containsKey(BUNDLE_ARG_TITLE)) {
            throw new IllegalStateException("Missing args. Please use newInstance()");
        }
        mTitle = args.getString(BUNDLE_ARG_TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_file_toolbar);
        toolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(toolbar);
        setStatusBarColor(mActivity, R.color.notifications_bar);
        setHasOptionsMenu(true);

        coordinatorLayoutView = rootView.findViewById(R.id.fragment_file_coordinator_layout);

        mPagerAdapter = new FileManagerFragmentPagerAdapter(getChildFragmentManager(), mApplicationCallback);

        mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_file_view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        if (savedInstanceState == null && mApplicationCallback.isLogged()) {
            if (NetUtils.isInternetConnection(mActivity)) {
                mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
                mViewPager.setCurrentItem(INIT_FRAGMENT);
            } else {
                mViewPager.setOffscreenPageLimit(NB_FRAGMENT);
                mViewPager.setCurrentItem(INIT_FRAGMENT + 1);
            }
        } else if (savedInstanceState == null) {
            mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
            mViewPager.setCurrentItem(0);
        }

        ((TabLayout) rootView.findViewById(R.id.fragment_file_tab_layout)).setupWithViewPager(mViewPager);

        mFab1 = ((FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_1));
        mFab1.setVisibility(View.GONE);
        mFab2 = ((FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_2));
        mFab2.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if (currentFragmentId == -1) {
            return false;
        }
        FabFragment fabFragment = getCurrentFragment();
        if (fabFragment == null) {
            return false;
        }
        refreshFab(fabFragment);
        return fabFragment.back();
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        mApplicationCallback.invalidateMenu();
        if (mApplicationCallback.isLogged()) {
            switch (position) {
                case 0:
                    updateNoInternet();
                    break;
                case 1:
                    updateNoInternet();
                    break;
                default:
                    if (mSnackbar != null) {
                        mSnackbar.dismiss();
                    }
            }
            refreshFab(position);
        } else {
            refreshFab(position + 2);
        }
    }

    @Override
    public void onRefreshFab() {
        refreshFab();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_filter);
        SearchView mSearchView = (SearchView) searchItem.getActionView();

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                if (query == null) {
                    return true;
                }
                if (query.replaceAll(" ", "").equals("")) {
                    refreshListServer();
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null) {
                    return false;
                }
                if (query.replaceAll(" ", "").equals("")) {
                    return false;
                }
                refreshListServer(query);
                return false;
            }
        };

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(queryTextListener);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_filter).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_home).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(true);

        if (getCurrentFragmentIndex() == (mApplicationCallback.isLogged() ? 2 : 0)) {
            menu.findItem(R.id.action_home).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                add();
                return true;
            case R.id.action_home:
                goHome();
                return true;
            case R.id.action_sort:
                sort();
                return true;
            case R.id.action_search:
                SearchActivity.start(mActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getCurrentFragmentIndex() {
        return mViewPager.getCurrentItem();
    }

    public FabFragment getCurrentFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.fragment_file_view_pager + ":" + mPagerAdapter.getItemId(getCurrentFragmentIndex()));
        if (fragment == null || !(fragment instanceof FabFragment)) {
            return null;
        }
        return (FabFragment) fragment;
    }

    public void refreshListServer() {
        refreshListServer(null);
    }

    public void refreshListServer(String search) {
        FabFragment fabFragment = getCurrentFragment();
        if (fabFragment != null) {
            if (fabFragment instanceof FileCloudFragment) {
                FileCloudFragment fragmentFileManagerFragment = (FileCloudFragment) fabFragment;
                fragmentFileManagerFragment.refreshList(search);
            } else if (fabFragment instanceof FileMyCloudFragment) {
                FileMyCloudFragment fragmentFileManagerFragment = (FileMyCloudFragment) fabFragment;
                fragmentFileManagerFragment.refreshList(search);
            } else if (fabFragment instanceof FileLocalFragment) {
                FileLocalFragment fragmentFileManagerFragment = (FileLocalFragment) fabFragment;
                fragmentFileManagerFragment.refreshList(search);
            } else if (fabFragment instanceof FileAudioLocalFragment) {
                FileAudioLocalFragment fragmentFileManagerFragment = (FileAudioLocalFragment) fabFragment;
                fragmentFileManagerFragment.refreshList(search);
            }
        }
    }

    public void updateAdapterListServer() {
        FabFragment fabFragment = getCurrentFragment();
        if (fabFragment != null) {
            if (fabFragment instanceof FileCloudFragment) {
                FileCloudFragment fragmentFileManagerFragment = (FileCloudFragment) fabFragment;
                fragmentFileManagerFragment.updateAdapter();
            } else if (fabFragment instanceof FileMyCloudFragment) {
                FileMyCloudFragment fragmentFileManagerFragment = (FileMyCloudFragment) fabFragment;
                fragmentFileManagerFragment.updateAdapter();
            } else if (fabFragment instanceof FileLocalFragment) {
                FileLocalFragment fragmentFileManagerFragment = (FileLocalFragment) fabFragment;
                fragmentFileManagerFragment.updateAdapter();
            }
        }
    }

    public void refreshData() {
        FabFragment fabFragment = getCurrentFragment();
        if (fabFragment != null) {
            if (fabFragment instanceof FileCloudFragment) {
                FileCloudFragment fragmentFileManagerFragment = (FileCloudFragment) fabFragment;
                fragmentFileManagerFragment.refreshList();
            }
            if (fabFragment instanceof FileMyCloudFragment) {
                FileMyCloudFragment fragmentFileManagerFragment = (FileMyCloudFragment) fabFragment;
                fragmentFileManagerFragment.refreshList();
            }
            if (fabFragment instanceof FileLocalFragment) {
                FileLocalFragment fragmentFileManagerFragment = (FileLocalFragment) fabFragment;
                fragmentFileManagerFragment.refreshList();
            }
        }
    }

    public void add() {
        new FileAddDialog(mActivity, mApplicationCallback, -1, new IListener() {
            @Override
            public void execute() {
                refreshListServer();
            }
        }, new IListener() { // Dismiss
            @Override
            public void execute() {

            }
        });
    }

    public void goHome() {
        FabFragment fabFragment = getCurrentFragment();
        if (fabFragment != null) {
            if (fabFragment instanceof FileLocalFragment) {
                FileLocalFragment fragmentFileManagerFragment = (FileLocalFragment) fabFragment;
                fragmentFileManagerFragment.goHome();
            }
        }
    }

    public void sort() {
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
        String[] menuList = {"Sort by name (A-Z)", "Sort by size", "Sort by date", mApplicationCallback.getConfig().getUserFileModeView() == Constants.MODE_LIST ? "Grid View" : "List View"};
        menuAlert.setTitle(getString(R.string.view));
        menuAlert.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        final int fragmentIndex = getCurrentFragmentIndex() + (!mApplicationCallback.isLogged() ? 2 : 0);

                        switch (item) {
                            case 0:
                            case 1:
                            case 2:
                                if (fragmentIndex != 2 && fragmentIndex != 3)
                                    Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                else {
                                    FabFragment fabFragment = getCurrentFragment();
                                    if (fabFragment != null) {
                                        if (fabFragment instanceof ISortMode) {
                                            ((ISortMode) fabFragment).setSortMode(item == 0 ? Constants.SORT_ABC : (item == 1 ? Constants.SORT_SIZE : Constants.SORT_DATE_MODIFICATION));
                                        }
                                    }
                                }
                                break;

                            case 3:
                                if (mViewMode == Constants.MODE_LIST) {
                                    mViewMode = Constants.MODE_GRID;
                                } else {
                                    mViewMode = Constants.MODE_LIST;
                                }
                                mApplicationCallback.getConfig().setUserFileModeView(mActivity, mViewMode);
                                FabFragment fabFragment = getCurrentFragment();
                                if (fabFragment != null) {
                                    if (fabFragment instanceof IListViewMode) {
                                        IListViewMode fragmentFileManagerFragment = (IListViewMode) fabFragment;
                                        fragmentFileManagerFragment.setViewMode(mViewMode);
                                    }
                                }

                                break;
                            default:
                                Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        AlertDialog menuDrop = menuAlert.create();
        menuDrop.show();
    }

    private void updateNoInternet() {
        if (!NetUtils.isInternetConnection(mActivity)) {
            this.mSnackbar = Snackbar.make(this.coordinatorLayoutView, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.refresh), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetUtils.isInternetConnection(mActivity)) {
                                FabFragment fabFragment = getCurrentFragment();
                                if (fabFragment != null) {
                                    fabFragment.onFocus();
                                }
                            } else {
                                updateNoInternet();
                            }
                        }
                    });
            this.mSnackbar.show();
        }
    }

    private void refreshFab() {
        refreshFab(getCurrentFragmentIndex());
    }

    private void refreshFab(int currentFragmentId) {
        if (currentFragmentId == -1) {
            return;
        }
        FabFragment fabFragment = getCurrentFragment();
        if (fabFragment == null) {
            return;
        }
        refreshFab(fabFragment);
    }

    private void refreshFab(final FabFragment currentFragment) {
        if (mFab1 == null) {
            return;
        }
        int imageResource;
        if (currentFragment.isFabVisible(0)) {
            mFab1.show();
            imageResource = currentFragment.getFabImageResource(0);
            if (imageResource == -1) {
                imageResource = android.R.drawable.ic_input_add;
            }
            mFab1.setImageResource(imageResource);
            mFab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFragment.onFabClick(0, mFab1);
                }
            });
        } else {
            mFab1.hide();
        }

        if (mFab2 == null) {
            return;
        }
        if (currentFragment.isFabVisible(1)) {
            mFab2.show();
            imageResource = currentFragment.getFabImageResource(1);
            if (imageResource == -1) {
                imageResource = android.R.drawable.ic_input_add;
            }
            mFab2.setImageResource(imageResource);
            mFab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFragment.onFabClick(1, mFab2);
                }
            });
        } else {
            mFab2.hide();
        }
    }

    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
        ApplicationCallback mApplicationCallback;

        public FileManagerFragmentPagerAdapter(FragmentManager fm, ApplicationCallback applicationCallback) {
            super(fm);
            mApplicationCallback = applicationCallback;
        }

        @Override
        public FabFragment getItem(int i) {
            if (!mApplicationCallback.isLogged()) {
                i += 2;
            }

            switch (i) {
                case 0:
                    return FileCloudFragment.newInstance();
                case 1:
                    return FileMyCloudFragment.newInstance();
                case 2:
                    return FileLocalFragment.newInstance();
                case 3:
                    return FileAudioLocalFragment.newInstance();
                default:
                    return FileLocalFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT - (mApplicationCallback.isLogged() ? 0 : 2);
        }

        @Override
        public CharSequence getPageTitle(int i) {
            if (!mApplicationCallback.isLogged()) {
                i += 2;
            }
            String title = "null";
            switch (i) {
                case 0:
                    title = getString(R.string.file_fragment_public_cloud);
                    break;
                case 1:
                    title = getString(R.string.file_fragment_my_cloud);
                    break;
                case 2:
                    title = getString(R.string.file_fragment_local);
                    break;
                case 3:
                    title = getString(R.string.file_fragment_music);
                    break;
            }
            return title;
        }
    }
}
