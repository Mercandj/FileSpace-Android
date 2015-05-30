/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.jarvis.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import mercandalli.com.jarvis.listener.IBitmapListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.ui.activity.Application;

import static mercandalli.com.jarvis.util.ImageUtils.is_image;
import static mercandalli.com.jarvis.util.ImageUtils.load_image;
import static mercandalli.com.jarvis.util.ImageUtils.save_image;

/**
 * Global behavior : DDL Image
 *
 * @author Jonathan
 *
 */
public class TaskGetDownloadImage extends AsyncTask<Void, Void, Void> {

    String url;
    Bitmap bitmap;
    IBitmapListener listener;
    Application app;
    private String login, password;
    int idFile;
    long sizeLimit, sizeFile;

    public TaskGetDownloadImage(Application app, ModelUser user, ModelFile fileModel, long sizeLimit, IBitmapListener listener) {
        this.app = app;
        this.login = user.getAccessLogin();
        this.password = user.getAccessPassword();
        this.url = fileModel.onlineUrl;
        this.idFile = fileModel.id;
        this.sizeFile = fileModel.size;
        this.listener = listener;
        this.sizeLimit = sizeLimit;
    }

    public TaskGetDownloadImage(Application app, String login, String password, String onlineUrl, int idFile, long sizeFile, long sizeLimit, IBitmapListener listener) {
        this.app = app;
        this.login = login;
        this.password = password;
        this.url = onlineUrl;
        this.idFile = idFile;
        this.sizeFile = sizeFile;
        this.listener = listener;
        this.sizeLimit = sizeLimit;
    }

    @Override
    protected Void doInBackground(Void... urls) {
        bitmap = drawable_from_url_Authorization();
        return null;
    }

    @Override
    protected void onPostExecute(Void response) {
        this.listener.execute(bitmap);
    }

    public Bitmap drawable_from_url_Authorization() {
        if(is_image(this.app, this.idFile))
            return load_image(this.app, this.idFile);
        if(this.sizeLimit < this.sizeFile)
            return null;
        Bitmap x = null;
        HttpResponse response;
        HttpGet httpget = new HttpGet(url);
        StringBuilder authentication = new StringBuilder().append(this.login).append(":").append(this.password);
        String result = Base64.encodeBytes(authentication.toString().getBytes());
        httpget.setHeader("Authorization", "Basic " + result);
        HttpClient httpclient = new DefaultHttpClient();
        try {
            response = httpclient.execute(httpget);
            InputStream inputStream = response.getEntity().getContent();
            x = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
            save_image(this.app, this.idFile, x);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return x;
    }

    /**
     * DDL image
     * @author Jonathan
     *
     */
    public class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int bytet = read();
                    if (bytet < 0)
                        break;  // we reached EOF
                    else
                        bytesSkipped = 1; // we read one byte
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }







}
