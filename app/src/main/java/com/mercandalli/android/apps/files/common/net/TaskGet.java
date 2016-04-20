/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p>
 * LICENSE:
 * <p>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.common.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.library.baselibrary.java.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Global behavior : http Get
 *
 * @author Jonathan
 */
public class TaskGet extends AsyncTask<Void, Void, String> {

    String url;
    IPostExecuteListener listener;
    List<StringPair> parameters;
    Context mContext;
    boolean isAuthentication = true;

    public TaskGet(Context context, String url, IPostExecuteListener listener) {
        this(context, url, listener, null, true);
    }

    public TaskGet(Context context, String url, IPostExecuteListener listener, List<StringPair> parameters) {
        this(context, url, listener, parameters, true);
    }

    public TaskGet(Context context, String url, IPostExecuteListener listener, List<StringPair> parameters, boolean isAuthentication) {
        mContext = context;
        this.url = url;
        this.listener = listener;
        this.parameters = parameters;
        this.isAuthentication = isAuthentication;
    }

    @Override
    protected String doInBackground(Void... urls) {
        try {
            if (this.parameters != null) {
                if (!StringUtils.isNullOrEmpty(Config.getNotificationId())) {
                    parameters.add(new StringPair("android_id", "" + Config.getNotificationId()));
                }
                url = NetUtils.addUrlParameters(url, parameters);
            }

            Log.d("TaskGet", "url = " + url);

            URL tmp_url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) tmp_url.openConnection();
            conn.setReadTimeout(10_000);
            conn.setConnectTimeout(15_000);
            conn.setRequestMethod("GET");
            if (isAuthentication) {
                conn.setRequestProperty("Authorization", "Basic " + Config.getUserToken());
            }
            conn.setUseCaches(false);
            conn.setDoInput(true);

            conn.connect(); // Starts the query
            int responseCode = conn.getResponseCode();
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());

            // convert inputstream to string
            String resultString = convertInputStreamToString(inputStream);

            //int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode >= 300) {
                resultString = "Status Code " + responseCode + ". " + resultString;
            }

            conn.disconnect();

            return resultString;
        } catch (IOException e) {
            Log.e(getClass().getName(), "IOException", e);
        }
        return null;
    }

    /**
     * Get http response to String
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String convertInputStreamToString(final InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        final StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        inputStream.close();
        return result.toString();
    }

    @Override
    protected void onPostExecute(String response) {
        Log.d("onPostExecute", "" + response);
        if (response == null && this.listener != null) {
            this.listener.onPostExecute(null, null);
        } else {
            try {
                JSONObject json = new JSONObject(response);
                if (this.listener != null) {
                    this.listener.onPostExecute(json, response);
                }
                if (json.has("toast") && !json.getString("toast").equals("")) {
                    Toast.makeText(mContext, json.getString("toast"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e(getClass().getName(), "Failed to convert Json", e);
                if (this.listener != null) {
                    this.listener.onPostExecute(null, response);
                }
            }
        }
    }
}
