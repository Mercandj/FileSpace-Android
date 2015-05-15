/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.net;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
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

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.util.FileUtils;

/**
 * Global behavior : DDL file
 * 
 * @author Jonathan
 * 
 */
public class TaskGetDownload extends AsyncTask<Void, Long, Void> {

	String url;
	String url_ouput;
	IListener listener;
	File file;
	Application app;
    ModelFile modelFile;

    int id = 1;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public TaskGetDownload(Application app, String url, String url_ouput, ModelFile modelFile, IListener listener) {
		this.app = app;
		this.url = url;
		this.url_ouput = url_ouput;
        this.modelFile = modelFile;
		this.listener = listener;
	}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mNotifyManager = (NotificationManager) this.app.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this.app);
        mBuilder.setContentTitle(this.modelFile.type.getTitle()+" Download")
                .setContentText("Download in progress : 0 / " + FileUtils.humanReadableByteCount(this.modelFile.size) + " : 0%")
                .setSmallIcon(R.drawable.ic_notification);
    }

    @Override
	protected Void doInBackground(Void... urls) {		
		file = file_from_url_Authorization(this.url);
		return null;		
	}

	@Override
	protected void onPostExecute(Void response) {
		Log.d("onPostExecute", "" + response);

        // When the loop is finished, updates the notification
        mBuilder.setContentText("Download complete")
                // Removes the progress bar
                .setProgress(0,0,false);
        mNotifyManager.notify(id, mBuilder.build());

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
			long lenghtOfFile = response.getEntity().getContentLength();
			OutputStream outputStream = new FileOutputStream(url_ouput);
			
			byte data[] = new byte[1024];
			 
            long total = 0;

            int missed_value = 50;
            int missed_conter = 0;
 
            int count;
            while ((count = inputStream.read(data)) != -1) {
                total += count;

                missed_conter++;
                if(missed_conter>missed_value) {
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(((total*100)/lenghtOfFile), total);

                    missed_conter = 0;
                }
 
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

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);

        long incr = 0;
        if(values.length>0)
            incr=values[0];
        mBuilder.setProgress(100, (int) incr, false);
        mBuilder.setContentText("Download in progress " + incr + "%");
        if(values.length>1)
            mBuilder.setContentText("Download in progress : " + FileUtils.humanReadableByteCount(values[1]) + " / " + FileUtils.humanReadableByteCount(this.modelFile.size) + " : " + incr + "%");

        mNotifyManager.notify(id, mBuilder.build());
    }
}
