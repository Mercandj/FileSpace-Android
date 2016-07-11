/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p>
 * LICENSE:
 * <p>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.file.cloud;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.file.FileAddDialog;
import com.mercandalli.android.apps.files.file.cloud.fab.FileCloudFabManager;
import com.mercandalli.android.apps.files.file.local.SearchActivity;
import com.mercandalli.android.apps.files.file.local.fab.FileLocalFabManager;
import com.mercandalli.android.apps.files.main.network.NetUtils;

import static com.mercandalli.android.library.base.view.StatusBarUtils.setStatusBarColor;

public class FileCloudPagerFragment extends BackFragment implements
        ViewPager.OnPageChangeListener,
        FileCloudFabManager.FabContainer,
        View.OnClickListener {

    private static final String BUNDLE_ARG_TITLE = "FileOnlineFragment.Args.BUNDLE_ARG_TITLE";

    private static final int NB_FRAGMENT = 3;
    private static final int INIT_VIEW_PAGER_POSITION = 1;
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;

    private FloatingActionButton mFab1;
    private FloatingActionButton mFab2;
    private View mCoordinatorLayoutView;

    private String mTitle;
    private SetToolbarCallback mSetToolbarCallback;

    private FileCloudFabManager mFileCloudFabManager;

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
        mFileCloudFabManager = FileCloudFabManager.getInstance();
        mFileCloudFabManager.setFabContainer(this, INIT_VIEW_PAGER_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_file_toolbar);
        toolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(toolbar);
        setStatusBarColor(getContext(), R.color.status_bar);
        setHasOptionsMenu(true);

        mCoordinatorLayoutView = rootView.findViewById(R.id.fragment_file_coordinator_layout);

        mPagerAdapter = new FileManagerFragmentPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_file_view_pager);
        mViewPager.setAdapter(mPagerAdapter);

        ((TabLayout) rootView.findViewById(R.id.fragment_file_tab_layout)).setupWithViewPager(mViewPager);

        mFab1 = ((FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_1));
        mFab2 = ((FloatingActionButton) rootView.findViewById(R.id.fragment_file_fab_2));
        mFab1.setVisibility(View.GONE);
        mFab2.setVisibility(View.GONE);
        mFab1.setOnClickListener(this);
        mFab2.setOnClickListener(this);

        if (savedInstanceState == null) {
            mViewPager.setCurrentItem(INIT_VIEW_PAGER_POSITION);
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
        Fragment fabFragment = getCurrentFragment();
        if (fabFragment == null) {
            return false;
        }
        mFileCloudFabManager.updateFabButtons();
        return ((BackFragment) fabFragment).back();
    }

    @Override
    public void updateFabs(final FileCloudFabManager.FabState[] fabStates) {
        for (int i = 0; i < fabStates.length; i++) {
            final FileCloudFabManager.FabState fabState = fabStates[i];
            if (fabState.fabVisible) {
                showFab(i);
            } else {
                hideFab(i);
            }
            int imageResource = fabState.fabImageResource;
            if (imageResource == -1) {
                imageResource = android.R.drawable.ic_input_add;
            }
            if (i == 0) {
                mFab1.setImageResource(imageResource);
            } else {
                mFab2.setImageResource(imageResource);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        final Context context = getContext();
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).invalidateOptionsMenu();
        }
        updateNoInternet();
        mFileCloudFabManager.onCurrentViewPagerPageChange(position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_home).setVisible(false);
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
            case R.id.action_search:
                SearchActivity.start(getContext());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        if (v == mFab1) {
            mFileCloudFabManager.onFabClick(0, mFab1);
        } else if (v == mFab2) {
            mFileCloudFabManager.onFabClick(1, mFab2);
        }
    }

    public int getCurrentFragmentIndex() {
        return mViewPager.getCurrentItem();
    }

    public Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.fragment_file_view_pager + ":" + mPagerAdapter.getItemId(getCurrentFragmentIndex()));
    }

    public void refreshListServer() {
        Fragment fabFragment = getCurrentFragment();
        if (fabFragment != null) {
            if (fabFragment instanceof FileCloudFragment) {
                FileCloudFragment fragmentFileManagerFragment = (FileCloudFragment) fabFragment;
                fragmentFileManagerFragment.refreshCurrentList();
            } else if (fabFragment instanceof FileMyCloudFragment) {
                FileMyCloudFragment fragmentFileManagerFragment = (FileMyCloudFragment) fabFragment;
                fragmentFileManagerFragment.refreshCurrentList();
            }
        }
    }

    public void add() {
        new FileAddDialog(getActivity(), -1, new IListener() {
            @Override
            public void execute() {
                refreshListServer();
            }
        }, null);
    }

    private void goHome() {
        Fragment fabFragment = getCurrentFragment();
        if (fabFragment != null) {
            if (fabFragment instanceof FileCloudFragment) {
                // TODO
            } else if (fabFragment instanceof FileMyCloudFragment) {
                // TODO
            }
        }
    }

    private void updateNoInternet() {
        if (!NetUtils.isInternetConnection(getContext())) {
            if (mCoordinatorLayoutView != null) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayoutView, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.refresh), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NetUtils.isInternetConnection(getContext())) {
                                    Fragment fabFragment = getCurrentFragment();
                                    if (fabFragment != null) {
                                        //fabFragment.onFocus();
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

    private void hideFab(
            final @IntRange(from = 0, to = FileLocalFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition) {
        switch (fabPosition) {
            case 0:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    mFab1.hide();
                } else {
                    mFab1.setVisibility(View.GONE);
                }
                break;
            case 1:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    mFab2.hide();
                } else {
                    mFab2.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void showFab(
            final @IntRange(from = 0, to = FileLocalFabManager.NUMBER_MAX_OF_FAB - 1) int fabPosition) {
        switch (fabPosition) {
            case 0:
                mFab1.show();
                break;
            case 1:
                mFab2.show();
                break;
        }
    }

    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {

        public FileManagerFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FileCloudFragment.newInstance(position);
                case 1:
                    return FileMyCloudFragment.newInstance(position);
                case 2:
                    return FileCloudDownloadedFragment.newInstance(position);
                default:
                    return FileCloudFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            switch (i) {
                case 0:
                    return getString(R.string.file_fragment_public_cloud);
                case 1:
                    return getString(R.string.file_fragment_my_cloud);
                case 2:
                    return "DOWNLOADED";
            }
            return "null";
        }
    }
}
