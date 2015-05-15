/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;

import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.listener.IBitmapListener;
import mercandalli.com.jarvis.model.ModelFile;
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
    ModelFile fileModel;

	public TaskGetDownloadImage(Application app, ModelUser user, ModelFile file, IBitmapListener listener) {
		this.app = app;
		this.user = user;
		this.url = file.onlineUrl;
        this.fileModel = file;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Void... urls) {		
		bitmap = drawable_from_url_Authorization();
		return null;		
	}

	@Override
	protected void onPostExecute(Void response) {
		this.listener.execute(bitmap);
	}
	
	public Bitmap drawable_from_url_Authorization() {
        if(is_image())
            return load_image();
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
            save_image(x);
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











    private static Map<Integer, Bitmap> images = new WeakHashMap<Integer, Bitmap>();

    public void save_image(Bitmap bm) {
        images.put(this.fileModel.id,bm);

        File file = new File(this.app.getFilesDir()+"/file_"+this.fileModel.id);
        if(file.exists()) return;

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(this.app.getFilesDir()+"/file_"+this.fileModel.id);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }
    }

    public Bitmap load_image() {
        if(images.containsKey(this.fileModel.id))
            return images.get(this.fileModel.id);

        File file = new File(this.app.getFilesDir()+"/file_"+this.fileModel.id);
        if(file.exists())
            return BitmapFactory.decodeFile(file.getPath());
        Log.e("TaskGetDownloadImage", "load_photo(String url) return null");
        return null;
    }

    public boolean is_image() {
        if(images.containsKey(this.fileModel.id))
            return true;
        File file = new File(this.app.getFilesDir()+"/file_"+this.fileModel.id);
        return file.exists();
    }
}
