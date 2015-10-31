package mercandalli.com.filespace.model;

import android.app.Activity;

import mercandalli.com.filespace.ui.activity.ApplicationCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Assima on 7/6/15.
 */
public class ModelUserLocation extends Model {

    public double longitude, latitude, altitude;
    public String title = "";

    public ModelUserLocation(Activity activity, ApplicationCallback app, JSONObject json) {
        super(activity, app);
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

    public ModelUserLocation(Activity activity, ApplicationCallback app, String title, double longitude, double latitude, double altitude) {
        super(activity, app);
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.title = title;
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
