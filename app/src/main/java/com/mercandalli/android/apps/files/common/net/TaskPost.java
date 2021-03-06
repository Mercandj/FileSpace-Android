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

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.library.base.java.StringUtils;

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

/**
 * Global behavior : http Post
 *
 * @author Jonathan
 */
public class TaskPost extends AsyncTask<Void, Void, String> {

    private String url, contentType;
    private List<StringPair> mParameters;
    private IPostExecuteListener mListener;
    private File file;
    private Activity mActivity;

    public TaskPost(Activity activity, String url, IPostExecuteListener listener) {
        mActivity = activity;
        this.url = url;
        this.mListener = listener;
    }

    public TaskPost(Activity activity, String url, IPostExecuteListener listener, List<StringPair> parameters) {
        mActivity = activity;
        this.url = url;
        this.mParameters = parameters;
        this.mListener = listener;
    }

    public TaskPost(Activity activity, String url, IPostExecuteListener listener, List<StringPair> parameters, String contentType) {
        mActivity = activity;
        this.url = url;
        this.mParameters = parameters;
        this.contentType = contentType;
        this.mListener = listener;
    }

    public TaskPost(Activity activity, String url, IPostExecuteListener listener, List<StringPair> parameters, File file) {
        mActivity = activity;
        this.url = url;
        this.mParameters = parameters;
        this.mListener = listener;
        this.file = file;
    }

    @Override
    protected String doInBackground(Void... urls) {


        try {
            if (this.mParameters != null) {
                if (!StringUtils.isNullOrEmpty(Config.getNotificationId())) {
                    mParameters.add(new StringPair("android_id", "" + Config.getNotificationId()));
                }
                url = NetUtils.addUrlParameters(url, mParameters);
            }

            Log.d("TaskGet", "url = " + url);

            final URL tmpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) tmpUrl.openConnection();
            conn.setReadTimeout(10_000);
            conn.setConnectTimeout(15_000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + Config.getUserToken());
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if (this.mParameters != null) {
                final OutputStream outputStream = conn.getOutputStream();
                final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(getQuery(mParameters));
                writer.flush();
                writer.close();
                outputStream.close();
            }

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
            Log.e(getClass().getName(), "Failed to convert Json", e);
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
        String result = "";
        boolean first = true;

        for (StringPair pair : params) {
            if (first) {
                first = false;
            } else {
                result += "&";
            }
            result += URLEncoder.encode(pair.getName(), "UTF-8") +
                    "=" + URLEncoder.encode(pair.getValue(), "UTF-8");
        }

        return result;
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
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();
        return result;
    }

    @Override
    protected void onPostExecute(String response) {
        Log.d("onPostExecute POST", "" + response);
        if (response == null) {
            if (this.mListener != null) {
                this.mListener.onPostExecute(null, null);
            }
        } else {
            try {
                JSONObject json = new JSONObject(response);
                if (this.mListener != null) {
                    this.mListener.onPostExecute(json, response);
                }
                if (json.has("toast") && !json.getString("toast").equals("")) {
                    Toast.makeText(mActivity, json.getString("toast"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e(getClass().getName(), "Failed to convert Json", e);
                Toast.makeText(mActivity, mActivity.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                if (this.mListener != null) {
                    this.mListener.onPostExecute(null, response);
                }
            }
        }
    }
}
