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
package com.mercandalli.android.filespace.common.net;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.main.Config;
import com.mercandalli.android.filespace.common.listener.IPostExecuteListener;
import com.mercandalli.android.filespace.main.ApplicationCallback;
import com.mercandalli.android.filespace.common.util.NetUtils;
import com.mercandalli.android.filespace.common.util.StringPair;
import com.mercandalli.android.filespace.common.util.StringUtils;

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
    ApplicationCallback mApplicationCallback;
    Activity mActivity;

    public TaskPost(Activity activity, ApplicationCallback app, String url, IPostExecuteListener listener) {
        mActivity = activity;
        this.mApplicationCallback = app;
        this.url = url;
        this.listener = listener;
    }

    public TaskPost(Activity activity, ApplicationCallback app, String url, IPostExecuteListener listener, List<StringPair> parameters) {
        mActivity = activity;
        this.mApplicationCallback = app;
        this.url = url;
        this.parameters = parameters;
        this.listener = listener;
    }

    public TaskPost(Activity activity, ApplicationCallback app, String url, IPostExecuteListener listener, List<StringPair> parameters, String contentType) {
        mActivity = activity;
        this.mApplicationCallback = app;
        this.url = url;
        this.parameters = parameters;
        this.contentType = contentType;
        this.listener = listener;
    }

    public TaskPost(Activity activity, ApplicationCallback app, String url, IPostExecuteListener listener, List<StringPair> parameters, File file) {
        mActivity = activity;
        this.mApplicationCallback = app;
        this.url = url;
        this.parameters = parameters;
        this.listener = listener;
        this.file = file;
    }

    @Override
    protected String doInBackground(Void... urls) {


        try {
            if (this.parameters != null) {
                if (!StringUtils.isNullOrEmpty(Config.getUserRegId()))
                    parameters.add(new StringPair("android_id", "" + Config.getUserRegId()));
                url = NetUtils.addUrlParameters(url, parameters);
            }

            Log.d("TaskGet", "url = " + url);

            URL tmp_url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) tmp_url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + Config.getUserToken());
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if (this.parameters != null) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(parameters));
                writer.flush();
                writer.close();
                os.close();
            }


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


//        try {
//
//            // http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post
//
//            HttpPost httppost = new HttpPost(url);
//
//            MultipartEntity mpEntity = new MultipartEntity();
//            if (this.file != null) mpEntity.addPart("file", new FileBody(file, "*/*"));
//
//            String log_parameters = "";
//            if (this.parameters != null)
//                for (StringPair b : parameters) {
//                    mpEntity.addPart(b.getName(), new StringBody(b.getValue(), Charset.forName("UTF-8")));
//                    log_parameters += b.getName() + ":" + b.getValue() + " ";
//                }
//            Log.d("TaskPost", "url = " + url + " " + log_parameters);
//
//            httppost.setEntity(mpEntity);
//
//            StringBuilder authentication = new StringBuilder().append(app.getConfig().getUser().getAccessLogin()).append(":").append(app.getConfig().getUser().getAccessPassword());
//            String result = Base64.encodeBytes(authentication.toString().getBytes());
//            httppost.setHeader("Authorization", "Basic " + result);
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpResponse response = httpclient.execute(httppost);
//
//            // receive response as inputStream
//            InputStream inputStream = response.getEntity().getContent();
//
//            String resultString = null;
//
//            // convert inputstream to string
//            if (inputStream != null)
//                resultString = convertInputStreamToString(inputStream);
//
//            int responseCode = response.getStatusLine().getStatusCode();
//            if (responseCode >= 300)
//                resultString = "Status Code " + responseCode + ". " + resultString;
//            return resultString;
//
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    private String getQuery(List<StringPair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (StringPair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
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
                        Toast.makeText(mActivity, json.getString("toast"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, mActivity.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                if (this.listener != null)
                    this.listener.onPostExecute(null, response);
            }
        }
    }
}
