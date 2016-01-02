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
package com.mercandalli.android.apps.files.file.cloud;

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

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.fragment.FabFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.common.util.NetUtils;
import com.mercandalli.android.apps.files.file.FileAddDialog;
import com.mercandalli.android.apps.files.file.local.SearchActivity;
import com.mercandalli.android.apps.files.main.ApplicationCallback;
import com.mercandalli.android.apps.files.main.Constants;

public class FileCloudPagerFragment extends BackFragment implements ViewPager.OnPageChangeListener, FabFragment.RefreshFabCallback {

    private static final String BUNDLE_ARG_TITLE = "FileOnlineFragment.Args.BUNDLE_ARG_TITLE";

    private static final int NB_FRAGMENT = 3;
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;

    private FloatingActionButton mFab1;
    private FloatingActionButton mFab2;
    private View mCoordinatorLayoutView;

    private String mTitle;
    private SetToolbarCallback mSetToolbarCallback;

    public static FileCloudPagerFragment newInstance(String title) {
        final FileCloudPagerFragment fragment = new FileCloudPagerFragment();
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
        setStatusBarColor(mActivity, R.color.status_bar);
        setHasOptionsMenu(true);

        mCoordinatorLayoutView = rootView.findViewById(R.id.fragment_file_coordinator_layout);

        mPagerAdapter = new FileManagerFragmentPagerAdapter(getChildFragmentManager(), mApplicationCallback);
        mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_file_view_pager);
        mViewPager.setAdapter(mPagerAdapter);

        ((TabLayout) rootView.findViewById(R.id.fragment_file_tab_layout)).setupWithViewPager(mViewPager);

        mFab1 = ((FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_1));
        mFab1.setVisibility(View.GONE);
        mFab2 = ((FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_2));
        mFab2.setVisibility(View.GONE);

        if (savedInstanceState == null) {
            //mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
            mViewPager.setCurrentItem(1);
        }
        mViewPager.addOnPageChangeListener(this);

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
        updateNoInternet();
        refreshFab(position);
    }

    @Override
    public void onRefreshFab() {
        refreshFab();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
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
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_home).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(true);
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
            if (fabFragment instanceof FileCloudFragment) {
                // TODO
            } else if (fabFragment instanceof FileMyCloudFragment) {
                // TODO
            }
        }
    }

    public void sort() {
        final Context context = getContext();
        final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
        String[] menuList = {context.getString(R.string.sort_abc), context.getString(R.string.sort_size), context.getString(R.string.sort_date)};
        menuAlert.setTitle(getString(R.string.view));
        menuAlert.setItems(menuList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        FabFragment fabFragment = getCurrentFragment();
                        if (fabFragment != null) {
                            if (fabFragment instanceof ISortMode) {
                                ((ISortMode) fabFragment).setSortMode(item == 0 ? Constants.SORT_ABC : (item == 1 ? Constants.SORT_SIZE : Constants.SORT_DATE_MODIFICATION));
                            } else {
                                Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mActivity, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog menuDrop = menuAlert.create();
        menuDrop.show();
    }

    private void updateNoInternet() {
        if (!NetUtils.isInternetConnection(mActivity)) {
            if (mCoordinatorLayoutView != null) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayoutView, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
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
                snackbar.show();
            } else {
                Toast.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }
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
            switch (i) {
                case 0:
                    return FileCloudFragment.newInstance();
                case 1:
                    return FileMyCloudFragment.newInstance();
                case 2:
                    return FileCloudDownloadedFragment.newInstance();
                default:
                    return FileCloudFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            String title = "null";
            switch (i) {
                case 0:
                    title = getString(R.string.file_fragment_public_cloud);
                    break;
                case 1:
                    title = getString(R.string.file_fragment_my_cloud);
                    break;
                case 2:
                    title = "DOWNLOADED";
                    break;
            }
            return title;
        }
    }
}
