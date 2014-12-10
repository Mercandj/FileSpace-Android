/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.net;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mercandalli.com.jarvis.Application;
import mercandalli.com.jarvis.listener.IListener;

/**
 * Global behavior : DDL file
 * 
 * @author Jonathan
 * 
 */
public class TaskGetDownload extends AsyncTask<Void, Void, Void> {

	String url;
	String url_ouput;
	IListener listener;
	File file;
	Application app;
	
	public TaskGetDownload(Application app, String url, String url_ouput, IListener listener) {
		this.app = app;
		this.url = url;
		this.url_ouput = url_ouput;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Void... urls) {		
		file = file_from_url_Authorization(this.url);
		return null;		
	}

	@Override
	protected void onPostExecute(Void response) {
		Log.d("onPostExecute", "" + response);
		this.listener.execute();
	}
	
	public File file_from_url_Authorization(String url) {
		File x = null;
		HttpResponse response;
		HttpGet httpget = new HttpGet(url);
    	StringBuilder authentication = new StringBuilder().append(app.getConfig().getUser().getAccessLogin()).append(":").append(app.getConfig().getUser().getAccessPassword());
        String result = Base64.encodeBytes(authentication.toString().getBytes());
        httpget.setHeader("Authorization", "Basic " + result);
		HttpClient httpclient = new DefaultHttpClient();
		try {
			response = httpclient.execute(httpget);
			InputStream inputStream = response.getEntity().getContent();
			//long lenghtOfFile = response.getEntity().getContentLength();
			OutputStream outputStream = new FileOutputStream(url_ouput);
			
			byte data[] = new byte[1024];
			 
            //long total = 0;
 
            int count;
            while ((count = inputStream.read(data)) != -1) {
                //total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                //publishProgress(""+(int)((total*100)/lenghtOfFile));
 
                // writing data to file
                outputStream.write(data, 0, count);
            }
 
            // flushing output
            outputStream.flush();
 
            // closing streams
            outputStream.close();
            inputStream.close();
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return x;
    }
}
