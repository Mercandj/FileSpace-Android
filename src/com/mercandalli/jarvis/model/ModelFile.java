/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.dialog.DialogShowTxt;
import com.mercandalli.jarvis.listener.IBitmapListener;
import com.mercandalli.jarvis.listener.IListener;
import com.mercandalli.jarvis.listener.IPostExecuteListener;
import com.mercandalli.jarvis.net.Base64;
import com.mercandalli.jarvis.net.TaskDelete;
import com.mercandalli.jarvis.net.TaskGet;
import com.mercandalli.jarvis.net.TaskGetDownload;
import com.mercandalli.jarvis.net.TaskGetDownloadImage;
import com.mercandalli.jarvis.net.TaskPut;

public class ModelFile {
	
	private Application app;
	
	public int id;
	public String url;
	public String name;
	public String size;
	public ModelFileType type;
	public boolean isDirectory;
	public Bitmap bitmap;
	public File file;
	
	public List<BasicNameValuePair> getForUpload() {
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		if(name!=null)
			parameters.add(new BasicNameValuePair("url", this.name));
		return parameters;
	}
	
	public ModelFile(Application app) {
		this.app = app;
	}
	
	public ModelFile(Application app, JSONObject json) {
		this.app = app;
		
		try {
			if(json.has("id"))
				this.id = json.getInt("id");
			if(json.has("url"))
				this.url = json.getString("url");
			if(json.has("type"))
				this.type = new ModelFileType(json.getString("type"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(this.type.equals(ModelFileTypeENUM.PICTURE.type)) {
			new TaskGetDownloadImage(app, this.app.getConfig().getUser(), this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+this.id, new IBitmapListener() {
				@Override
				public void execute(Bitmap bitmap) {
					ModelFile.this.bitmap = bitmap;
					ModelFile.this.app.updateAdapters();
				}
			}).execute();
		}
		
	}
	
	public void executeOnline() {
		if(this.type.equals(ModelFileTypeENUM.TEXT.type)) {
			new TaskGet(this.app, this.app.getConfig().getUser(), this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id, new IPostExecuteListener() {
				@Override
				public void execute(JSONObject json, String body) {
					new DialogShowTxt(app, body);
				}				
			}).execute();		
		}
		else if(this.type.equals(ModelFileTypeENUM.AUDIO.type)) {
			try {
				Uri uri = Uri.parse(this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id);
				
				Map<String, String> headers = new HashMap<String, String>();
				StringBuilder authentication = new StringBuilder().append(app.getConfig().getUser().getAccessLogin()).append(":").append(app.getConfig().getUser().getAccessPassword());
		        String result = Base64.encodeBytes(authentication.toString().getBytes());
		        headers.put("Authorization", "Basic " + result);
				
				MediaPlayer player = new MediaPlayer();
				player.setDataSource(this.app, uri, headers);
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				player.prepare();
				player.start();				
				
			} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void executeLocal() {
		if (!file.exists())
			return;
		if (this.type.equals(ModelFileTypeENUM.APK.type)) {
			Intent apkIntent = new Intent();
			apkIntent.setAction(android.content.Intent.ACTION_VIEW);
			apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			app.startActivity(apkIntent);
		}
		else if(this.type.equals(ModelFileTypeENUM.TEXT.type)) {
			Intent txtIntent = new Intent();
			txtIntent.setAction(android.content.Intent.ACTION_VIEW);
			txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");
			try {
				app.startActivity(txtIntent);
			} catch (ActivityNotFoundException e) {
				txtIntent.setType("text/*");
				app.startActivity(txtIntent);
			}
		}
		else if(this.type.equals(ModelFileTypeENUM.HTML.type)) {
			Intent htmlIntent = new Intent();
			htmlIntent.setAction(android.content.Intent.ACTION_VIEW);
			htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");
			try {
				app.startActivity(htmlIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(app, "ERREUR", Toast.LENGTH_SHORT).show();
			}
		}
		else if(this.type.equals(ModelFileTypeENUM.AUDIO.type)) {
			Intent i = new Intent();
			i.setAction(android.content.Intent.ACTION_VIEW);
			i.setDataAndType(Uri.fromFile(file), "audio/*");
			app.startActivity(i);
		}
		else if(this.type.equals(ModelFileTypeENUM.PICTURE.type)) {
			Intent picIntent = new Intent();
			picIntent.setAction(android.content.Intent.ACTION_VIEW);
			picIntent.setDataAndType(Uri.fromFile(file), "image/*");
			app.startActivity(picIntent);
		}
		else if(this.type.equals(ModelFileTypeENUM.VIDEO.type)) {
			Intent movieIntent = new Intent();
			movieIntent.setAction(android.content.Intent.ACTION_VIEW);
			movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
			app.startActivity(movieIntent);
		}
		else if(this.type.equals(ModelFileTypeENUM.PDF.type)) {
			Intent pdfIntent = new Intent();
			pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
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
		String url_ouput = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+app.getConfig().localFolderName+File.separator+this.url;
		new TaskGetDownload(this.app, url, url_ouput, listener).execute();
	}
	
	public void delete(IPostExecuteListener listener) {
		String url = this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id;
		new TaskDelete(app, url, listener).execute();
	}
	
	public void rename(String new_name, IPostExecuteListener listener) {
		this.name = new_name;
		this.url = new_name;
		String url = this.app.getConfig().getUrlServer()+this.app.getConfig().routeFile+"/"+id;
		new TaskPut(app, url, listener, getForUpload()).execute();
	}
}
