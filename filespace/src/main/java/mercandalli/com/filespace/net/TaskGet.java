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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.models.ModelUser;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.utils.NetUtils;
import mercandalli.com.filespace.utils.StringPair;
import mercandalli.com.filespace.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
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
    File file;
    ModelUser user;
    List<StringPair> parameters;
    ApplicationActivity app;
    boolean isAuthentication = true;

    public TaskGet(ApplicationActivity app, ModelUser user, String url, IPostExecuteListener listener) {
        this.app = app;
        this.user = user;
        this.url = url;
        this.listener = listener;
    }

    public TaskGet(ApplicationActivity app, ModelUser user, String url, IPostExecuteListener listener, List<StringPair> parameters) {
        this.app = app;
        this.user = user;
        this.url = url;
        this.listener = listener;
        this.parameters = parameters;
    }

    public TaskGet(ApplicationActivity app, ModelUser user, String url, IPostExecuteListener listener, List<StringPair> parameters, boolean isAuthentication) {
        this.app = app;
        this.user = user;
        this.url = url;
        this.listener = listener;
        this.parameters = parameters;
        this.isAuthentication = isAuthentication;
    }

    @Override
    protected String doInBackground(Void... urls) {
        try {
            if (this.parameters != null) {
                if (!StringUtils.isNullOrEmpty(this.app.getConfig().getUserRegId()))
                    parameters.add(new StringPair("android_id", "" + this.app.getConfig().getUserRegId()));
                url = NetUtils.addUrlParameters(url, parameters);
            }

            Log.d("TaskGet", "url = " + url);

            URL tmp_url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) tmp_url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            String authentication = user.getAccessLogin() + ":" + user.getAccessPassword();
            String result = Base64.encodeBytes(authentication.getBytes());
            if (isAuthentication)
                conn.setRequestProperty("Authorization", "Basic " + result);
            conn.setUseCaches(false);
            conn.setDoInput(true);

            conn.connect(); // Starts the query
            int responseCode = conn.getResponseCode();
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());

            // convert inputstream to string
            String resultString = convertInputStreamToString(inputStream);

            //int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode >= 300)
                resultString = "Status Code " + responseCode + ". " + resultString;

            conn.disconnect();

            return resultString;
        } catch (IOException e) {
            e.printStackTrace();
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
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    @Override
    protected void onPostExecute(String response) {
        Log.d("onPostExecute", "" + response);
        if (response == null && this.listener != null) {
            this.listener.onPostExecute(null, null);
        } else {
            try {
                JSONObject json = new JSONObject(response);
                if (this.listener != null)
                    this.listener.onPostExecute(json, response);
                if (json.has("toast"))
                    if (!json.getString("toast").equals(""))
                        Toast.makeText(app, json.getString("toast"), Toast.LENGTH_SHORT).show();
                if (json.has("apk_update")) {
                    JSONArray array = json.getJSONArray("apk_update");
                    PackageManager packageManager = app.getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageInfo(app.getPackageName(), 0);
                    label:
                    for (int i = 0; i < array.length(); i++) {
                        ModelFile file = new ModelFile(app, array.getJSONObject(i));
                        if (packageInfo.lastUpdateTime < file.date_creation.getTime()) {
                            Toast.makeText(app, "You have an update.", Toast.LENGTH_SHORT).show();
                            break label;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (this.listener != null)
                    this.listener.onPostExecute(null, response);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
