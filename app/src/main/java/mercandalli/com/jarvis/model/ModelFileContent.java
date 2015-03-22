package mercandalli.com.jarvis.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jonathan on 22/03/2015.
 */
public class ModelFileContent {

    public String type;
    public Date date_creation, timer_date;

    public ModelFileContent(String content) {
        try {
            JSONObject json = new JSONObject(content);
            if(json.has("type") && !json.isNull("type"))
                this.type = json.getString("type");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                if(json.has("date_creation") && !json.isNull("date_creation"))
                    this.date_creation = dateFormat.parse(json.getString("date_creation"));
                if(json.has("timer_date") && !json.isNull("timer_date"))
                    this.timer_date = dateFormat.parse(json.getString("timer_date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.d("Content type",""+type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if(timer_date != null)
            return printDifference(timer_date, new Date());
        if(date_creation != null)
            return date_creation.toString();
        return "null";
    }

    public String printDifference(Date endDate, Date startDate){
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        if(different<0)
            return "Finished";

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return
                (elapsedDays!=0 ? (elapsedDays + "d ") : "") +
                (elapsedHours!=0 ? (elapsedHours + "h ") : "") +
                (elapsedMinutes!=0 ? (elapsedMinutes + "m ") : "") +
                (elapsedSeconds!=0 ? (elapsedSeconds + "s") : "") ;
    }
}
