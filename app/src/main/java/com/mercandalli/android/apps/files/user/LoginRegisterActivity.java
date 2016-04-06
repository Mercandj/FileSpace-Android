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
package com.mercandalli.android.apps.files.user;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.dialog.ConfirmationDialog;
import com.mercandalli.android.apps.files.common.dialog.DialogCallback;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.view.PagerSlidingTabStrip;
import com.mercandalli.android.apps.files.main.ApplicationActivity;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.MainActivity;
import com.mercandalli.android.library.baselibrary.java.HashUtils;
import com.mercandalli.android.library.baselibrary.java.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.GET_ACCOUNTS;

public class LoginRegisterActivity extends ApplicationActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, DialogCallback, GoogleApiClient.OnConnectionFailedListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_GOOGLE_SIGN_IN = 0;

    /**
     * Id to identity READ_CONTACTS permission request.
     **/
    private static final int REQUEST_GET_ACCOUNT_PERMISSION = 1;

    /**
     * The name of the {@link android.content.SharedPreferences} used for saving the record audio permission state.
     */
    private static final String SHARED_PREFERENCES_GET_ACCOUNT_PERMISSION = "ContactsPermission";

    /**
     * The key of the value stored for knowing if it is the first request of the account permission.
     */
    private static final String KEY_IS_FIRST_ACCOUNT_PERMISSION_REQUEST = "AccountPermission.Key.KEY_1";
    private static final int RC_SIGN_IN = 28757;

    private static final String ADMIN = "admin";

    private static final int NB_FRAGMENT = 2;
    private int mInitialFragment = 1;

    public Fragment mListFragment[] = new Fragment[NB_FRAGMENT];
    private ViewPager mViewPager;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private boolean mRequestLaunched;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        RegisterLoginPagerAdapter mPagerAdapter = new RegisterLoginPagerAdapter(getSupportFragmentManager(), this);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.activity_register_login_tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(NB_FRAGMENT - 1);

        if (Config.getUserUsername() == null || Config.getUserPassword() == null) {
            mInitialFragment = 0;
        } else if (Config.getUserUsername().equals("") || Config.getUserPassword().equals("")) {
            mInitialFragment = 0;
        }

        mViewPager.setCurrentItem(this.mInitialFragment);

        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(ContextCompat.getColor(this, android.R.color.white));

        if (this.getConfig().isAutoConnection() && Config.getUserUsername() != null && Config.getUserPassword() != null &&
                !Config.getUserUsername().equals("") && !Config.getUserPassword().equals("") && Config.getUserId() != -1) {
            connectionSucceed();
        }

        findViewById(R.id.activity_register_login_signin).setOnClickListener(this);
        findViewById(R.id.activity_register_login_gg_sign).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
