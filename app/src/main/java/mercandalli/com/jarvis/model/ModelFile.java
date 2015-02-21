/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.ActivityFileAudio;
import mercandalli.com.jarvis.activity.ActivityFileText;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.listener.IBitmapListener;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.net.TaskDelete;
import mercandalli.com.jarvis.net.TaskGetDownload;
import mercandalli.com.jarvis.net.TaskGetDownloadImage;
import mercandalli.com.jarvis.net.TaskPut;

public class ModelFile extends Model implements Parcelable {
	
	public int id;
	public String url;
	public String name;
	public String size;
	public ModelFileType type;
	public boolean directory = false;
	public Bitmap bitmap;
	public File file;
    public String onlineUrl;
	
	public List<BasicNameValuePair> getForUpload() {
		List<BasicNameValuePair> parameters = new ArrayList<>();
		if(name!=null)
			parameters.add(new BasicNameValuePair("url", this.name));
        if(directory)
            parameters.add(new BasicNameValuePair("directory", this.directory?"true":"false"));
		return parameters;
	}
	
	public ModelFile(Application app) {
		super(app);
	}
	
	public ModelFile(Application app, JSONObject json) {
		super(app);
		
		try {
			if(json.has("id")) {
                this.id = json.getInt("id");
                this.onlineUrl = this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id;
            }
			if(json.has("url"))
                this.url = json.getString("url");
            if(json.has("name"))
                this.name = json.getString("name");
			if(json.has("type")) {
                this.type = new ModelFileType(json.getString("type"));
                this.name += "." + this.type;
            }
            if(json.has("directory") && !json.isNull("directory"))
                this.directory = json.getInt("directory")==1;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(this.type.equals(ModelFileTypeENUM.PICTURE.type)) {
			new TaskGetDownloadImage(app, this.app.getConfig().getUser(), this.onlineUrl, new IBitmapListener() {
				@Override
				public void execute(Bitmap bitmap) {
					ModelFile.this.bitmap = bitmap;
					ModelFile.this.app.updateAdapters();
				}
			}).execute();
		}		
	}
	
	public void executeOnline(ArrayList<ModelFile> files) {
		if(this.type.equals(ModelFileTypeENUM.TEXT.type)) {
            Intent intent = new Intent(this.app, ActivityFileText.class);
            intent.putExtra("URL_FILE", ""+this.onlineUrl);
            intent.putExtra("LOGIN", ""+this.app.getConfig().getUser().getAccessLogin());
            intent.putExtra("PASSWORD", ""+this.app.getConfig().getUser().getAccessPassword());
            intent.putExtra("ONLINE", true);
            this.app.startActivity(intent);
            this.app.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
		else if(this.type.equals(ModelFileTypeENUM.AUDIO.type)) {
            Intent intent = new Intent(app, ActivityFileAudio.class);
            intent.putExtra("LOGIN", ""+app.getConfig().getUser().getAccessLogin());
            intent.putExtra("PASSWORD", ""+app.getConfig().getUser().getAccessPassword());
            intent.putExtra("ONLINE", true);
            intent.putExtra("FILE", this);
            ArrayList<ModelFile> tmpFiles = new ArrayList<ModelFile>();
            for(ModelFile f:files)
                if(f.type.equals(ModelFileTypeENUM.AUDIO.type))
                    tmpFiles.add(f);
            intent.putParcelableArrayListExtra("FILES", tmpFiles);
            this.app.startActivity(intent);
            this.app.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
	}
	
	public void executeLocal(ArrayList<ModelFile> files) {
		if (!file.exists())
			return;
		if (this.type.equals(ModelFileTypeENUM.APK.type)) {
			Intent apkIntent = new Intent();
			apkIntent.setAction(Intent.ACTION_VIEW);
			apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            this.app.startActivity(apkIntent);
		}
		else if(this.type.equals(ModelFileTypeENUM.TEXT.type)) {
			Intent txtIntent = new Intent();
			txtIntent.setAction(Intent.ACTION_VIEW);
			txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");
			try {
				this.app.startActivity(txtIntent);
			} catch (ActivityNotFoundException e) {
				txtIntent.setType("text/*");
                this.app.startActivity(txtIntent);
			}
		}
		else if(this.type.equals(ModelFileTypeENUM.HTML.type)) {
			Intent htmlIntent = new Intent();
			htmlIntent.setAction(Intent.ACTION_VIEW);
			htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");
			try {
				this.app.startActivity(htmlIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this.app, "ERREUR", Toast.LENGTH_SHORT).show();
			}
		}
		else if(this.type.equals(ModelFileTypeENUM.AUDIO.type)) {
            Intent intent = new Intent(this.app, ActivityFileAudio.class);
            intent.putExtra("ONLINE", false);
            intent.putExtra("FILE", this);
            ArrayList<ModelFile> tmpFiles = new ArrayList<ModelFile>();
            for(ModelFile f:files)
                if(f.type.equals(ModelFileTypeENUM.AUDIO.type))
                    tmpFiles.add(f);
            intent.putParcelableArrayListExtra("FILES", tmpFiles);
            this.app.startActivity(intent);
            this.app.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
		else if(this.type.equals(ModelFileTypeENUM.PICTURE.type)) {
			Intent picIntent = new Intent();
			picIntent.setAction(Intent.ACTION_VIEW);
			picIntent.setDataAndType(Uri.fromFile(file), "image/*");
            this.app.startActivity(picIntent);
		}
		else if(this.type.equals(ModelFileTypeENUM.VIDEO.type)) {
			Intent movieIntent = new Intent();
			movieIntent.setAction(Intent.ACTION_VIEW);
			movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
			app.startActivity(movieIntent);
		}
		else if(this.type.equals(ModelFileTypeENUM.PDF.type)) {
			Intent pdfIntent = new Intent();
			pdfIntent.setAction(Intent.ACTION_VIEW);
			pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
			try {
				app.startActivity(pdfIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(app, "ERREUR", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void download(IListener listener) {
		String url = this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id;
		String url_ouput = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+app.getConfig().localFolderName+File.separator+this.name+"."+this.type;
		new TaskGetDownload(this.app, url, url_ouput, listener).execute();
	}
	
	public boolean isOnline() {
		return (file==null);
	}
	
	public void delete(IPostExecuteListener listener) {
		if(this.isOnline()) {
			String url = this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id;
			new TaskDelete(app, url, listener).execute();
		}
		else {
			file.delete();			
			listener.execute(null, null);
		}
	}
	
	public void rename(String new_name, IPostExecuteListener listener) {
		this.name = new_name;
		this.url = new_name;
		String url = this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id;
		new TaskPut(app, url, listener, getForUpload()).execute();
	}





    public static final Parcelable.Creator<ModelFile> CREATOR = new Parcelable.Creator<ModelFile>() {
        @Override
        public ModelFile createFromParcel(Parcel source) {
            return new ModelFile(source);
        }
        @Override
        public ModelFile[] newArray(int size) {
            return new ModelFile[size];
        }
    };

    public ModelFile(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.onlineUrl = in.readString();
        this.name = in.readString();
        this.size = in.readString();
        boolean[] b = new boolean[1];
        in.readBooleanArray(b);
        this.directory = b[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeString(this.onlineUrl);
        dest.writeString(this.name);
        dest.writeString(this.size);
        dest.writeBooleanArray(new boolean[]{this.directory});
    }
}