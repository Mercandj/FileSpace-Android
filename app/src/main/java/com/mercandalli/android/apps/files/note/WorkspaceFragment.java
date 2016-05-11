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
package com.mercandalli.android.apps.files.note;

import android.content.Context;
import android.os.Bundle;
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

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.file.FileAddDialog;

import static com.mercandalli.android.library.base.view.StatusBarUtils.setStatusBarColor;

public class WorkspaceFragment extends BackFragment implements ViewPager.OnPageChangeListener {

    private static final String BUNDLE_ARG_TITLE = "WorkspaceFragment.Args.BUNDLE_ARG_TITLE";

    private static final int NB_FRAGMENT = 1;
    private static final int INIT_FRAGMENT = 0;
    public static final BackFragment LIST_BACK_FRAGMENT[] = new BackFragment[NB_FRAGMENT];
    private ViewPager mViewPager;

    private String mTitle;
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

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.fragment_workspace_toolbar);
        toolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(toolbar);
        setStatusBarColor(getActivity(), R.color.status_bar);
        setHasOptionsMenu(true);

        FileManagerFragmentPagerAdapter pagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager());

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);
        mViewPager.setCurrentItem(INIT_FRAGMENT);

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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        final Context context = getContext();
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).invalidateOptionsMenu();
        }
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
            String title;
            switch (i) {
                case 0:
                    title = "NOTES";
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
        new FileAddDialog(getActivity(), -1, new IListener() {
            @Override
            public void execute() {
                refreshListServer();
            }
        }, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_delete).setVisible(true);
        menu.findItem(R.id.action_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_home).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int currentIndex = getCurrentFragmentIndex();
        switch (item.getItemId()) {
            case R.id.action_delete:
                switch (currentIndex) {
                    case 0:
                        ((NoteFragment) WorkspaceFragment.LIST_BACK_FRAGMENT[currentIndex]).delete();
                        break;
                }
                return true;
            case R.id.action_share:
                switch (currentIndex) {
                    case 0:
                        ((NoteFragment) WorkspaceFragment.LIST_BACK_FRAGMENT[currentIndex]).share();
                        break;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
