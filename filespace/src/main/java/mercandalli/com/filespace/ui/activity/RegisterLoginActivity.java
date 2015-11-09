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
package mercandalli.com.filespace.ui.activity;

import android.accounts.Account;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.config.Config;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelUser;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.dialog.ConfirmationDialog;
import mercandalli.com.filespace.ui.dialog.DialogCallback;
import mercandalli.com.filespace.ui.fragment.login.LoginFragment;
import mercandalli.com.filespace.ui.fragment.login.RegistrationFragment;
import mercandalli.com.filespace.ui.view.PagerSlidingTabStrip;
import mercandalli.com.filespace.util.HashUtils;
import mercandalli.com.filespace.util.NetUtils;
import mercandalli.com.filespace.util.StringPair;
import mercandalli.com.filespace.util.StringUtils;

import static android.Manifest.permission.GET_ACCOUNTS;

public class RegisterLoginActivity extends ApplicationActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DialogCallback {

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

    private final int NB_FRAGMENT = 2;
    private int INIT_FRAGMENT = 1;

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

        if (this.getConfig().getUserUsername() == null || this.getConfig().getUserPassword() == null) {
            this.INIT_FRAGMENT = 0;
        } else if (this.getConfig().getUserUsername().equals("") || this.getConfig().getUserPassword().equals("")) {
            this.INIT_FRAGMENT = 0;
        }

        mViewPager.setCurrentItem(this.INIT_FRAGMENT);

        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(ContextCompat.getColor(this, R.color.white));

        if (this.getConfig().isAutoConncetion() && this.getConfig().getUrlServer() != null && this.getConfig().getUserUsername() != null && this.getConfig().getUserPassword() != null)
            if (!this.getConfig().getUserUsername().equals("") && !this.getConfig().getUserPassword().equals("") && Config.getUserId() != -1)
                connectionSucceed();

        findViewById(R.id.activity_register_login_signin).setOnClickListener(this);
        findViewById(R.id.activity_register_login_gg_sign).setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
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
        RegisterLoginActivity.this.invalidateOptionsMenu();
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
            mShouldResolve = true;
            mGoogleApiClient.connect();
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
    public void onConnected(Bundle bundle) {
        //The user has successfully signed in with Google.
        mShouldResolve = false;

        //Retrieve profile information on the currently signed in user
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            onGoogleConnectionSucceeded();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Google+ Nothing here
    }

    /**
     * Code executed once the user has logged in with Google +.
     */
    private void onGoogleConnectionSucceeded() {
        GetGoogleIdTokenTask task = new GetGoogleIdTokenTask();
        task.execute();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
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
        if (mRequestLaunched)
            return;
        mRequestLaunched = true;

        final ModelUser user = new ModelUser();
        user.username = username;
        user.password = password;

        if (StringUtils.isNullOrEmpty(this.getConfig().getUrlServer())) {
            mRequestLaunched = false;
            return;
        }

        // Login : POST /user
        List<StringPair> parameters = new ArrayList<>();
        parameters.add(new StringPair("google_plus", "true"));
        parameters.add(new StringPair("username", "" + user.username));
        parameters.add(new StringPair("password", "" + user.password));
        if (NetUtils.isInternetConnection(this))
            (new TaskPost(this, this, this.getConfig().getUrlServer() + Config.routeUser, new IPostExecuteListener() {
                @Override
                public void onPostExecute(JSONObject json, String body) {
                    try {
                        if (json != null) {
                            if (json.has("succeed"))
                                if (json.getBoolean("succeed")) {

                                    if (!StringUtils.isNullOrEmpty(user.username))
                                        RegisterLoginActivity.this.getConfig().setUserUsername(user.username);

                                    if (!StringUtils.isNullOrEmpty(user.password))
                                        RegisterLoginActivity.this.getConfig().setUserPassword(user.password);

                                    connectionSucceed();
                                }
                            if (json.has("user")) {
                                JSONObject user = json.getJSONObject("user");
                                if (user.has("id"))
                                    RegisterLoginActivity.this.getConfig().setUserId(user.getInt("id"));
                                if (user.has("admin")) {
                                    Object admin_obj = user.get("admin");
                                    if (admin_obj instanceof Integer)
                                        RegisterLoginActivity.this.getConfig().setUserAdmin(user.getInt("admin") == 1);
                                    else if (admin_obj instanceof Boolean)
                                        RegisterLoginActivity.this.getConfig().setUserAdmin(user.getBoolean("admin"));
                                }
                                if (user.has("id_file_profile_picture"))
                                    RegisterLoginActivity.this.getConfig().setUserIdFileProfilePicture(user.getInt("id_file_profile_picture"));
                            }
                        } else
                            Toast.makeText(RegisterLoginActivity.this, RegisterLoginActivity.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mRequestLaunched = false;
                }
            }, parameters)).execute();
        else
            mRequestLaunched = false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {//Google
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    private class GetGoogleIdTokenTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... params) {

            Response response = new Response();

            //We retrieve the ID token using the defined account and the server client ID
            response.mAccountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            response.mId = currentPerson.getId();
            Account account = new Account(response.mAccountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            googlePlusRegisterLogin(response.mAccountName, HashUtils.sha1(response.mId));
        }

    }

    class Response {
        public String mAccountName, mId;
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
