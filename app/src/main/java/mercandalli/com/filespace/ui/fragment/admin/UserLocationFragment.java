/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.fragment.admin;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelUser;
import mercandalli.com.filespace.model.ModelUserLocation;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.fragment.Fragment;
import mercandalli.com.filespace.util.HashUtils;
import mercandalli.com.filespace.util.StringPair;
import mercandalli.com.filespace.util.StringUtils;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;


public class UserLocationFragment extends Fragment {

	Application app;
	private View rootView;
    private MapView mapView;

    // Google Map
    private GoogleMap map;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (Application) activity;
    }

    public UserLocationFragment() {
        super();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_admin_add_user_location, container, false);

        if (map == null && app != null) {
            mapView = (MapView) rootView.findViewById(R.id.mapView);

            mapView.onCreate(savedInstanceState);
            mapView.onResume(); //without this, map showed but was empty

            if(mapView != null) {
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

            addLocation(new ModelUserLocation(app, "Zero Zero", 0,0,0));
        }

        return this.rootView;
	}

    public void addLocations(List<ModelUserLocation> locations) {
        for(ModelUserLocation userLocation : locations) {
            addLocation(userLocation);
        }
    }

    public void addLocation(ModelUserLocation userLocation) {
        if(map == null)
            return;
        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(userLocation.latitude, userLocation.longitude)).title(userLocation.title);
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        // adding marker
        map.addMarker(marker);
    }

    @Override
    public void onPause() {
        if(mapView != null)
            mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        if(mapView != null)
            mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public boolean back() {
        return false;
    }
}
