/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.net;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.listener.IBitmapListener;

/**
 * Global behavior : DDL Image
 * 
 * @author Jonathan
 * 
 */
public class TaskDownloadImage extends AsyncTask<Void, Void, Void> {

	String url;
	List<BasicNameValuePair> parameters;
	Bitmap bitmap;
	IBitmapListener listener;
	File file;
	Application app;
	
	public TaskDownloadImage(Application app, String url, IBitmapListener listener) {
		this.app = app;
		this.url = url;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Void... urls) {		
		bitmap = drawable_from_url_Authorization(this.url);
		return null;		
	}

	@Override
	protected void onPostExecute(Void response) {
		Log.d("onPostExecute", "" + response);
		this.listener.execute(bitmap);
	}
	
	public Bitmap drawable_from_url_Authorization(String url) {
		Bitmap x = null;
		HttpResponse response;
		HttpPost httppost = new HttpPost(url.replaceAll(" ", "%20"));
    	StringBuilder authentication = new StringBuilder().append(app.config.getUser().getAccessLogin()).append(":").append(app.config.getUser().getAccessPassword());
        String result = Base64.encodeBytes(authentication.toString().getBytes());
        httppost.setHeader("Authorization", "Basic " + result);
		HttpClient httpclient = new DefaultHttpClient();
		try {
			response = httpclient.execute(httppost);
			InputStream inputStream = response.getEntity().getContent();
			x = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
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
