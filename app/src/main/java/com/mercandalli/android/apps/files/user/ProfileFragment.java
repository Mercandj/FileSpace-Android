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
import com.mercandalli.android.apps.files.common.util.TimeUtils;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.settings.AdapterModelSetting;
import com.mercandalli.android.apps.files.settings.ModelSetting;
import com.mercandalli.android.apps.files.common.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class ProfileFragment extends BackFragment {

    private View rootView;
    private ProgressBar circularProgressBar;
    private UserModel user;

    private TextView username;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ModelSetting> list = new ArrayList<>();

    private ImageView icon_back;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        this.circularProgressBar = (ProgressBar) this.rootView.findViewById(R.id.circularProgressBar);
        this.circularProgressBar.setVisibility(View.VISIBLE);

        icon_back = (ImageView) rootView.findViewById(R.id.icon_back);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        Bitmap icon_profile_online = mApplicationCallback.getConfig().getUserProfilePicture(getActivity());
        if (icon_profile_online != null) {
            icon_back.setImageBitmap(ImageUtils.setBlur(ImageUtils.setBrightness(icon_profile_online, -50), 15));
        }

        this.username = (TextView) this.rootView.findViewById(R.id.username);
        this.username.setText(StringUtils.capitalize(Config.getUserUsername()));

        refreshView();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Drawable drawable = icon_back.getDrawable();
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
                    Constants.URL_DOMAIN + Config.routeUser + "/" + Config.getUserId(),
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            if (!isAdded()) {
                                return;
                            }
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        user = new UserModel(getActivity(), mApplicationCallback, json.getJSONObject("result"));
                                        list.clear();
                                        list.add(new ModelSetting("Username", "" + user.username));
                                        list.add(new ModelSetting("Files size", FileUtils.humanReadableByteCount(user.size_files) + " / " + FileUtils.humanReadableByteCount(user.server_max_size_end_user)));
                                        list.add(new ModelSetting("Files count", "" + user.num_files));
                                        list.add(new ModelSetting("Creation date", "" + TimeUtils.getDate(user.date_creation)));
                                        list.add(new ModelSetting("Connection date", "" + TimeUtils.getDate(user.date_last_connection)));
                                        if (user.isAdmin()) {
                                            list.add(new ModelSetting("Admin", "" + user.isAdmin()));

                                            if (user.userLocation != null) {
                                                list.add(new ModelSetting("Longitude", "" + user.userLocation.longitude));
                                                list.add(new ModelSetting("Latitude", "" + user.userLocation.latitude));
                                                list.add(new ModelSetting("Altitude", "" + user.userLocation.altitude));
                                            }
                                        }

                                        Location location = GpsUtils.getGpsLocation(getContext(), new ILocationListener() {
                                            @Override
                                            public void execute(Location location) {
                                                if (location != null) {
                                                    double longitude = location.getLongitude(),
                                                            latitude = location.getLatitude();

                                                    list.add(new ModelSetting("Gps Longitude", "" + longitude));
                                                    list.add(new ModelSetting("Gps Latitude", "" + latitude));

                                                    if (NetUtils.isInternetConnection(getContext()) && longitude != 0 && latitude != 0) {
                                                        List<StringPair> parameters = new ArrayList<>();
                                                        parameters.add(new StringPair("longitude", "" + longitude));
                                                        parameters.add(new StringPair("latitude", "" + latitude));

                                                        (new TaskPost(getActivity(), mApplicationCallback, Constants.URL_DOMAIN + Config.routeUserPut, new IPostExecuteListener() {
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

                                            list.add(new ModelSetting("Gps Longitude", "" + longitude));
                                            list.add(new ModelSetting("Gps Latitude", "" + latitude));

                                            if (NetUtils.isInternetConnection(getContext()) && longitude != 0 && latitude != 0) {
                                                List<StringPair> parameters = new ArrayList<>();
                                                parameters.add(new StringPair("longitude", "" + longitude));
                                                parameters.add(new StringPair("latitude", "" + latitude));

                                                (new TaskPost(getActivity(), mApplicationCallback, Constants.URL_DOMAIN + Config.routeUserPut, new IPostExecuteListener() {
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
        this.circularProgressBar.setVisibility(View.GONE);

        if (recyclerView != null && list != null) {
            AdapterModelSetting adapter = new AdapterModelSetting(list);
            adapter.setOnItemClickListener(new AdapterModelSetting.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position < list.size()) {
                        switch (position) {
                        }
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }
}
