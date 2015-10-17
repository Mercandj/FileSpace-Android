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
package mercandalli.com.filespace.ui.fragments.community;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.ui.dialogs.DialogAddFileManager;
import mercandalli.com.filespace.ui.fragments.BackFragment;

import org.json.JSONObject;

import mercandalli.com.filespace.R;

public class CommunityFragment extends BackFragment implements ViewPager.OnPageChangeListener {

    private static final int NB_FRAGMENT = 3;
    private static final int INIT_FRAGMENT = 1;
    public static BackFragment listBackFragment[] = new BackFragment[NB_FRAGMENT];
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private TabLayout tabs;
    private Toolbar mToolbar;

    public static CommunityFragment newInstance() {
        Bundle args = new Bundle();
        CommunityFragment fragment = new CommunityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community_manager, container, false);

        app.setTitle(R.string.tab_community);

        mToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        if (mToolbar != null) {
            //mToolbar.setBackgroundColor(getResources().getColor(R.color.actionbar));
            app.setToolbar(mToolbar);
            //app.setStatusBarColor(R.color.notifications_bar);
        }
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
        if (result >= listBackFragment.length)
            return -1;
        return mViewPager.getCurrentItem();
    }

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if (listBackFragment == null || currentFragmentId == -1)
            return false;
        BackFragment backFragment = listBackFragment[currentFragmentId];
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
        CommunityFragment.this.app.invalidateOptionsMenu();
        if (position < NB_FRAGMENT)
            if (listBackFragment[position] != null)
                listBackFragment[position].onFocus();
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
                    backFragment = UserFragment.newInstance();
                    break;
                case 1:
                    backFragment = TalkFragment.newInstance();
                    break;
                case 2:
                    backFragment = UserLocationFragment.newInstance();
                    break;
                default:
                    backFragment = UserFragment.newInstance();
                    break;
            }
            listBackFragment[i] = backFragment;
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
                    title = "USERS";
                    break;
                case 1:
                    title = "TALKS";
                    break;
                case 2:
                    title = "LOCATIONS";
                    break;
                default:
                    title = "USERS";
                    break;
            }
            return title;
        }
    }


    public void refreshListServer() {
        refreshListServer(null);
    }

    public void refreshListServer(String search) {
        if (listBackFragment[0] != null)
            if (listBackFragment[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) listBackFragment[0];
                fragmentFileManagerFragment.refreshList(search);
            }
        if (listBackFragment[1] != null)
            if (listBackFragment[1] instanceof TalkFragment) {
                TalkFragment fragmentFileManagerFragment = (TalkFragment) listBackFragment[1];
                fragmentFileManagerFragment.refreshList(search);
            }
    }

    public void updateAdapterListServer() {
        if (listBackFragment[0] != null)
            if (listBackFragment[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) listBackFragment[0];
                fragmentFileManagerFragment.updateAdapter();
            }
        if (listBackFragment.length > 1)
            if (listBackFragment[1] != null)
                if (listBackFragment[1] instanceof TalkFragment) {
                    TalkFragment fragmentFileManagerFragment = (TalkFragment) listBackFragment[1];
                    fragmentFileManagerFragment.updateAdapter();
                }
    }

    public void refreshAdapterListServer() {
        if (listBackFragment[0] != null)
            if (listBackFragment[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) listBackFragment[0];
                fragmentFileManagerFragment.refreshList();
            }
        if (listBackFragment.length > 1)
            if (listBackFragment[1] != null)
                if (listBackFragment[1] instanceof TalkFragment) {
                    TalkFragment fragmentFileManagerFragment = (TalkFragment) listBackFragment[1];
                    fragmentFileManagerFragment.refreshList();
                }
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
}
