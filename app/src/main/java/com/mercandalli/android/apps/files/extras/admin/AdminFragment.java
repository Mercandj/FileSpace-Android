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
package com.mercandalli.android.apps.files.extras.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.extras.admin.game.GameFragment;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.fragment.EmptyFragment;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.common.view.NonSwipeableViewPager;


public class AdminFragment extends BackFragment implements ViewPager.OnPageChangeListener {

    private static final String BUNDLE_ARG_TITLE = "AdminFragment.Args.BUNDLE_ARG_TITLE";

    private static final int NB_FRAGMENT = 8;
    private static final int INIT_FRAGMENT = 0;
    public static final BackFragment LIST_BACK_FRAGMENT[] = new BackFragment[NB_FRAGMENT];
    private NonSwipeableViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private TabLayout tabs;

    private AppBarLayout mAppBarLayout;

    private String mTitle;
    private Toolbar mToolbar;
    private SetToolbarCallback mSetToolbarCallback;

    public static AdminFragment newInstance(String title) {
        final AdminFragment fragment = new AdminFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.fragment_admin_toolbar);
        mToolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(mToolbar);
        setStatusBarColor(mActivity, R.color.status_bar);
        setHasOptionsMenu(true);


        mAppBarLayout = (AppBarLayout) rootView.findViewById(R.id.fragment_admin_app_bar_layout);
        mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager());

        tabs = (TabLayout) rootView.findViewById(R.id.fragment_admin_tab_layout);
        mViewPager = (NonSwipeableViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setNonSwipeableItem(7);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(this);

        mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
        mViewPager.setCurrentItem(INIT_FRAGMENT);

        tabs.setupWithViewPager(mViewPager);

        return rootView;
    }

    public int getCurrentFragmentIndex() {
        if (mViewPager == null) {
            return -1;
        }
        int result = mViewPager.getCurrentItem();
        if (result >= LIST_BACK_FRAGMENT.length) {
            return -1;
        }
        return mViewPager.getCurrentItem();
    }

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if (currentFragmentId == -1) {
            return false;
        }
        BackFragment backFragment = LIST_BACK_FRAGMENT[currentFragmentId];
        return backFragment != null && backFragment.back();
    }

    @Override
    public void onFocus() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mApplicationCallback.invalidateMenu();
        mAppBarLayout.setExpanded(true);
        if (position < NB_FRAGMENT && LIST_BACK_FRAGMENT[position] != null) {
            LIST_BACK_FRAGMENT[position].onFocus();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {

        public FileManagerFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BackFragment getItem(int i) {
            BackFragment backFragment;
            switch (i) {
                case 0:
                    backFragment = ServerDataFragment.newInstance();
                    break;
                case 1:
                    backFragment = ServerLogsFragment.newInstance();
                    break;
                case 2:
                    backFragment = UserAddFragment.newInstance();
                    break;
                case 5:
                    backFragment = RequestFragment.newInstance();
                    break;
                case 6:
                    backFragment = GameFragment.newInstance();
                    break;
                case 7:
                    backFragment = StatisticsFragment.newInstance();
                    break;
                default:
                    backFragment = EmptyFragment.newInstance();
                    break;
            }
            LIST_BACK_FRAGMENT[i] = backFragment;
            return backFragment;
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
                    title = "SERVER DATA";
                    break;
                case 1:
                    title = "SERVER LOGS";
                    break;
                case 2:
                    title = "USER ADD";
                    break;
                case 3:
                    title = "USER DATA";
                    break;
                case 4:
                    title = "USER LICENCES";
                    break;
                case 5:
                    title = "REQUEST";
                    break;
                case 6:
                    title = "GAME";
                    break;
                case 7:
                    title = "STATS";
                    break;
            }
            return title;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_home).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);

        if (getCurrentFragmentIndex() == 5) {
            menu.findItem(R.id.action_delete).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (getCurrentFragmentIndex() == 5) {
                    ((RequestFragment) AdminFragment.LIST_BACK_FRAGMENT[getCurrentFragmentIndex()]).delete();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}