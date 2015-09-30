package mercandalli.com.filespace.util;

import android.graphics.Bitmap;
import android.text.format.DateFormat;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import mercandalli.com.filespace.listener.IBitmapListener;
import mercandalli.com.filespace.listener.IModelNasaImageListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelNasaImage;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.net.TaskGetDownloadImage;
import mercandalli.com.filespace.ui.activity.Application;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;

/**
 * Created by Jonathan on 29/09/2015.
 */
public class NasaUtils {

    public static String getRandomDate() {
        long beginTime;
        long endTime;
        beginTime = Timestamp.valueOf("2015-05-01 00:00:00").getTime();
        endTime = Calendar.getInstance().getTimeInMillis();
        long diff = endTime - beginTime + 1;
        Date date = new Date(beginTime + (long) (Math.random() * diff));
        return DateFormat.format("yyyy-MM-dd", date).toString();
    }

    public static String getNasaPhoto(String date) {
        // https://api.nasa.gov/api.html#apod
        // https://api.nasa.gov/planetary/apod?concept_tags=True&api_key=DEMO_KEY
        String key = "AS0opJ2hMYHltKt8Mx3TP6l21AlJrDGMNUTyDwTW"; // "DEMO_KEY"
        return "https://api.nasa.gov/planetary/apod?concept_tags=True&api_key="+key+"&date="+date;
    }

    public static void getNasaRandomPicture(final Application app, final IModelNasaImageListener modelNasaImageListener) {
        if(isInternetConnection(app)) {
            final String date = getRandomDate();
            new TaskGet(
                    app,
                    app.getConfig().getUser(),
                    getNasaPhoto(date),
                    new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            if(json == null)
                                return;
                            final ModelNasaImage modelNasaImage = new ModelNasaImage(json, date);
                            if(modelNasaImage.media_type != null)
                                if(modelNasaImage.media_type.equals("image"))
                                    new TaskGetDownloadImage(
                                            app,
                                            modelNasaImage.url,
                                            2000000,
                                            new IBitmapListener() {
                                                @Override
                                                public void execute(Bitmap bitmap) {
                                                    modelNasaImage.bitmap = bitmap;
                                                    modelNasaImageListener.execute(modelNasaImage);
                                                }
                                            },
                                            false,
                                            false
                                    ).execute();
                        }
                    },
                    null,
                    false
            ).execute();
        }
    }

}
