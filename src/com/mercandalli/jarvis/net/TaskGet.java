/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.listener.IPostExecuteListener;

/**
 * Global behavior : http Post
 * 
 * @author Jonathan
 * 
 */
public class TaskGet extends AsyncTask<Void, Void, String> {

	String url;
	IPostExecuteListener listener;
	File file;
	Application app;
	List<BasicNameValuePair> parameters;

	public TaskGet(Application app, String url, IPostExecuteListener listener) {
		this.app = app;
		this.url = url;
		this.listener = listener;
	}
	
	public TaskGet(Application app, String url, IPostExecuteListener listener, List<BasicNameValuePair> parameters) {
		this.app = app;
		this.url = url;
		this.listener = listener;
		this.parameters = parameters;
	}

	@Override
	protected String doInBackground(Void... urls) {
		try {
			
			List<NameValuePair> params = new LinkedList<NameValuePair>();
	        //params.add(new BasicNameValuePair("pseudo", String.valueOf(lib.user.getPseudo())));
			if(parameters!=null) {
				for(BasicNameValuePair b : parameters)
					params.add(b);	        
		        String paramString = URLEncodedUtils.format(params, "utf-8");        
		    	url += "?"+paramString;	
			}
			
			HttpGet httpget = new HttpGet(url);						

	    	StringBuilder authentication = new StringBuilder().append(app.config.getUser().getAccessLogin()).append(":").append(app.config.getUser().getAccessPassword());
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
		if (response == null)
			this.listener.execute(null, null);
		else {
			try {
				JSONObject json = new JSONObject(response);				
				this.listener.execute(json, response);				
				if(json.has("toast"))										
					Toast.makeText(app, json.getString("toast"), Toast.LENGTH_SHORT).show();				
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
				this.listener.execute(null, response);
			}
		}
	}
}
