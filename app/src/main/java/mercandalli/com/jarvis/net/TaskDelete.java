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

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.listener.IPostExecuteListener;

/**
 * Global behavior : http Delete
 * 
 * @author Jonathan
 * 
 */
public class TaskDelete extends AsyncTask<Void, Void, String> {

	String url;
	IPostExecuteListener listener;
	File file;
	Application app;
	List<BasicNameValuePair> parameters;

	public TaskDelete(Application app, String url, IPostExecuteListener listener) {
		this.app = app;
		this.url = url;
		this.listener = listener;
	}
	
	public TaskDelete(Application app, String url, IPostExecuteListener listener, List<BasicNameValuePair> parameters) {
		this.app = app;
		this.url = url;
		this.listener = listener;
		this.parameters = parameters;
	}

	@Override
	protected String doInBackground(Void... urls) {
		try {
			
			List<NameValuePair> params = new LinkedList<NameValuePair>();
            String paramString = "";
			if(parameters!=null) {
				for(BasicNameValuePair b : parameters)
					params.add(b);	        
		        paramString = URLEncodedUtils.format(params, "utf-8");
		    	url += "?"+paramString;	
			}
            Log.d("TaskDelete", "url = "+url+" "+paramString);
			
			HttpDelete httpdelete = new HttpDelete(url);						

	    	StringBuilder authentication = new StringBuilder().append(app.getConfig().getUser().getAccessLogin()).append(":").append(app.getConfig().getUser().getAccessPassword());
	        String result = Base64.encodeBytes(authentication.toString().getBytes());
	        httpdelete.setHeader("Authorization", "Basic " + result);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpdelete);

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
	 * Delete http response to String
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
        Log.d("onPostExecute DELETE", "" + response);
		if (response == null)
			this.listener.execute(null, null);
		else {
			try {
				JSONObject json = new JSONObject(response);				
				this.listener.execute(json, response);				
				if(json.has("toast"))
					if(!json.getString("toast").equals(""))
						Toast.makeText(app, json.getString("toast"), Toast.LENGTH_SHORT).show();				
			} catch (JSONException e) {
				e.printStackTrace();				
				this.listener.execute(null, response);
			}
		}
	}
}
