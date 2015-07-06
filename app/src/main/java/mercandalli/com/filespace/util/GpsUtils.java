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
package mercandalli.com.filespace.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Jonathan on 29/06/15.
 */
public class GpsUtils {

    public static Location getLocation(Context context) {
        LocationManager myLocationManager;
        String PROVIDER = LocationManager.NETWORK_PROVIDER;
        myLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //get last known location, if available
        return myLocationManager.getLastKnownLocation(PROVIDER);
    }

    public static double getLongitude(Context context) {
        Location location = getLocation(context);
        if(location != null)
            return location.getLongitude();
        return 0;
    }

    public static double getLatitude(Context context) {
        Location location = getLocation(context);
        if(location != null)
            return location.getLatitude();
        return 0;
    }

    public static double getAltitude(Context context) {
        Location location = getLocation(context);
        if(location != null)
            return location.getAltitude();
        return 0;
    }





    public static Location getGpsLocation(Context context) {
        LocationManager myLocationManager;
        String PROVIDER = LocationManager.GPS_PROVIDER;
        myLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //get last known location, if available
        return myLocationManager.getLastKnownLocation(PROVIDER);
    }

    public static double getGpsLongitude(Context context) {
        Location location = getGpsLocation(context);
        if(location != null)
            return location.getLongitude();
        return 0;
    }

    public static double getGpsLatitude(Context context) {
        Location location = getGpsLocation(context);
        if(location != null)
            return location.getLatitude();
        return 0;
    }

    public static double getGpsAltitude(Context context) {
        Location location = getGpsLocation(context);
        if(location != null)
            return location.getAltitude();
        return 0;
    }

}
