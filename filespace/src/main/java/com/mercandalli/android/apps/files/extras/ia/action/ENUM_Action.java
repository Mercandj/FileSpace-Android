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
package com.mercandalli.android.apps.files.extras.ia.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.provider.Settings;

public enum ENUM_Action {

    BATTERIE(new Action() {
        @Override
        public String action(Context context, String input) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float) scale;
            batteryPct *= 100.0;

            return "Tu as " + batteryPct + "% de batterie.";
        }
    }
    ),
    WIFI(new Action() {
        @Override
        public String action(Context context, String input) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
                return "Le wifi est activ�.";
            else
                return "Le wifi n'est pas activ�.";
        }
    }
    ),
    WIFI_ON(new Action() {
        @Override
        public String action(Context context, String input) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
            return "Le wifi est activ�.";
        }
    }
    ),
    WIFI_OFF(new Action() {
        @Override
        public String action(Context context, String input) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(false);
            return "Le wifi est �teint.";
        }
    }
    ),
    MODE_AVION(new Action() {
        @Override
        public String action(Context context, String input) {
            if (Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1)
                return "Tu es en mode avion.";
            else
                return "Le mode avion n'est pas activ�.";
        }
    }
    ),
    LOCALISATION(new Action() {
        @Override
        public String action(Context context, String input) {
            LocationManager myLocationManager;
            String PROVIDER = LocationManager.NETWORK_PROVIDER;
            myLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            //get last known location, if available
            Location location = myLocationManager.getLastKnownLocation(PROVIDER);

            if (location != null)
                return "Votre longitude est : " + location.getLongitude() + ".\nVotre latitude est : " + location.getLatitude() + ".";
            else
                return "Pas de localisation.";
        }
    }
    ),
    VERSION_DROID(new Action() {
        @Override
        public String action(Context context, String input) {
            try {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
    ),
    QUIT(new Action() {
        @Override
        public String action(Context context, String input) {
            ((Activity) context).finish();
            return input;
        }
    }),
    WEB_SEARCH(new Action() {
        @Override
        public String action(Context context, String input) {
            if (input == null)
                return null;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(input));
            context.startActivity(browserIntent);
            return null;
        }
    });

    public Action action;

    private ENUM_Action(Action action) {
        this.action = action;
    }
}
