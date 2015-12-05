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
package com.mercandalli.android.apps.files.workspace;

import android.content.Context;
import android.os.Bundle;
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
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.file.FileAddDialog;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;

public class WorkspaceFragment extends BackFragment implements ViewPager.OnPageChangeListener {

    private static final String BUNDLE_ARG_TITLE = "WorkspaceFragment.Args.BUNDLE_ARG_TITLE";

    private static final int NB_FRAGMENT = 2;
    private static final int INIT_FRAGMENT = 0;
    public static final BackFragment LIST_BACK_FRAGMENT[] = new BackFragment[NB_FRAGMENT];
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private TabLayout tabs;

    private String mTitle;
    private Toolbar mToolbar;
    private SetToolbarCallback mSetToolbarCallback;

    public static WorkspaceFragment newInstance(String title) {
        final WorkspaceFragment fragment = new WorkspaceFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_workspace, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.fragment_workspace_toolbar);
        mToolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(mToolbar);
        setStatusBarColor(mActivity, R.color.status_bar);
        setHasOptionsMenu(true);

        mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager());

        tabs = (TabLayout) rootView.findViewById(R.id.tabs);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mViewPager.setOffscreenPageLimit(this.NB_FRAGMENT - 1);
        mViewPager.setCurrentItem(this.INIT_FRAGMENT);

        tabs.setupWithViewPager(mViewPager);

        return rootView;
    }

    public int getCurrentFragmentIndex() {
        if (mViewPager == null)
            return -1;
        int result = mViewPager.getCurrentItem();
        if (result >= LIST_BACK_FRAGMENT.length)
            return -1;
        return mViewPager.getCurrentItem();
    }

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if (LIST_BACK_FRAGMENT == null || currentFragmentId == -1)
            return false;
        BackFragment backFragment = LIST_BACK_FRAGMENT[currentFragmentId];
        if (backFragment == null)
            return false;
        return backFragment.back();
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
        if (position < NB_FRAGMENT)
            if (LIST_BACK_FRAGMENT[position] != null)
                LIST_BACK_FRAGMENT[position].onFocus();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {

        public FileManagerFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BackFragment getItem(int i) {
            BackFragment backFragment = null;
            switch (i) {
                case 0:
                    backFragment = NoteFragment.newInstance();
                    break;
                case 1:
                    backFragment = CryptFragment.newInstance();
                    break;
                default:
                    backFragment = CryptFragment.newInstance();
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
                    title = "NOTE";
                    break;
                case 1:
                    title = "CRYPT";
                    break;
                default:
                    title = "CRYPT";
                    break;
            }
            return title;
        }
    }


    public void refreshListServer() {
        refreshListServer(null);
    }

    public void refreshListServer(String search) {

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(true);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_home).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                final int currentIndex = getCurrentFragmentIndex();
                switch (currentIndex) {
                    case 0:
                        ((NoteFragment) WorkspaceFragment.LIST_BACK_FRAGMENT[currentIndex]).delete();
                        break;
                    case 1:
                        ((CryptFragment) WorkspaceFragment.LIST_BACK_FRAGMENT[currentIndex]).delete();
                        break;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
