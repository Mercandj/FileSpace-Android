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
package com.mercandalli.android.apps.files.common.net;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.mercandalli.android.apps.files.common.listener.IBitmapListener;
import com.mercandalli.android.apps.files.common.listener.ILongListener;
import com.mercandalli.android.apps.files.common.util.ImageUtils;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.main.Config;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Global behavior : DDL Image
 *
 * @author Jonathan
 */
public class TaskGetDownloadImage extends AsyncTask<Void, Long, Void> {

    String url;
    Bitmap bitmap;
    IBitmapListener listener;
    Config.ConfigCallback app;
    Activity mActivity;
    int idFile;
    long sizeLimit, sizeFile;
    ILongListener progressListener;
    private boolean isAuthentication = true;
    private boolean isModelFile = true;

    public TaskGetDownloadImage(Activity activity, Config.ConfigCallback app, String url, long sizeLimit, IBitmapListener listener, boolean isAuthentication, boolean isModelFile) {
        mActivity = activity;
        this.app = app;
        this.url = url;
        this.listener = listener;
        this.sizeLimit = sizeLimit;
        this.isAuthentication = isAuthentication;
        this.isModelFile = isModelFile;
    }

    public TaskGetDownloadImage(Activity activity, Config.ConfigCallback app, FileModel fileModel, long sizeLimit, IBitmapListener listener) {
        mActivity = activity;
        this.app = app;
        this.url = fileModel.getOnlineUrl();
        this.idFile = fileModel.getId();
        this.sizeFile = fileModel.getSize();
        this.listener = listener;
        this.sizeLimit = sizeLimit;
    }

    public TaskGetDownloadImage(Activity activity, Config.ConfigCallback app, String onlineUrl, int idFile, long sizeFile, long sizeLimit, IBitmapListener listener) {
        mActivity = activity;
        this.app = app;
        this.url = onlineUrl;
        this.idFile = idFile;
        this.sizeFile = sizeFile;
        this.listener = listener;
        this.sizeLimit = sizeLimit;
    }

    public TaskGetDownloadImage(Activity activity, Config.ConfigCallback app, String onlineUrl, int idFile, long sizeFile, long sizeLimit, IBitmapListener listener, ILongListener progressListener) {
        mActivity = activity;
        this.app = app;
        this.url = onlineUrl;
        this.idFile = idFile;
        this.sizeFile = sizeFile;
        this.listener = listener;
        this.sizeLimit = sizeLimit;
        this.progressListener = progressListener;
    }

    @Override
    protected Void doInBackground(Void... urls) {
        bitmap = drawableFromUrlAuthorization();
        return null;
    }

    @Override
    protected void onPostExecute(Void response) {
        this.listener.execute(bitmap);
    }

    public Bitmap drawableFromUrlAuthorization() {
        Log.d("TaskGetDownloadImage", "id:" + idFile + "  url:" + url);

        if (isModelFile && ImageUtils.isImage(mActivity, this.idFile)) {
            return ImageUtils.loadImage(mActivity, this.idFile);
        }
        if (this.sizeLimit > 0 && this.sizeLimit < this.sizeFile) {
            return null;
        }

        Bitmap x = null;
        try {
            StringBuilder authentication = new StringBuilder().append(app.getConfig().getUser().getAccessLogin()).append(":").append(app.getConfig().getUser().getAccessPassword());
            String result = Base64.encodeBytes(authentication.toString().getBytes());

            HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
            if (isAuthentication) {
                conn.setRequestProperty("Authorization", "Basic " + result);
            }
            conn.setRequestMethod("GET");

            InputStream inputStream = conn.getInputStream();

            String contentLength = conn.getHeaderField("Content-Length");
            if (contentLength == null) {
                return x;
            }

            long lengthOfFile = Long.parseLong(contentLength);

            // Get the source image's dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inScaled = false;
            if (this.sizeFile > 3_000_000) {
                options.inSampleSize = 16;
            } else if (this.sizeFile > 2_000_000) {
                options.inSampleSize = 8;
            } else if (this.sizeFile > 500_000) {
                options.inSampleSize = 4;
            }
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            x = BitmapFactory.decodeStream(new FlushedInputStream(inputStream, lengthOfFile), null, options);

            conn.disconnect();

            if (isModelFile) {
                ImageUtils.saveImage(mActivity, this.idFile, x);
            }
        } catch (IOException e) {
            Log.e(getClass().getName(), "IOException", e);
        }
        return x;
    }

    /**
     * DDL image
     *
     * @author Jonathan
     */
    public class FlushedInputStream extends FilterInputStream {

        private long counter = 0, lenghtOfFile;

        public FlushedInputStream(InputStream inputStream, long lenghtOfFile) {
            super(inputStream);
            this.lenghtOfFile = lenghtOfFile;
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int bytet = read();
                    if (bytet < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }

        @Override
        public int read(byte[] buffer, int offset, int count) throws IOException {
            int result = super.read(buffer, offset, count);
            counter += result;
            if (progressListener != null) {
                publishProgress((long) (((counter * 1.0) / lenghtOfFile) * 100));
            }
            return result;
        }
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        if (progressListener != null && values.length > 0) {
            progressListener.execute(values[0]);
        }
    }
}
