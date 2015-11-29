package com.mercandalli.android.filespace.user;

import org.json.JSONException;
import org.json.JSONObject;

public class UserLocationModel {

    public double longitude, latitude, altitude;
    public String title = "";

    public UserLocationModel(JSONObject json) {
        try {
            if (json.has("username"))
                this.title = json.getString("username");

            if (json.has("longitude") && !json.isNull("longitude"))
                this.longitude = json.getDouble("longitude");
            if (json.has("latitude") && !json.isNull("latitude"))
                this.latitude = json.getDouble("latitude");
            if (json.has("altitude") && !json.isNull("altitude"))
                this.altitude = json.getDouble("altitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserLocationModel(String title, double longitude, double latitude, double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.title = title;
    }
}
