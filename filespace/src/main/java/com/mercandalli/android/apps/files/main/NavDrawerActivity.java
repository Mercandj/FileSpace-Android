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
package com.mercandalli.android.apps.files.main;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.admin.AdminFragment;
import com.mercandalli.android.apps.files.common.Preconditions;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.extras.genealogy.GenealogyFragment;
import com.mercandalli.android.apps.files.extras.robotics.RoboticsFragment;
import com.mercandalli.android.apps.files.file.cloud.FileCloudPagerFragment;
import com.mercandalli.android.apps.files.file.local.FileLocalPagerFragment;
import com.mercandalli.android.apps.files.note.WorkspaceFragment;
import com.mercandalli.android.apps.files.settings.SettingsFragment;
import com.mercandalli.android.apps.files.support.SupportFragment;
import com.mercandalli.android.apps.files.user.ProfileFragment;
import com.mercandalli.android.apps.files.user.community.CommunityFragment;

import java.util.List;

/**
 * An abstract class with a {@link NavigationView}.
 */
public abstract class NavDrawerActivity extends ApplicationActivity implements
        SetToolbarCallback,
        DrawerLayout.DrawerListener,
        NavDrawerView.OnNavDrawerClickCallback {

    /**
     * Per the design guidelines, you should show the drawer on launch until the
     * user manually expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "NavDrawerActivity.navigation_drawer_learned";

    /**
     * The current fragment displayed.
     */
    protected BackFragment mBackFragment;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;

    private final FragmentManager mFragmentManager = getSupportFragmentManager();

    /**
     * True if returns from a saved instance state, false otherwise.
     */
    private boolean mFromSavedInstanceState;

    /**
     * True if the user has already learned to user the navigation drawer, false otherwise.
     * <p/>
     * In this implementation, we consider that the user learns the navigation drawer when she closes it.
     */
    private boolean mUserLearnedDrawer;

    private InterstitialAd mInterstitialAd;
    private int mThanhYou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.activity_main_navigation_view);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        NavDrawerView navDrawerView = (NavDrawerView) findViewById(R.id.activity_main_nav_drawer_view);
        navDrawerView.setOnNavDrawerClickCallback(this);
        navDrawerView.setSelectedRow(this, getInitFragmentId());
        navDrawerView.setConnected(isLogged());
        if (isLogged()) {
            navDrawerView.setUser(getConfig().getUser(), getConfig().getUserProfilePicture(this));
        }

        // Initial Fragment
        if (savedInstanceState == null) {
            selectItem(getInitFragmentId());
        } else {
            mFromSavedInstanceState = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment instanceof BackFragment) {
                    mBackFragment = (BackFragment) fragment;
                }
            }
        }

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        // Ads
        // Create an InterstitialAd object. This same object can be re-used whenever you want to
        // show an interstitial.
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4616471093567176/1180013643");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (!isFinishing()) {
                    switch (mThanhYou) {
                        case 0:
                            Toast.makeText(NavDrawerActivity.this, R.string.settings_ad_thank_you_1, Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(NavDrawerActivity.this, R.string.settings_ad_thank_you_2, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(NavDrawerActivity.this, R.string.settings_ad_thank_you_3, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(NavDrawerActivity.this, R.string.settings_ad_thank_you_4, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    mThanhYou++;
                }
            }
        });
        if (!mInterstitialAd.isLoaded()) {
            requestNewInterstitial();
        }
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
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && backPressed() || super.onKeyDown(keyCode, event);
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(this);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.onDrawerOpened(drawerView);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.onDrawerClosed(drawerView);
        }
        if (!mUserLearnedDrawer) {
            // The user manually closed the drawer; store this flag to prevent auto-showing
            // the navigation drawer automatically in the future.
            mUserLearnedDrawer = true;
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(NavDrawerActivity.this);
            sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
                    .apply();
        }

        invalidateOptionsMenu();
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.onDrawerStateChanged(newState);
        }
    }

    public BackFragment getBackFragment() {
        return mBackFragment;
    }

    /* package */ void selectItem(final NavDrawerView.NavDrawerRow navDrawerRow) {
        Preconditions.checkNotNull(navDrawerRow);

        if (navDrawerRow.equals(NavDrawerView.NavDrawerRow.LOGOUT)) {
            DialogUtils.alert(NavDrawerActivity.this, "Log out", "Do you want to log out?", "Yes", new IListener() {
                @Override
                public void execute() {
                    NavDrawerActivity.this.getConfig().reset(NavDrawerActivity.this);
                    NavDrawerActivity.this.finish();
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
                    } else {
                        return;
                    }
                    break;
                case FILES:
                    fragment = FileLocalPagerFragment.newInstance(getString(R.string.tab_files));
                    break;
                case CLOUD:
                    fragment = FileCloudPagerFragment.newInstance(getString(R.string.tab_cloud));
                    break;
                case WORKSPACE:
                    fragment = WorkspaceFragment.newInstance(getString(R.string.tab_notes));
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
                case LOYALTY:
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        requestNewInterstitial();
                        Toast.makeText(this, R.string.settings_ad_is_loading, Toast.LENGTH_SHORT).show();
                    }
                    return;
                case LOGOUT:
                    break;
                case SUPPORT:
                    fragment = SupportFragment.newInstance(getString(R.string.tab_support));
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

    /**
     * Close the nav drawer or follow back to fragment.
     *
     * @return true if action done, false will finish.
     */
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

    /**
     * Load a new interstitial ad asynchronously.
     */
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public abstract void updateAdapters();
}
