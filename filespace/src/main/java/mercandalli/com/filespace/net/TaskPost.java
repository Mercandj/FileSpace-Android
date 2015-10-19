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

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.utils.StringPair;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import mercandalli.com.filespace.R;

/**
 * Global behavior : http Post
 *
 * @author Jonathan
 */
public class TaskPost extends AsyncTask<Void, Void, String> {

    String url, contentType;
    List<StringPair> parameters;
    IPostExecuteListener listener;
    File file;
    ApplicationActivity app;

    public TaskPost(ApplicationActivity app, String url, IPostExecuteListener listener) {
        this.app = app;
        this.url = url;
        this.listener = listener;
    }

    public TaskPost(ApplicationActivity app, String url, IPostExecuteListener listener, List<StringPair> parameters) {
        this.app = app;
        this.url = url;
        this.parameters = parameters;
        this.listener = listener;
    }

    public TaskPost(ApplicationActivity app, String url, IPostExecuteListener listener, List<StringPair> parameters, String contentType) {
        this.app = app;
        this.url = url;
        this.parameters = parameters;
        this.contentType = contentType;
        this.listener = listener;
    }

    public TaskPost(ApplicationActivity app, String url, IPostExecuteListener listener, List<StringPair> parameters, File file) {
        this.app = app;
        this.url = url;
        this.parameters = parameters;
        this.listener = listener;
        this.file = file;
    }

    public TaskPost(ApplicationActivity app, String url, IPostExecuteListener listener, File file) {
        this.app = app;
        this.url = url;
        this.listener = listener;
        this.file = file;
    }

    @Override
    protected String doInBackground(Void... urls) {
        try {

            // http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post

            HttpPost httppost = new HttpPost(url);

            MultipartEntity mpEntity = new MultipartEntity();
            if (this.file != null) mpEntity.addPart("file", new FileBody(file, "*/*"));

            String log_parameters = "";
            if (this.parameters != null)
                for (StringPair b : parameters) {
                    mpEntity.addPart(b.getName(), new StringBody(b.getValue(), Charset.forName("UTF-8")));
                    log_parameters += b.getName() + ":" + b.getValue() + " ";
                }
            Log.d("TaskPost", "url = " + url + " " + log_parameters);

            httppost.setEntity(mpEntity);

            StringBuilder authentication = new StringBuilder().append(app.getConfig().getUser().getAccessLogin()).append(":").append(app.getConfig().getUser().getAccessPassword());
            String result = Base64.encodeBytes(authentication.toString().getBytes());
            httppost.setHeader("Authorization", "Basic " + result);

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);

            // receive response as inputStream
            InputStream inputStream = response.getEntity().getContent();

            String resultString = null;

            // convert inputstream to string
            if (inputStream != null)
                resultString = convertInputStreamToString(inputStream);

            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode >= 300)
                resultString = "Status Code " + responseCode + ". " + resultString;
            return resultString;


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
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
        Log.d("onPostExecute POST", "" + response);
        if (response == null) {
            if (this.listener != null)
                this.listener.onPostExecute(null, null);
        } else {
            try {
                JSONObject json = new JSONObject(response);
                if (this.listener != null)
                    this.listener.onPostExecute(json, response);
                if (json.has("toast"))
                    if (!json.getString("toast").equals(""))
                        Toast.makeText(app, json.getString("toast"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                if (this.listener != null)
                    this.listener.onPostExecute(null, response);
            }
        }
    }
}
