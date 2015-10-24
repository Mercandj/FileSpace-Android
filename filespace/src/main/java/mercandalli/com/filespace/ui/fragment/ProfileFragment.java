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
package mercandalli.com.filespace.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.ILocationListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelSetting;
import mercandalli.com.filespace.model.ModelUser;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.adapter.AdapterModelSetting;
import mercandalli.com.filespace.util.FileUtils;
import mercandalli.com.filespace.util.FontUtils;
import mercandalli.com.filespace.util.GpsUtils;
import mercandalli.com.filespace.util.ImageUtils;
import mercandalli.com.filespace.util.NetUtils;
import mercandalli.com.filespace.util.StringPair;
import mercandalli.com.filespace.util.StringUtils;
import mercandalli.com.filespace.util.TimeUtils;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class ProfileFragment extends BackFragment {

    private View rootView;
    private ProgressBar circularProgressBar;
    private ModelUser user;

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

        Bitmap icon_profile_online = mApplicationCallback.getConfig().getUserProfilePicture();
        if (icon_profile_online != null) {
            icon_back.setImageBitmap(ImageUtils.setBlur(ImageUtils.setBrightness(icon_profile_online, -50), 15));
        }

        this.username = (TextView) this.rootView.findViewById(R.id.username);
        this.username.setText(StringUtils.capitalize(mApplicationCallback.getConfig().getUserUsername()));
        FontUtils.applyFont(mActivity, this.username, "fonts/Roboto-Regular.ttf");

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
        if (NetUtils.isInternetConnection(mActivity) && mApplicationCallback.isLogged()) {
            List<StringPair> parameters = null;
            new TaskGet(
                    mActivity,
                    mApplicationCallback,
                    mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUser + "/" + mApplicationCallback.getConfig().getUserId(),
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            if (!isAdded())
                                return;
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        user = new ModelUser(mActivity, mApplicationCallback, json.getJSONObject("result"));
                                        list.clear();
                                        list.add(new ModelSetting(mActivity, mApplicationCallback, "Username", "" + user.username));
                                        list.add(new ModelSetting(mActivity, mApplicationCallback, "Files size", FileUtils.humanReadableByteCount(user.size_files) + " / " + FileUtils.humanReadableByteCount(user.server_max_size_end_user)));
                                        list.add(new ModelSetting(mActivity, mApplicationCallback, "Files count", "" + user.num_files));
                                        list.add(new ModelSetting(mActivity, mApplicationCallback, "Creation date", "" + TimeUtils.getDate(user.date_creation)));
                                        list.add(new ModelSetting(mActivity, mApplicationCallback, "Connection date", "" + TimeUtils.getDate(user.date_last_connection)));
                                        if (user.isAdmin()) {
                                            list.add(new ModelSetting(mActivity, mApplicationCallback, "Admin", "" + user.isAdmin()));

                                            if (user.userLocation != null) {
                                                list.add(new ModelSetting(mActivity, mApplicationCallback, "Longitude", "" + user.userLocation.longitude));
                                                list.add(new ModelSetting(mActivity, mApplicationCallback, "Latitude", "" + user.userLocation.latitude));
                                                list.add(new ModelSetting(mActivity, mApplicationCallback, "Altitude", "" + user.userLocation.altitude));
                                            }
                                        }

                                        Location location = GpsUtils.getGpsLocation(mActivity, new ILocationListener() {
                                            @Override
                                            public void execute(Location location) {
                                                if (location != null) {
                                                    double longitude = location.getLongitude(),
                                                            latitude = location.getLatitude();

                                                    list.add(new ModelSetting(mActivity, mApplicationCallback, "Gps Longitude", "" + longitude));
                                                    list.add(new ModelSetting(mActivity, mApplicationCallback, "Gps Latitude", "" + latitude));

                                                    if (NetUtils.isInternetConnection(mActivity) && longitude != 0 && latitude != 0) {
                                                        List<StringPair> parameters = new ArrayList<>();
                                                        parameters.add(new StringPair("longitude", "" + longitude));
                                                        parameters.add(new StringPair("latitude", "" + latitude));

                                                        (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUserPut, new IPostExecuteListener() {
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

                                            list.add(new ModelSetting(mActivity, mApplicationCallback, "Gps Longitude", "" + longitude));
                                            list.add(new ModelSetting(mActivity, mApplicationCallback, "Gps Latitude", "" + latitude));

                                            if (NetUtils.isInternetConnection(mActivity) && longitude != 0 && latitude != 0) {
                                                List<StringPair> parameters = new ArrayList<>();
                                                parameters.add(new StringPair("longitude", "" + longitude));
                                                parameters.add(new StringPair("latitude", "" + latitude));

                                                (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUserPut, new IPostExecuteListener() {
                                                    @Override
                                                    public void onPostExecute(JSONObject json, String body) {

                                                    }
                                                }, parameters)).execute();
                                            }
                                        }
                                    }
                                } else
                                    Toast.makeText(mActivity, mActivity.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
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
            AdapterModelSetting adapter = new AdapterModelSetting(mActivity, mApplicationCallback, list);
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
