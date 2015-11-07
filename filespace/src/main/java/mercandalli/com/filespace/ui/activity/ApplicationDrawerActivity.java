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
package mercandalli.com.filespace.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.Stack;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.SetToolbarCallback;
import mercandalli.com.filespace.ui.fragment.BackFragment;
import mercandalli.com.filespace.ui.fragment.HomeFragment;
import mercandalli.com.filespace.ui.fragment.ProfileFragment;
import mercandalli.com.filespace.ui.fragment.RoboticsFragment;
import mercandalli.com.filespace.ui.fragment.SettingsFragment;
import mercandalli.com.filespace.ui.fragment.WebFragment;
import mercandalli.com.filespace.ui.fragment.admin.AdminFragment;
import mercandalli.com.filespace.ui.fragment.community.CommunityFragment;
import mercandalli.com.filespace.ui.fragment.file.FileFragment;
import mercandalli.com.filespace.ui.fragment.genealogy.GenealogyFragment;
import mercandalli.com.filespace.ui.fragment.workspace.WorkspaceFragment;
import mercandalli.com.filespace.ui.navdrawer.NavDrawerItem;
import mercandalli.com.filespace.ui.navdrawer.NavDrawerItemList;
import mercandalli.com.filespace.ui.navdrawer.NavDrawerListAdapter;
import mercandalli.com.filespace.util.DialogUtils;

public abstract class ApplicationDrawerActivity extends ApplicationActivity implements SetToolbarCallback {

    public static final int[] noSelectable = new int[]{Constants.TAB_VIEW_TYPE_SECTION, Constants.TAB_VIEW_TYPE_SECTION_TITLE, Constants.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE};

    protected BackFragment mBackFragment;
    private final int INIT_FRAGMENT_ID = 3;
    private final int INIT_FRAGMENT_LOGGED_ID = 3;
    private Stack<Integer> ID_FRAGMENT_VISITED = new Stack<>();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private NavDrawerItemList navDrawerItems;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.activity_main_left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItems = new NavDrawerItemList();

        // Tab 0
        navDrawerItems.add(
                new NavDrawerItem(getConfig().getUserUsername(), "Edit your profile", new IListener() {
                    @Override
                    public void execute() {
                        if (isLogged()) {
                            mBackFragment = ProfileFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                        }
                    }
                }, R.drawable.ic_user, R.color.notifications_bar, Constants.TAB_VIEW_TYPE_PROFILE)
        );

        // Tab 1
        navDrawerItems.add(
                new NavDrawerItem("", R.drawable.ic_launcher, Constants.TAB_VIEW_TYPE_SECTION)
        );

        // Tab 2
        navDrawerItems.add(
                new NavDrawerItem(getString(R.string.tab_home), new IListener() {
                    @Override
                    public void execute() {
                        mBackFragment = HomeFragment.newInstance(getString(R.string.tab_home));
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                    }
                }, R.drawable.q_ic_drawer_home, R.drawable.q_ic_drawer_home_pressed, Constants.TAB_VIEW_TYPE_NORMAL)
        );

        // Tab 3
        navDrawerItems.add(
                new NavDrawerItem(getString(R.string.tab_files), new IListener() {
                    @Override
                    public void execute() {
                        mBackFragment = FileFragment.newInstance(getString(R.string.tab_files));
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                    }
                }, R.drawable.q_ic_drawer_files, R.drawable.q_ic_drawer_files_pressed, Constants.TAB_VIEW_TYPE_NORMAL)
        );

        // Tab 4
        navDrawerItems.add(
                new NavDrawerItem(getString(R.string.tab_workspace), new IListener() {
                    @Override
                    public void execute() {
                        mBackFragment = WorkspaceFragment.newInstance(getString(R.string.tab_workspace));
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                    }
                }, R.drawable.q_ic_drawer_workspace, R.drawable.q_ic_drawer_workspace_pressed, Constants.TAB_VIEW_TYPE_NORMAL)
        );

        // User logged Tabs
        if (getConfig().isLogged()) {
            // Tab 5
            navDrawerItems.add(
                    new NavDrawerItem(getString(R.string.tab_community), new IListener() {
                        @Override
                        public void execute() {
                            mBackFragment = CommunityFragment.newInstance(getString(R.string.tab_community));
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                        }
                    }, R.drawable.q_ic_drawer_community, R.drawable.q_ic_drawer_community_pressed, Constants.TAB_VIEW_TYPE_NORMAL)
            );
        }

