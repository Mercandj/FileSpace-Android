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
package com.mercandalli.android.filespace.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.admin.AdminFragment;
import com.mercandalli.android.filespace.common.Preconditions;
import com.mercandalli.android.filespace.common.fragment.BackFragment;
import com.mercandalli.android.filespace.common.fragment.WebFragment;
import com.mercandalli.android.filespace.common.listener.IListener;
import com.mercandalli.android.filespace.common.listener.SetToolbarCallback;
import com.mercandalli.android.filespace.common.util.DialogUtils;
import com.mercandalli.android.filespace.extras.genealogy.GenealogyFragment;
import com.mercandalli.android.filespace.extras.robotics.RoboticsFragment;
import com.mercandalli.android.filespace.file.FileFragment;
import com.mercandalli.android.filespace.home.HomeFragment;
import com.mercandalli.android.filespace.settings.SettingsFragment;
import com.mercandalli.android.filespace.user.ProfileFragment;
import com.mercandalli.android.filespace.user.community.CommunityFragment;
import com.mercandalli.android.filespace.workspace.WorkspaceFragment;

import java.util.List;

public abstract class DrawerActivity extends ApplicationActivity implements
        SetToolbarCallback,
        NavDrawerView.OnNavDrawerClickCallback {

    protected BackFragment mBackFragment;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private final FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.activity_main_navigation_view);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        NavDrawerView navDrawerView = (NavDrawerView) findViewById(R.id.activity_main_nav_drawer_view);
        navDrawerView.setOnNavDrawerClickCallback(this);
        navDrawerView.setSelectedRow(this, getInitFragmentId());
        navDrawerView.setConnected(isLogged());
        navDrawerView.setUser(getConfig().getUser());

        // Initial Fragment
        if (savedInstanceState == null) {
            selectItem(getInitFragmentId());
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment instanceof BackFragment) {
                    mBackFragment = (BackFragment) fragment;
                }
            }
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNavDrawerClicked(final NavDrawerView.NavDrawerRow navDrawerRow, final View v) {
        selectItem(navDrawerRow);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (backPressed()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    public BackFragment getBackFragment() {
        return mBackFragment;
    }

    /**
     * TODO - Need to be removed.
     */
    public void selectItem(int position) {
        switch (position) {
            case 3:
                selectItem(NavDrawerView.NavDrawerRow.FILES);
                break;
            case 4:
                selectItem(NavDrawerView.NavDrawerRow.WORKSPACE);
                break;
        }
    }

    /* package */ void selectItem(NavDrawerView.NavDrawerRow navDrawerRow) {
        Preconditions.checkNotNull(navDrawerRow);

        if (navDrawerRow.equals(NavDrawerView.NavDrawerRow.LOGOUT)) {
            DialogUtils.alert(DrawerActivity.this, "Log out", "Do you want to log out?", "Yes", new IListener() {
                @Override
                public void execute() {
                    DrawerActivity.this.getConfig().reset();
                    DrawerActivity.this.finish();
                }
            }, getString(R.string.cancel), null);
            return;
        }

        final String fragmentTag = navDrawerRow.getTag();
        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            switch (navDrawerRow) {
                case HEADER:
                    if (isLogged()) {
                        fragment = ProfileFragment.newInstance();
                    }
                    break;
                case HOME:
                    fragment = HomeFragment.newInstance(getString(R.string.tab_home));
                    break;
                case FILES:
                    fragment = FileFragment.newInstance(getString(R.string.tab_files));
                    break;
                case WORKSPACE:
                    fragment = WorkspaceFragment.newInstance(getString(R.string.tab_workspace));
                    break;
                case COMMUNITY:
                    fragment = CommunityFragment.newInstance(getString(R.string.tab_community));
                    break;
                case ROBOTICS:
                    fragment = RoboticsFragment.newInstance(getString(R.string.tab_robotics));
                    break;
                case GENEALOGY:
                    fragment = GenealogyFragment.newInstance(getString(R.string.tab_genealogy));
                    break;
                case ADMIN:
                    fragment = AdminFragment.newInstance(getString(R.string.tab_admin));
                    break;
                case SETTINGS:
                    fragment = SettingsFragment.newInstance(getString(R.string.tab_settings));
                    break;
                case LOGOUT:
                    break;
                case ABOUT:
                    fragment = WebFragment.newInstance();
                    break;
                default:
                    throw new IllegalArgumentException("Wrong navDrawerRow in selectItem() " + navDrawerRow);
            }
        }
        mBackFragment = (BackFragment) fragment;
        mFragmentManager.beginTransaction().replace(R.id.activity_main_content_frame, fragment, fragmentTag).commit();
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        }
    }

    private boolean backPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
            return true;
        }
        return mBackFragment != null && mBackFragment.back();
    }

    private NavDrawerView.NavDrawerRow getInitFragmentId() {
        return NavDrawerView.NavDrawerRow.FILES;
    }

    public abstract void updateAdapters();
}
