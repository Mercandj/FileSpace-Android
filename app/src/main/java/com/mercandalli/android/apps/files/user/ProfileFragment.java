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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.ILocationListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskGet;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.GpsUtils;
import com.mercandalli.android.apps.files.common.util.ImageUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.util.StringUtils;
import com.mercandalli.android.apps.files.common.util.TimeUtils;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.settings.AdapterModelSetting;
import com.mercandalli.android.apps.files.settings.ModelSetting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class ProfileFragment extends BackFragment {

    private ProgressBar mProgressBar;
    private UserModel mUserModel;

    private TextView mUsernameTextView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private final List<ModelSetting> mModelSettings = new ArrayList<>();

    private ImageView mIconBack;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mIconBack = (ImageView) rootView.findViewById(R.id.icon_back);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Bitmap iconProfileOnline = mApplicationCallback.getConfig()
                .getUserProfilePicture(activity, mApplicationCallback);
        if (iconProfileOnline != null) {
            mIconBack.setImageBitmap(ImageUtils.setBlur(ImageUtils.setBrightness(iconProfileOnline, -50), 15));
        }

        this.mUsernameTextView = (TextView) rootView.findViewById(R.id.username);
        this.mUsernameTextView.setText(StringUtils.capitalize(Config.getUserUsername()));

        refreshView();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Drawable drawable = mIconBack.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmapDrawable.getBitmap().recycle();
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }

    public void refreshView() {
        if (NetUtils.isInternetConnection(getContext()) && mApplicationCallback.isLogged()) {
            List<StringPair> parameters = null;
            new TaskGet(
                    getActivity(),
                    Constants.URL_DOMAIN + Config.ROUTE_USER + "/" + Config.getUserId(),
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            if (!isAdded()) {
                                return;
                            }
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        mUserModel = new UserModel(getActivity(), mApplicationCallback, json.getJSONObject("result"));
                                        mModelSettings.clear();
                                        mModelSettings.add(new ModelSetting("Username", "" + mUserModel.username));
                                        mModelSettings.add(new ModelSetting("Files size", FileUtils.humanReadableByteCount(mUserModel.size_files) + " / " + FileUtils.humanReadableByteCount(mUserModel.server_max_size_end_user)));
                                        mModelSettings.add(new ModelSetting("Files count", "" + mUserModel.num_files));
                                        mModelSettings.add(new ModelSetting("Creation date", "" + TimeUtils.getDate(mUserModel.date_creation)));
                                        mModelSettings.add(new ModelSetting("Connection date", "" + TimeUtils.getDate(mUserModel.date_last_connection)));
                                        if (mUserModel.isAdmin()) {
                                            mModelSettings.add(new ModelSetting("Admin", "" + mUserModel.isAdmin()));

                                            if (mUserModel.userLocation != null) {
                                                mModelSettings.add(new ModelSetting("Longitude", "" + mUserModel.userLocation.longitude));
                                                mModelSettings.add(new ModelSetting("Latitude", "" + mUserModel.userLocation.latitude));
                                                mModelSettings.add(new ModelSetting("Altitude", "" + mUserModel.userLocation.altitude));
                                            }
                                        }

                                        Location location = GpsUtils.getGpsLocation(getContext(), new ILocationListener() {
                                            @Override
                                            public void execute(Location location) {
                                                if (location != null) {
                                                    double longitude = location.getLongitude(),
                                                            latitude = location.getLatitude();

                                                    mModelSettings.add(new ModelSetting("Gps Longitude", "" + longitude));
                                                    mModelSettings.add(new ModelSetting("Gps Latitude", "" + latitude));

                                                    if (NetUtils.isInternetConnection(getContext()) && longitude != 0 && latitude != 0) {
                                                        List<StringPair> parameters = new ArrayList<>();
                                                        parameters.add(new StringPair("longitude", "" + longitude));
                                                        parameters.add(new StringPair("latitude", "" + latitude));

                                                        (new TaskPost(getActivity(), mApplicationCallback, Constants.URL_DOMAIN + Config.ROUTE_USER_PUT, new IPostExecuteListener() {
                                                            @Override
                                                            public void onPostExecute(JSONObject json, String body) {

                                                            }
                                                        }, parameters)).execute();
                                                    }
                                                }
                                            }
                                        });

                                        if (location != null) {
                                            double longitude = location.getLongitude(),
                                                    latitude = location.getLatitude();

                                            mModelSettings.add(new ModelSetting("Gps Longitude", "" + longitude));
                                            mModelSettings.add(new ModelSetting("Gps Latitude", "" + latitude));

                                            if (NetUtils.isInternetConnection(getContext()) && longitude != 0 && latitude != 0) {
                                                List<StringPair> parameters = new ArrayList<>();
                                                parameters.add(new StringPair("longitude", "" + longitude));
                                                parameters.add(new StringPair("latitude", "" + latitude));

                                                (new TaskPost(getActivity(), mApplicationCallback, Constants.URL_DOMAIN + Config.ROUTE_USER_PUT, new IPostExecuteListener() {
                                                    @Override
                                                    public void onPostExecute(JSONObject json, String body) {

                                                    }
                                                }, parameters)).execute();
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(getContext(), R.string.action_failed, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e(getClass().getName(), "Failed to convert Json", e);
                            }
                            updateView();
                        }
                    },
                    parameters
            ).execute();
        }
    }

    public void updateView() {
        this.mProgressBar.setVisibility(View.GONE);

        if (mRecyclerView != null && mModelSettings != null) {
            AdapterModelSetting adapter = new AdapterModelSetting(mModelSettings);
            adapter.setOnItemClickListener(new AdapterModelSetting.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position < mModelSettings.size()) {
                        switch (position) {
                        }
                    }
                }
            });
            mRecyclerView.setAdapter(adapter);
        }
    }
}
