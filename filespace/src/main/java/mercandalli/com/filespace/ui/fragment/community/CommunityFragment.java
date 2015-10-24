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
package mercandalli.com.filespace.ui.fragment.community;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.listener.SetToolbarCallback;
import mercandalli.com.filespace.ui.dialog.DialogAddFileManager;
import mercandalli.com.filespace.ui.fragment.BackFragment;

public class CommunityFragment extends BackFragment implements ViewPager.OnPageChangeListener {

    private static final String BUNDLE_ARG_TITLE = "CommunityFragment.Args.BUNDLE_ARG_TITLE";

    private static final int NB_FRAGMENT = 3;
    private static final int INIT_FRAGMENT = 1;
    public static BackFragment mBackFragmentArray[] = new BackFragment[NB_FRAGMENT];
    private ViewPager mViewPager;
    private FileManagerFragmentPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;

    private String mTitle;
    private Toolbar mToolbar;
    private SetToolbarCallback mSetToolbarCallback;

    public static CommunityFragment newInstance(String title) {
        final CommunityFragment fragment = new CommunityFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_community_manager, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.fragment_community_toolbar);
        mToolbar.setTitle(mTitle);
        mSetToolbarCallback.setToolbar(mToolbar);
        setStatusBarColor(mActivity, R.color.notifications_bar);
        setHasOptionsMenu(true);
        mPagerAdapter = new FileManagerFragmentPagerAdapter(this.getChildFragmentManager());

        mTabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);


        mViewPager.setOffscreenPageLimit(this.NB_FRAGMENT - 1);
        mViewPager.setCurrentItem(this.INIT_FRAGMENT);

        mTabLayout.setupWithViewPager(mViewPager);

        return rootView;
    }

    public int getCurrentFragmentIndex() {
        if (mViewPager == null)
            return -1;
        int result = mViewPager.getCurrentItem();
        if (result >= mBackFragmentArray.length)
            return -1;
        return mViewPager.getCurrentItem();
    }

    @Override
    public boolean back() {
        int currentFragmentId = getCurrentFragmentIndex();
        if (mBackFragmentArray == null || currentFragmentId == -1)
            return false;
        BackFragment backFragment = mBackFragmentArray[currentFragmentId];
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
            if (mBackFragmentArray[position] != null)
                mBackFragmentArray[position].onFocus();
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
            mBackFragmentArray[i] = backFragment;
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
        if (mBackFragmentArray[0] != null)
            if (mBackFragmentArray[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) mBackFragmentArray[0];
                fragmentFileManagerFragment.refreshList(search);
            }
        if (mBackFragmentArray[1] != null)
            if (mBackFragmentArray[1] instanceof TalkFragment) {
                TalkFragment fragmentFileManagerFragment = (TalkFragment) mBackFragmentArray[1];
                fragmentFileManagerFragment.refreshList(search);
            }
    }

    public void updateAdapterListServer() {
        if (mBackFragmentArray[0] != null)
            if (mBackFragmentArray[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) mBackFragmentArray[0];
                fragmentFileManagerFragment.updateAdapter();
            }
        if (mBackFragmentArray.length > 1)
            if (mBackFragmentArray[1] != null)
                if (mBackFragmentArray[1] instanceof TalkFragment) {
                    TalkFragment fragmentFileManagerFragment = (TalkFragment) mBackFragmentArray[1];
                    fragmentFileManagerFragment.updateAdapter();
                }
    }

    public void refreshAdapterListServer() {
        if (mBackFragmentArray[0] != null)
            if (mBackFragmentArray[0] instanceof UserFragment) {
                UserFragment fragmentFileManagerFragment = (UserFragment) mBackFragmentArray[0];
                fragmentFileManagerFragment.refreshList();
            }
        if (mBackFragmentArray.length > 1)
            if (mBackFragmentArray[1] != null)
                if (mBackFragmentArray[1] instanceof TalkFragment) {
                    TalkFragment fragmentFileManagerFragment = (TalkFragment) mBackFragmentArray[1];
                    fragmentFileManagerFragment.refreshList();
                }
    }

    public void add() {
        new DialogAddFileManager(mActivity, mApplicationCallback, -1, new IPostExecuteListener() {
            @Override
            public void onPostExecute(JSONObject json, String body) {
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
