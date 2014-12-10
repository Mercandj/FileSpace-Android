/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import mercandalli.com.jarvis.Application;
import mercandalli.com.jarvis.listener.IBitmapListener;
import mercandalli.com.jarvis.model.ModelUser;

/**
 * Global behavior : DDL Image
 * 
 * @author Jonathan
 * 
 */
public class TaskGetDownloadImage extends AsyncTask<Void, Void, Void> {

	String url;
	Bitmap bitmap;
	IBitmapListener listener;
	Application app;
	ModelUser user;
	
	public TaskGetDownloadImage(Application app, ModelUser user, String url, IBitmapListener listener) {
		this.app = app;
		this.user = user;
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
		this.listener.execute(bitmap);
	}
	
	public Bitmap drawable_from_url_Authorization(String url) {
		Bitmap x = null;
		HttpResponse response;
		HttpGet httpget = new HttpGet(url);
    	StringBuilder authentication = new StringBuilder().append(user.getAccessLogin()).append(":").append(user.getAccessPassword());
        String result = Base64.encodeBytes(authentication.toString().getBytes());
        httpget.setHeader("Authorization", "Basic " + result);
		HttpClient httpclient = new DefaultHttpClient();
		try {
			response = httpclient.execute(httpget);
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
