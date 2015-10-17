/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.net;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.utils.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mercandalli.com.filespace.R;

/**
 * Global behavior : DDL file
 *
 * @author Jonathan
 */
public class TaskGetDownload extends AsyncTask<Void, Long, Void> {

    String url;
    String url_ouput;
    IListener listener;
    ApplicationActivity app;
    ModelFile modelFile;

    int id = 1;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public TaskGetDownload(ApplicationActivity app, String url, String url_ouput, ModelFile modelFile, IListener listener) {
        this.app = app;
        this.url = url;
        this.url_ouput = url_ouput;
        this.modelFile = modelFile;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mNotifyManager = (NotificationManager) this.app.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this.app);
        mBuilder.setContentTitle(this.modelFile.type.getTitle() + " Download")
                .setContentText("Download in progress : 0 / " + FileUtils.humanReadableByteCount(this.modelFile.size) + " : 0%")
                .setSmallIcon(R.drawable.ic_notification);
    }

    @Override
    protected Void doInBackground(Void... urls) {
        file_from_url_Authorization(this.url);
        return null;
    }

    @Override
    protected void onPostExecute(Void response) {
        Log.d("onPostExecute", "" + response);

        // When the loop is finished, updates the notification
        mBuilder.setContentText("Download complete")
                // Removes the progress bar
                .setProgress(0, 0, false);
        mNotifyManager.notify(id, mBuilder.build());

        this.listener.execute();
    }

    public void file_from_url_Authorization(String url) {
        try {
            StringBuilder authentication = new StringBuilder().append(app.getConfig().getUser().getAccessLogin()).append(":").append(app.getConfig().getUser().getAccessPassword());
            String result = Base64.encodeBytes(authentication.toString().getBytes());

            HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
            conn.setRequestProperty("Authorization", "Basic " + result);
            conn.setRequestMethod("GET");

            InputStream inputStream = conn.getInputStream();
            long lengthOfFile = Long.parseLong(conn.getHeaderField("Content-Length"));
            OutputStream outputStream = new FileOutputStream(url_ouput);

            byte data[] = new byte[1024];
            long total = 0;
            int missed_value = 50;
            int missed_counter = 0;

            int count;
            while ((count = inputStream.read(data)) != -1) {
                total += count;

                missed_counter++;
                if (missed_counter > missed_value) {
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(((total * 100) / lengthOfFile), total);

                    missed_counter = 0;
                }

                // writing data to file
                outputStream.write(data, 0, count);
            }

            // flushing output
            outputStream.flush();

            // closing streams
            outputStream.close();
            inputStream.close();

            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);

        long incr = 0;
        if (values.length > 0)
            incr = values[0];
        mBuilder.setProgress(100, (int) incr, false);
        mBuilder.setContentText("Download in progress " + incr + "%");
        if (values.length > 1)
            mBuilder.setContentText("Download in progress : " + FileUtils.humanReadableByteCount(values[1]) + " / " + FileUtils.humanReadableByteCount(this.modelFile.size) + " : " + incr + "%");

        mNotifyManager.notify(id, mBuilder.build());
    }
}
