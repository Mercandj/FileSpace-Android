package mercandalli.com.jarvis.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 22/03/2015.
 */
public class ModelFileContent {

    public String type;
    public Date date_creation, timer_date;
    private Application app;

    public ModelFileContent(Application app, String content) {
        this.app = app;
        try {
            JSONObject json = new JSONObject(content);
            if(json.has("type") && !json.isNull("type"))
                this.type = json.getString("type");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if(json.has("date_creation") && !json.isNull("date_creation"))
                    this.date_creation = dateFormat.parse(json.getString("date_creation"));
                if(json.has("timer_date") && !json.isNull("timer_date"))
                    this.timer_date = dateFormat.parse(json.getString("timer_date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            Log.e("model ModelFileContent", "JSONException");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if(timer_date != null) {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return this.app.getLibrary().printDifferenceFuture(timer_date, dateFormatLocal.parse(dateFormatGmt.format(new Date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(date_creation != null)
            return date_creation.toString();
        return "null";
    }
}
