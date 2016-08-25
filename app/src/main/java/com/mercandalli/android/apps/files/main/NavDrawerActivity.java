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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.file.cloud.FileCloudPagerFragment;
import com.mercandalli.android.apps.files.file.local.FileLocalPagerFragment;
import com.mercandalli.android.apps.files.note.WorkspaceFragment;
import com.mercandalli.android.apps.files.settings.SettingsFragment;
import com.mercandalli.android.apps.files.storage.StorageFragment;
import com.mercandalli.android.apps.files.support.SupportFragment;
import com.mercandalli.android.apps.files.user.ProfileFragment;
import com.mercandalli.android.apps.files.user.community.CommunityFragment;
import com.mercandalli.android.library.base.dialog.DialogUtils;
import com.mercandalli.android.library.base.precondition.Preconditions;

import static com.mercandalli.android.apps.files.main.FileApp.logPerformance;

/**
 * An abstract class with a {@link NavigationView}.
 */
/* package */
abstract class NavDrawerActivity extends ApplicationActivity implements
        SetToolbarCallback,
        DrawerLayout.DrawerListener,
        NavDrawerView.OnNavDrawerClickCallback {

    private static final String TAG = "NavDrawerActivity";

    /**
     * Per the design guidelines, you should show the drawer on launch until the
     * user manually expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "NavDrawerActivity.navigation_drawer_learned";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private NavDrawerView mNavDrawerView;

    @NonNull
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
    private int mThankYou;
    private String mCurrentFragmentTag;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logPerformance(TAG, "NavDrawerActivity#onCreate() - Start");

        setContentView(R.layout.activity_main);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.activity_main_navigation_view);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mNavDrawerView = (NavDrawerView) findViewById(R.id.activity_main_nav_drawer_view);
        mNavDrawerView.setOnNavDrawerClickCallback(this);
        mNavDrawerView.setSelectedRow(this, getInitFragmentId());
        mNavDrawerView.setConnected(Config.isLogged());
        if (Config.isLogged()) {
            mNavDrawerView.setUser(Config.getUser());
        }

        logPerformance(TAG, "NavDrawerActivity#onCreate() - Middle");

        // Initial Fragment
        if (savedInstanceState == null) {
            selectItem(getInitFragmentId());
        } else {
            mFromSavedInstanceState = true;
        }

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        initAds();

        logPerformance(TAG, "NavDrawerActivity#onCreate() - End");
    }

    @Override
    protected void onDestroy() {
        mNavDrawerView.setOnNavDrawerClickCallback(null);
        super.onDestroy();
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
        return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && backPressed() ||
                super.onKeyDown(keyCode, event);
    }

    @Override
    public void setToolbar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(this);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void setTitleToolbar(@StringRes int title) {
        setTitle(title);
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
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
        }

        invalidateOptionsMenu();
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.onDrawerStateChanged(newState);
        }
    }

    @Nullable
    protected Fragment getCurrentFragment() {
        return mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
    }

    private void selectItem(final NavDrawerView.NavDrawerRow navDrawerRow) {
        Preconditions.checkNotNull(navDrawerRow);

        if (NavDrawerView.NavDrawerRow.LOGOUT.equals(navDrawerRow)) {
            DialogUtils.alert(
                    this,
                    "Log out",
                    "Do you want to log out?",
                    "Yes",
                    new DialogUtils.OnDialogUtilsListener() {
                        @Override
                        public void onDialogUtilsCalledBack() {
                            Config.reset(NavDrawerActivity.this);
                            NavDrawerActivity.this.finish();
                        }
                    }, getString(android.R.string.cancel), null);
            return;
        }

        mCurrentFragmentTag = navDrawerRow.getTag();
        Fragment fragment = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
        if (fragment == null) {
            switch (navDrawerRow) {
                case HEADER:
                    if (Config.isLogged()) {
                        fragment = ProfileFragment.newInstance();
                    } else {
                        fragment = StorageFragment.newInstance();
                    }
                    break;
                case FILES:
                    fragment = FileLocalPagerFragment.newInstance();
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
                case ADMIN:
                    fragment = AdminFragment.newInstance(getString(R.string.tab_admin));
                    break;
                case SETTINGS:
                    fragment = SettingsFragment.newInstance(getString(R.string.tab_settings));
                    break;
                case LOYALTY:
                    if (Constants.ADS_VISIBLE) {
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            requestNewInterstitial();
                            Toast.makeText(this, R.string.settings_ad_is_loading, Toast.LENGTH_SHORT).show();
                        }
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
        mFragmentManager.beginTransaction().replace(R.id.activity_main_content_frame, fragment, mCurrentFragmentTag).commit();
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
        final Fragment fragment = getCurrentFragment();
        return fragment instanceof BackFragment && ((BackFragment) fragment).back();
    }

    private NavDrawerView.NavDrawerRow getInitFragmentId() {
        return NavDrawerView.NavDrawerRow.FILES;
    }

    /**
     * Load a new interstitial ad asynchronously.
     */
    private void requestNewInterstitial() {
        if (mInterstitialAd == null || mInterstitialAd.isLoaded()) {
            return;
        }
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void initAds() {
        if (Constants.ADS_VISIBLE) {
            // Ads
            // Create an InterstitialAd object. This same object can be re-used whenever you want to
            // show an interstitial.
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(Constants.AD_MOB_KEY_NAV_DRAWER);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    if (!isFinishing()) {
                        switch (mThankYou) {
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
                        mThankYou++;
                    }
                }
            });
            requestNewInterstitial();
        }
    }
}
