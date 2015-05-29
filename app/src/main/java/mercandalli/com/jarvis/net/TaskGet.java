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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.ui.activity.Application;

/**
 * Global behavior : http Get
 * 
 * @author Jonathan
 * 
 */
public class TaskGet extends AsyncTask<Void, Void, String> {

	String url;
	IPostExecuteListener listener;
	File file;
	ModelUser user;
	List<BasicNameValuePair> parameters;
	Application app;

	public TaskGet(Application app, ModelUser user, String url, IPostExecuteListener listener) {
		this.app = app;
		this.user = user;
		this.url = url;
		this.listener = listener;
	}
	
	public TaskGet(Application app, ModelUser user, String url, IPostExecuteListener listener, List<BasicNameValuePair> parameters) {
		this.app = app;
		this.user = user;
		this.url = url;
		this.listener = listener;
		this.parameters = parameters;
	}

	@Override
	protected String doInBackground(Void... urls) {
		try {

			if(parameters!=null) {
                parameters.add(new BasicNameValuePair("android_id", ""+this.app.getConfig().getUserRegId()));
                url += url.endsWith("?") ? URLEncodedUtils.format(parameters, "utf-8") : "?" + URLEncodedUtils.format(parameters, "utf-8");
            }

			Log.d("TaskGet", "url = "+url);
			
			HttpGet httpget = new HttpGet(url);
			
	    	StringBuilder authentication = new StringBuilder().append(user.getAccessLogin()).append(":").append(user.getAccessPassword());
	        String result = Base64.encodeBytes(authentication.toString().getBytes());
	        httpget.setHeader("Authorization", "Basic " + result);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpget);

			// receive response as inputStream
			InputStream inputStream = response.getEntity().getContent();

			
			String resultString = null;
			
			// convert inputstream to string
			if (inputStream != null)
				resultString = convertInputStreamToString(inputStream);			
			
			int responseCode = response.getStatusLine().getStatusCode();
			if(responseCode>=300)
				resultString = "Status Code "+responseCode+". "+resultString;
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
		Log.d("onPostExecute", "" + response);
		if (response == null) {
            if (this.listener != null)
                this.listener.execute(null, null);
        }
		else {
			try {
				JSONObject json = new JSONObject(response);
                if (this.listener != null)
				    this.listener.execute(json, response);
				if(json.has("toast"))
					if(!json.getString("toast").equals(""))
						Toast.makeText(app, json.getString("toast"), Toast.LENGTH_SHORT).show();
				if(json.has("apk_update")) {
					JSONArray array = json.getJSONArray("apk_update");
                    PackageManager packageManager=app.getPackageManager();
                    PackageInfo packageInfo=packageManager.getPackageInfo(app.getPackageName(), 0);
                    label:for(int i=0; i<array.length(); i++) {
                        ModelFile file = new ModelFile(app, array.getJSONObject(i));
                        if(packageInfo.lastUpdateTime < file.date_creation.getTime()) {
                            Toast.makeText(app, "You have an update.", Toast.LENGTH_SHORT).show();
                            break label;
                        }
                    }
				}
			} catch (JSONException e) {
				e.printStackTrace();
                if (this.listener != null)
				    this.listener.execute(null, response);
			} catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
	}
}