/*
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();*/
    }

    public void connectionSucceed() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        this.finish();
    }

    @Override
    public void updateAdapters() {

    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onPageSelected(int arg0) {
        LoginRegisterActivity.this.invalidateOptionsMenu();
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // Nothing here
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // Nothing here
    }

    public int getCurrentFragmentIndex() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.activity_register_login_gg_sign:
                onGoogleSignInClicked();
                break;
            case R.id.activity_register_login_signin:
                if (mListFragment[getCurrentFragmentIndex()] != null) {
                    if (mListFragment[getCurrentFragmentIndex()] instanceof RegistrationFragment) {
                        ((RegistrationFragment) mListFragment[getCurrentFragmentIndex()]).inscription();
                    } else if (mListFragment[getCurrentFragmentIndex()] instanceof LoginFragment) {
                        ((LoginFragment) mListFragment[getCurrentFragmentIndex()]).login();
                    }
                }
                break;
        }
    }

    /**
     * Action when the user clicked the Google + signing button
     **/
    private void onGoogleSignInClicked() {

        if (ContextCompat.checkSelfPermission(this, GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
            /*
            mShouldResolve = true;
            mGoogleApiClient.connect();
            */
        } else if (shouldRequestAccountPermissionRationale()) {
            requestAccountPermissionRationale();
        } else {
            requestAccountPermissionInSettings();
        }

    }

    /**
     * Show a UI rationale for requesting the {@link android.Manifest.permission#GET_ACCOUNTS}.
     * <p/>
     * If the user agree, a native poop-up will appear.
     */
    private void requestAccountPermissionRationale() {
        ConfirmationDialog.newInstance(
                "Permission needed",
                "FileSpace needs the Contacts permission in order to link your Google account to the app. Nothing will be saved, the permission is only used for the log-in.",
                android.R.string.ok,
                android.R.string.cancel,
                this,
                this).show();
    }

    private void requestAccountPermissionInSettings() {
        Snackbar.make(findViewById(R.id.activity_register_login_signin), "FileSpace needs the Contacts permission for using Google.", Snackbar.LENGTH_LONG)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                })
                .show();
    }

    /**
     * Checks if the Contact permission has to be requested when the user want to log-in using
     * his Google account.
     *
     * @return : False if the permission has already been granted, true otherwise.
     */
    private boolean shouldRequestAccountPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, GET_ACCOUNTS) ||
                isFirstAccountPermissionRequest();
    }

    @Override
    public void onPositiveClick() {
        // The user understood why the app needs the contacts permission
        // Remember that the we requested the contacts permission.
        final SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_GET_ACCOUNT_PERMISSION, MODE_PRIVATE)
                .edit();
        editor.putBoolean(KEY_IS_FIRST_ACCOUNT_PERMISSION_REQUEST, false);
        editor.apply();

        //Request the account permission
        ActivityCompat.requestPermissions(this, new String[]{GET_ACCOUNTS}, REQUEST_GET_ACCOUNT_PERMISSION);
    }

    @Override
    public void onNegativeClick() {

    }

    @Override
    public void onNeutralClick() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("RegisterLoginActivity", "onConnectionFailed error");
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_GOOGLE_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an error dialog.
                //TODO login 1: Show a toast or a snackbar error.
            }
        } else {
            //TODO login 3 : Show sign-out UI
        }
    }

    public class RegisterLoginPagerAdapter extends FragmentPagerAdapter {
        ApplicationActivity app;

        public RegisterLoginPagerAdapter(FragmentManager fm, ApplicationActivity app) {
            super(fm);
            this.app = app;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch (i) {
                case 0:
                    fragment = RegistrationFragment.newInstance();
                    break;
                case 1:
                    fragment = LoginFragment.newInstance();
                    break;
                default:
                    fragment = RegistrationFragment.newInstance();
                    break;
            }
            mListFragment[i] = fragment;
            return fragment;
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
                    title = "REGISTER";
                    break;
                case 1:
                    title = "LOGIN";
                    break;
            }
            return title;
        }
    }


    private void googlePlusRegisterLogin(String username, String password) {
        if (mRequestLaunched) {
            return;
        }
        mRequestLaunched = true;

        final UserModel user = new UserModel();
        user.username = username;
        user.password = password;

        if (StringUtils.isNullOrEmpty(Constants.URL_DOMAIN)) {
            mRequestLaunched = false;
            return;
        }

        // Login : POST /user
        List<StringPair> parameters = new ArrayList<>();
        parameters.add(new StringPair("google_plus", "true"));
        parameters.add(new StringPair("username", "" + user.username));
        parameters.add(new StringPair("password", "" + user.password));
        if (NetUtils.isInternetConnection(this)) {
            (new TaskPost(this, this, Constants.URL_DOMAIN + Config.ROUTE_USER, new IPostExecuteListener() {
                @Override
                public void onPostExecute(JSONObject json, String body) {
                    try {
                        if (json != null) {
                            if (json.has("succeed") && json.getBoolean("succeed")) {
                                if (!StringUtils.isNullOrEmpty(user.username)) {
                                    LoginRegisterActivity.this.getConfig().setUserUsername(LoginRegisterActivity.this, user.username);
                                }

                                if (!StringUtils.isNullOrEmpty(user.password)) {
                                    LoginRegisterActivity.this.getConfig().setUserPassword(LoginRegisterActivity.this, user.password);
                                }

                                connectionSucceed();
                            }
                            if (json.has("user")) {
                                JSONObject user = json.getJSONObject("user");
                                if (user.has("id")) {
                                    LoginRegisterActivity.this.getConfig().setUserId(LoginRegisterActivity.this, user.getInt("id"));
                                }
                                if (user.has(ADMIN)) {
                                    Object admin_obj = user.get(ADMIN);
                                    if (admin_obj instanceof Integer) {
                                        LoginRegisterActivity.this.getConfig().setUserAdmin(LoginRegisterActivity.this, user.getInt(ADMIN) == 1);
                                    } else if (admin_obj instanceof Boolean) {
                                        LoginRegisterActivity.this.getConfig().setUserAdmin(LoginRegisterActivity.this, user.getBoolean(ADMIN));
                                    }
                                }
                                if (user.has("id_file_profile_picture")) {
                                    LoginRegisterActivity.this.getConfig().setUserIdFileProfilePicture(LoginRegisterActivity.this, user.getInt("id_file_profile_picture"));
                                }
                            }
                        } else {
                            Toast.makeText(LoginRegisterActivity.this, LoginRegisterActivity.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(getClass().getName(), "Failed to convert Json", e);
                    }
                    mRequestLaunched = false;
                }
            }, parameters)).execute();
        } else {
            mRequestLaunched = false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("RegisterLoginActivity", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            googlePlusRegisterLogin(acct.getEmail(), HashUtils.sha1(acct.getId()));
        } else {
            // Signed out, show unauthenticated UI.
        }
    }


    /**
     * Check if this is the first request to allow the {@link android.Manifest.permission#GET_ACCOUNTS} permission.
     *
     * @return Returns true if this is the very first request to allow {@link android.Manifest.permission#GET_ACCOUNTS} permission, false otherwise
     */
    private boolean isFirstAccountPermissionRequest() {
        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_GET_ACCOUNT_PERMISSION, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_FIRST_ACCOUNT_PERMISSION_REQUEST, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_GET_ACCOUNT_PERMISSION &&
                grantResults.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mShouldResolve = true;
            mGoogleApiClient.connect();
        }

    }

}
