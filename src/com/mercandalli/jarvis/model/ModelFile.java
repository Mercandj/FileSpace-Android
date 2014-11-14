/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.dialog.DialogShowTxt;
import com.mercandalli.jarvis.listener.IBitmapListener;
import com.mercandalli.jarvis.listener.IPostExecuteListener;
import com.mercandalli.jarvis.net.Base64;
import com.mercandalli.jarvis.net.TaskDownloadImage;
import com.mercandalli.jarvis.net.TaskGet;

public class ModelFile {
	
	private Application app;
	
	public int id;
	public String url;
	public String name;
	public String size;
	public String type;
	public boolean isDirectory;
	public Bitmap bitmap;
	
	public List<BasicNameValuePair> getForUpload() {
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		/*if(url!=null)
			parameters.add(new BasicNameValuePair("name", name));*/
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
				this.type = json.getString("type");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		switch(this.type) {
		case "jpg":
			new TaskDownloadImage(app, this.app.config.getUrlServer()+this.app.config.routeFile+"/"+this.id, new IBitmapListener() {
				@Override
				public void execute(Bitmap bitmap) {
					ModelFile.this.bitmap = bitmap;
					ModelFile.this.app.updateAdapters();
				}
			}).execute();
			break;
		}
	}
	
	public void execute() {
		switch(this.type) {
		
		case "txt":		
			new TaskGet(this.app, this.app.config.getUrlServer()+this.app.config.routeFile+"/"+id, new IPostExecuteListener() {
				@Override
				public void execute(JSONObject json, String body) {
					new DialogShowTxt(app, body);
				}				
			}).execute();			
			break;
			
		case "mp3":			
			try {
				Uri uri = Uri.parse(this.app.config.getUrlServer()+this.app.config.routeFile+"/"+id);
				
				Map<String, String> headers = new HashMap<String, String>();
				StringBuilder authentication = new StringBuilder().append(app.config.getUser().getAccessLogin()).append(":").append(app.config.getUser().getAccessPassword());
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
			break;
		}
	}	
}
