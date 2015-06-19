/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelUser;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.fragment.InscriptionFragment;
import mercandalli.com.filespace.ui.fragment.LoginFragment;
import mercandalli.com.filespace.ui.view.PagerSlidingTabStrip;
import mercandalli.com.filespace.util.HashUtils;
import mercandalli.com.filespace.util.StringPair;
import mercandalli.com.filespace.util.StringUtils;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;

public class ActivityRegisterLogin extends Application {

    private final int NB_FRAGMENT = 2;
    private int INIT_FRAGMENT = 1;
    public Fragment listFragment[] = new Fragment[NB_FRAGMENT];
    private ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        RegisterLoginPagerAdapter mPagerAdapter = new RegisterLoginPagerAdapter(this.getFragmentManager(), this);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) this.findViewById(R.id.tabs);
        mViewPager = (ViewPager) this.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                ActivityRegisterLogin.this.invalidateOptionsMenu();
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mViewPager.setOffscreenPageLimit(this.NB_FRAGMENT - 1);

        if(this.getConfig().getUserUsername()==null || this.getConfig().getUserPassword()==null)
            this.INIT_FRAGMENT = 0;
        else if(this.getConfig().getUserUsername().equals("") || this.getConfig().getUserPassword().equals(""))
            this.INIT_FRAGMENT = 0;

        mViewPager.setCurrentItem(this.INIT_FRAGMENT);

        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(getResources().getColor(R.color.white));

        if(this.getConfig().isAutoConncetion() && this.getConfig().getUrlServer()!=null && this.getConfig().getUserUsername()!=null && this.getConfig().getUserPassword()!=null)
            if(!this.getConfig().getUserUsername().equals("") && !this.getConfig().getUserPassword().equals("") && this.getConfig().getUserId() != -1)
        	    connectionSucceed();

        (this.findViewById(R.id.signin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listFragment[getCurrentFragmentIndex()] != null) {
                    if(listFragment[getCurrentFragmentIndex()] instanceof InscriptionFragment) {
                        ((InscriptionFragment)listFragment[getCurrentFragmentIndex()]).inscription();
                    }
                    else if(listFragment[getCurrentFragmentIndex()] instanceof LoginFragment) {
                        ((LoginFragment)listFragment[getCurrentFragmentIndex()]).login();
                    }
                }
            }
        });

        SignInButton signInButton = (SignInButton) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGplus();
            }
        });

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle arg0) {
                        mSignInClicked = false;
                        //Toast.makeText(getActivity(), "User is connected!", Toast.LENGTH_LONG).show();

                        // Get user's information
                        getProfileInformation();
                    }

                    @Override
                    public void onConnectionSuspended(int arg0) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (!result.hasResolution()) {
                            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), ActivityRegisterLogin.this,
                                    0).show();
                            return;
                        }

                        if (!mIntentInProgress) {
                            // Store the ConnectionResult for later usage
                            mConnectionResult = result;

                            if (mSignInClicked) {
                                // The user has already clicked 'sign-in' so we attempt to
                                // resolve all
                                // errors until the user is signed in, or they cancel.
                                resolveSignInError();
                            }
                        }

                    }
                }).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}
	
	public void connectionSucceed() {
		Intent intent = new Intent(this, ActivityMain.class);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		this.finish();
	}

	@Override
	public void updateAdapters() {
		
	}

    @Override
    public View getFab() {
        return null;
    }

    @Override
	public void refreshAdapters() {
		
	}

    public int getCurrentFragmentIndex() {
        return mViewPager.getCurrentItem();
    }

    public class RegisterLoginPagerAdapter extends FragmentPagerAdapter {
        Application app;

        public RegisterLoginPagerAdapter(FragmentManager fm, Application app) {
            super(fm);
            this.app = app;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch(i) {
                case 0:		fragment = new InscriptionFragment();  	break;
                case 1:		fragment = new LoginFragment(); 	    break;
                default:	fragment = new InscriptionFragment();	break;
            }
            listFragment[i] = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            String title = "null";
            switch(i) {
                case 0:		title = "REGISTER";		break;
                case 1:		title = "LOGIN";		break;
            }
            return title;
        }
    }


    boolean requestLaunched = false;

    private void googlePlusRegisterLogin(String username, String password) {
        if (requestLaunched)
            return;
        requestLaunched = true;

        final ModelUser user = new ModelUser();
        user.username = username;
        user.password = password;

        if (StringUtils.isNullOrEmpty(this.getConfig().getUrlServer())) {
            requestLaunched = false;
            return;
        }

        // Login : POST /user
        List<StringPair> parameters = new ArrayList<>();
        parameters.add(new StringPair("google_plus", "true"));
        parameters.add(new StringPair("username", "" + user.username));
        parameters.add(new StringPair("password", "" + user.password));
        if(isInternetConnection(this))
            (new TaskPost(this, this.getConfig().getUrlServer() + this.getConfig().routeUser, new IPostExecuteListener() {
                @Override
                public void execute(JSONObject json, String body) {
                    try {
                        if (json != null) {
                            if (json.has("succeed"))
                                if (json.getBoolean("succeed")) {

                                    if (!StringUtils.isNullOrEmpty(user.username))
                                        ActivityRegisterLogin.this.getConfig().setUserUsername(user.username);

                                    if (!StringUtils.isNullOrEmpty(user.password))
                                        ActivityRegisterLogin.this.getConfig().setUserPassword(user.password);

                                    connectionSucceed();
                                }
                            if (json.has("user")) {
                                JSONObject user = json.getJSONObject("user");
                                if (user.has("id"))
                                    ActivityRegisterLogin.this.getConfig().setUserId(user.getInt("id"));
                                if (user.has("admin"))
                                    ActivityRegisterLogin.this.getConfig().setUserAdmin(user.getBoolean("admin"));
                                if (user.has("id_file_profile_picture"))
                                    ActivityRegisterLogin.this.getConfig().setUserIdFileProfilePicture(user.getInt("id_file_profile_picture"));
                            }
                        } else
                            Toast.makeText(ActivityRegisterLogin.this, ActivityRegisterLogin.this.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestLaunched = false;
                }
            }, parameters)).execute();
        else
            requestLaunched = false;
    }

    /****************************************
     *      Login via Google+
     ****************************************/

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private static final int RC_SIGN_IN = 0;

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;


    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult == null) {
            mIntentInProgress = false;
            mGoogleApiClient.connect();
            return;
        }
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(People.LoadPeopleResult loadPeopleResult) {

            }
        });

        Plus.PeopleApi.loadConnected(mGoogleApiClient);

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhotoUrl = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            // by default the profile url gives 50x50 px image only
            // we can replace the value with whatever dimension we want by
            // replacing sz=X
            personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl.length() - 2) + PROFILE_PIC_SIZE;

            googlePlusRegisterLogin(email, HashUtils.sha1(currentPerson.getId()));
        }
        else {
            Toast.makeText(this, getString(R.string.failed_google_plus), Toast.LENGTH_LONG).show();
        }
    }

}
