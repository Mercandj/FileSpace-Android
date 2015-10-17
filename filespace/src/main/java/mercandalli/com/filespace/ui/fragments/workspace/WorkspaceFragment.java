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
package mercandalli.com.filespace.ui.fragments.workspace;

import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.ui.dialogs.DialogAddFileManager;
import mercandalli.com.filespace.ui.fragments.BackFragment;

import org.json.JSONObject;

import mercandalli.com.filespace.R;

public class WorkspaceFragment extends BackFragment implements ViewPager.OnPageChangeListener {

    private static final int NB_FRAGMENT = 2;
    private static final int INIT_FRAGMENT = 0;
    public static final BackFragment LIST_BACK_FRAGMENT[] = new BackFragment[NB_FRAGMENT];
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private TabLayout tabs;

    public WorkspaceFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workspace, container, false);

        app.setTitle(R.string.tab_workspace);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        app.setToolbar(mToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            app.getWindow().setStatusBarColor(ContextCompat.getColor(app, R.color.notifications_bar));
        setHasOptionsMenu(true);

        mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager(), app);

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
        WorkspaceFragment.this.app.invalidateOptionsMenu();
        if (position < NB_FRAGMENT)
            if (LIST_BACK_FRAGMENT[position] != null)
                LIST_BACK_FRAGMENT[position].onFocus();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class FileManagerFragmentPagerAdapter extends FragmentPagerAdapter {
        ApplicationActivity app;

        public FileManagerFragmentPagerAdapter(FragmentManager fm, ApplicationActivity app) {
            super(fm);
            this.app = app;
        }

        @Override
        public BackFragment getItem(int i) {
            BackFragment backFragment = null;
            switch (i) {
                case 0:
                    backFragment = new NoteFragment();
                    break;
                case 1:
                    backFragment = new CryptFragment();
                    break;
                default:
                    backFragment = new CryptFragment();
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
        app.dialog = new DialogAddFileManager(app, -1, new IPostExecuteListener() {
            @Override
            public void execute(JSONObject json, String body) {
                if (json != null)
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
        menu.findItem(R.id.action_download).setVisible(false);
        menu.findItem(R.id.action_upload).setVisible(false);
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