        // Admin Tabs
        if (getConfig().getUser().isAdmin()) {
            // Tab 6
            navDrawerItems.add(
                    new NavDrawerItem(getString(R.string.tab_robotics), new IListener() {
                        @Override
                        public void execute() {
                            mBackFragment = RoboticsFragment.newInstance(getString(R.string.tab_settings));
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                        }
                    }, R.drawable.q_ic_drawer_robotics, R.drawable.q_ic_drawer_robotics_pressed, Constants.TAB_VIEW_TYPE_NORMAL)
            );

            // Tab 7
            navDrawerItems.add(
                    new NavDrawerItem(getString(R.string.tab_genealogy), new IListener() {
                        @Override
                        public void execute() {
                            mBackFragment = GenealogyFragment.newInstance(getString(R.string.tab_genealogy));
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                        }
                    }, R.drawable.q_ic_drawer_robotics, R.drawable.q_ic_drawer_robotics_pressed, Constants.TAB_VIEW_TYPE_NORMAL)
            );

            // Tab 8
            navDrawerItems.add(
                    new NavDrawerItem(getString(R.string.tab_admin), new IListener() {
                        @Override
                        public void execute() {
                            mBackFragment = AdminFragment.newInstance(getString(R.string.tab_admin));
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                        }
                    }, R.drawable.q_ic_drawer_data, R.drawable.q_ic_drawer_data_pressed, Constants.TAB_VIEW_TYPE_NORMAL)
            );
        }

        // Tab 9
        navDrawerItems.add(
                new NavDrawerItem("", R.drawable.ic_launcher, Constants.TAB_VIEW_TYPE_SECTION)
        );

        // Tab 10
        navDrawerItems.add(
                new NavDrawerItem("Other", R.drawable.ic_launcher, Constants.TAB_VIEW_TYPE_SECTION_TITLE)
        );

        // Tab 11
        navDrawerItems.add(
                new NavDrawerItem(
                        getString(R.string.tab_settings),
                        new IListener() {
                            @Override
                            public void execute() {
                                mBackFragment = SettingsFragment.newInstance(getString(R.string.tab_settings));
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                            }
                        },
                        R.drawable.ic_settings_grey,
                        Constants.TAB_VIEW_TYPE_SETTING)
        );

        // Tab 12
        if (getConfig().isLogged()) {
            navDrawerItems.add(
                    new NavDrawerItem(
                            getString(R.string.tab_log_out),
                            new IListener() {
                                @Override
                                public void execute() {
                                    DialogUtils.alert(ApplicationDrawerActivity.this, "Log out", "Do you want to log out?", "Yes", new IListener() {
                                        @Override
                                        public void execute() {
                                            ApplicationDrawerActivity.this.getConfig().reset();
                                            ApplicationDrawerActivity.this.finish();
                                        }
                                    }, getString(R.string.cancel), null);
                                }
                            },
                            R.drawable.ic_log_out,
                            Constants.TAB_VIEW_TYPE_SETTING_NO_SELECTABLE)
            );
        }

        // Tab 13
        navDrawerItems.add(
                new NavDrawerItem(
                        getString(R.string.tab_about),
                        new IListener() {
                            @Override
                            public void execute() {
                                WebFragment fr = WebFragment.newInstance();
                                fr.setInitURL(ApplicationDrawerActivity.this.getConfig().aboutURL);
                                mBackFragment = fr;
                                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mBackFragment).commit();
                            }
                        },
                        R.drawable.ic_help_grey,
                        Constants.TAB_VIEW_TYPE_SETTING)
        );

        // Initial Fragment
        if (savedInstanceState == null) {
            selectItem(getInitFragmentId());
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            for(Fragment fragment : fragments){
                if(fragment != null && fragment instanceof BackFragment) {
                    mBackFragment = (BackFragment) fragment;
                }
            }
        }

        mDrawerList.setAdapter(new NavDrawerListAdapter(this, navDrawerItems.getListe()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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

    public void selectItem(int position) {
        if (position >= navDrawerItems.size())
            return;
        for (NavDrawerItem nav : navDrawerItems.getListe())
            if (navDrawerItems.get(position).equals(nav)) {
                for (int i : noSelectable)
                    if (nav.viewType == i) {
                        if (nav.listenerClick != null)
                            nav.listenerClick.execute();
                        return;
                    }
            }
        if (position == getInitFragmentId()) {
            ID_FRAGMENT_VISITED = new Stack<>();
        }
        ID_FRAGMENT_VISITED.push(position);

        for (NavDrawerItem nav : navDrawerItems.getListe()) {
            nav.isSelected = false;
            if (navDrawerItems.get(position).equals(nav)) {
                nav.isSelected = true;
                if (nav.listenerClick != null)
                    nav.listenerClick.execute();
            }
        }
        mDrawerLayout.closeDrawer(mDrawerList);
        mDrawerList.setAdapter(new NavDrawerListAdapter(this, navDrawerItems.getListe()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
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

    public abstract void updateAdapters();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            if (backPressed())
                return true;
        return super.onKeyDown(keyCode, event);
    }

    public boolean backPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return true;
        }
        if (mBackFragment != null && mBackFragment.back()) {
            return true;
        }
        if (ID_FRAGMENT_VISITED == null) {
            Log.e("ApplicationDrawer", "backPressed() ID_FRAGMENT_VISITED==null");
            return false;
        }
        if (ID_FRAGMENT_VISITED.empty()) {
            Log.e("ApplicationDrawer", "backPressed() ID_FRAGMENT_VISITED.empty()");
            return false;
        }
        if (ID_FRAGMENT_VISITED.pop() == getInitFragmentId())
            return false;
        selectItem(ID_FRAGMENT_VISITED.pop());
        return true;
    }

    private int getInitFragmentId() {
        return isLogged() ? INIT_FRAGMENT_LOGGED_ID : INIT_FRAGMENT_ID;
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
}
