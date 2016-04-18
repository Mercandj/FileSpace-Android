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
package com.mercandalli.android.apps.files.user.community;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.ILocationListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskGet;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.common.util.GpsUtils;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.user.UserLocationModel;
import com.mercandalli.android.apps.files.user.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


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

            addLocation(new UserLocationModel("Zero Zero", 0, 0, 0));
        }


        this.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.alert(getContext(), "Share your location", "If you share your location you will see all the positions of every users", getString(android.R.string.yes),
                        new IListener() {
                            @Override
                            public void execute() {
                                location = GpsUtils.getGpsLocation(getContext(), new ILocationListener() {
                                    @Override
                                    public void execute(Location location) {
                                        if (location != null) {
                                            double longitude = location.getLongitude(),
                                                    latitude = location.getLatitude();

                                            if (NetUtils.isInternetConnection(getContext()) && longitude != 0 && latitude != 0) {
                                                List<StringPair> parameters = new ArrayList<>();
                                                parameters.add(new StringPair("longitude", "" + longitude));
                                                parameters.add(new StringPair("latitude", "" + latitude));

                                                (new TaskPost(getActivity(), Constants.URL_DOMAIN + Config.ROUTE_USER_PUT, new IPostExecuteListener() {
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

                                    if (NetUtils.isInternetConnection(getContext()) && longitude != 0 && latitude != 0) {
                                        List<StringPair> parameters = new ArrayList<>();
                                        parameters.add(new StringPair("longitude", "" + longitude));
                                        parameters.add(new StringPair("latitude", "" + latitude));

                                        (new TaskPost(getActivity(), Constants.URL_DOMAIN + Config.ROUTE_USER_PUT, new IPostExecuteListener() {
                                            @Override
                                            public void onPostExecute(JSONObject json, String body) {
                                                refreshMap();
                                            }
                                        }, parameters)).execute();
                                    }
                                }
                            }
                        }, getString(android.R.string.no), null
                );
            }
        });

        refreshMap();

        return this.rootView;
    }

    public void refreshMap() {
        if (NetUtils.isInternetConnection(getContext()) && Config.isLogged()) {
            new TaskGet(
                    getContext(),
                    Constants.URL_DOMAIN + Config.ROUTE_USER,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            List<UserLocationModel> locations = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            UserModel userModel = new UserModel(getActivity(), array.getJSONObject(i));
                                            locations.add(userModel.userLocation);
                                        }
                                    }
                                } else {
                                    Toast.makeText(getContext(), R.string.action_failed, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e(getClass().getName(), "Failed to convert Json", e);
                            }
                            addLocations(locations);
                        }
                    },
                    null
            ).execute();
        }
    }

    public void addLocations(List<UserLocationModel> locations) {
        int nbLocation = 0;
        for (UserLocationModel userLocation : locations) {
            if (addLocation(userLocation)) {
                nbLocation++;
            }
        }
        text.setText(nbLocation + " user location" + ((nbLocation > 1) ? "s" : ""));
    }

    public boolean addLocation(UserLocationModel userLocation) {
        if (map == null || userLocation == null) {
            return false;
        }
        if (userLocation.latitude == 0 && userLocation.longitude == 0) {
            return false;
        }
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
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public boolean back() {
        return false;
    }
}
