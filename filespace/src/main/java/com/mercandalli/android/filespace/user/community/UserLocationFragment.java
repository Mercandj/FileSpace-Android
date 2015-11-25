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
package com.mercandalli.android.filespace.user.community;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.listener.IListener;
import com.mercandalli.android.filespace.common.listener.ILocationListener;
import com.mercandalli.android.filespace.common.listener.IPostExecuteListener;
import com.mercandalli.android.filespace.user.ModelUser;
import com.mercandalli.android.filespace.user.ModelUserLocation;
import com.mercandalli.android.filespace.common.net.TaskGet;
import com.mercandalli.android.filespace.common.net.TaskPost;
import com.mercandalli.android.filespace.common.fragment.BackFragment;
import com.mercandalli.android.filespace.common.util.DialogUtils;
import com.mercandalli.android.filespace.common.util.GpsUtils;
import com.mercandalli.android.filespace.common.util.NetUtils;
import com.mercandalli.android.filespace.common.util.StringPair;


public class UserLocationFragment extends BackFragment {

    private View rootView;
    private MapView mapView;
    private TextView text;
    private ImageButton circle;

    private Location location;

    // Google Map
    private GoogleMap map;

    public static UserLocationFragment newInstance() {
        return new UserLocationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_admin_add_user_location, container, false);

        this.text = (TextView) rootView.findViewById(R.id.text);
        this.text.setText("Touch the circle to see user positions");

        this.circle = (ImageButton) rootView.findViewById(R.id.circle);

        if (map == null) {
            mapView = (MapView) rootView.findViewById(R.id.mapView);

            mapView.onCreate(savedInstanceState);
            mapView.onResume(); //without this, map showed but was empty

            if (mapView != null) {
                map = mapView.getMap();

                /*
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.setMyLocationEnabled(true);

                // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
                MapsInitializer.initialize(this.getActivity());

                // Updates the location and zoom of the MapView
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(44.14, 14.2), 10);
                map.animateCamera(cameraUpdate);
                */
            }

            addLocation(new ModelUserLocation(mActivity, mApplicationCallback, "Zero Zero", 0, 0, 0));
        }


        this.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.alert(mActivity, "Share your location", "If you share your location you will see all the positions of every users", getString(R.string.yes),
                        new IListener() {
                            @Override
                            public void execute() {
                                location = GpsUtils.getGpsLocation(mActivity, new ILocationListener() {
                                    @Override
                                    public void execute(Location location) {
                                        if (location != null) {
                                            double longitude = location.getLongitude(),
                                                    latitude = location.getLatitude();

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

                                    if (NetUtils.isInternetConnection(mActivity) && longitude != 0 && latitude != 0) {
                                        List<StringPair> parameters = new ArrayList<>();
                                        parameters.add(new StringPair("longitude", "" + longitude));
                                        parameters.add(new StringPair("latitude", "" + latitude));

                                        (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUserPut, new IPostExecuteListener() {
                                            @Override
                                            public void onPostExecute(JSONObject json, String body) {
                                                refreshMap();
                                            }
                                        }, parameters)).execute();
                                    }
                                }
                            }
                        }, getString(R.string.no), null
                );
            }
        });

        refreshMap();

        return this.rootView;
    }

    public void refreshMap() {
        List<StringPair> parameters = null;
        if (NetUtils.isInternetConnection(mActivity) && mApplicationCallback.isLogged())
            new TaskGet(
                    mActivity,
                    mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUser,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            List<ModelUserLocation> locations = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelUser modelUser = new ModelUser(mActivity, mApplicationCallback, array.getJSONObject(i));
                                            locations.add(modelUser.userLocation);
                                        }
                                    }
                                } else
                                    Toast.makeText(mActivity, mActivity.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            addLocations(locations);
                        }
                    },
                    parameters
            ).execute();
    }

    public void addLocations(List<ModelUserLocation> locations) {
        int nbLocation = 0;
        for (ModelUserLocation userLocation : locations) {
            if (addLocation(userLocation))
                nbLocation++;
        }
        text.setText(nbLocation + " user location" + ((nbLocation > 1) ? "s" : ""));
    }

    public boolean addLocation(ModelUserLocation userLocation) {
        if (map == null || userLocation == null)
            return false;
        if (userLocation.latitude == 0 && userLocation.longitude == 0)
            return false;
        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(userLocation.latitude, userLocation.longitude)).title(userLocation.title);
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        // adding marker
        map.addMarker(marker);
        return true;
    }

    @Override
    public void onPause() {
        if (mapView != null)
            mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mapView != null)
            mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }
}
