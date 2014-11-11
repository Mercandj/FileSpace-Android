/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.adapter.AdapterModelFile;
import com.mercandalli.jarvis.listener.IPostExecuteListener;
import com.mercandalli.jarvis.model.ModelFile;
import com.mercandalli.jarvis.net.TaskGet;

public class FileManagerFragmentServer extends Fragment {
	
	Application app;
	ListView listView;
	List<ModelFile> listModelFile;
	ProgressBar circulerProgressBar;
	TextView message;
	
	public FileManagerFragmentServer(Application app) {
		this.app = app;
	}	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
        View rootView = inflater.inflate(R.layout.fragment_filemanager_online, container, false);
        circulerProgressBar = (ProgressBar) rootView.findViewById(R.id.circulerProgressBar);
        message = (TextView) rootView.findViewById(R.id.message);
        listView = (ListView) rootView.findViewById(R.id.listView);
        
        if(this.app.config.isLoginSucceed)
        	refreshList();
        
        return rootView;
    }	
	
	public void refreshList() {
			
		new TaskGet(app, this.app.config.getUrlServer()+this.app.config.routeFile, new IPostExecuteListener() {
			@Override
			public void execute(JSONObject json, String body) {
				listModelFile = new ArrayList<ModelFile>();
				try {
					if(json!=null) {
						if(json.has("result")) {							
							JSONArray array = json.getJSONArray("result");
							for(int i=0; i<array.length();i++) {
								ModelFile modelFile = new ModelFile();
								JSONObject fileJson = array.getJSONObject(i);
								if(fileJson.has("url")) {
									modelFile.url = fileJson.getString("url");
								}
								listModelFile.add(modelFile);
							}
							circulerProgressBar.setVisibility(View.INVISIBLE);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				updateAdapter();
			}			
		}, null).execute();
			
	}
	
	private void updateAdapter() {
		if(listView!=null && listModelFile!=null) {
			
			if(listModelFile.size()==0) {
				message.setText(getString(R.string.no_file_server));
				message.setVisibility(View.VISIBLE);
			}
			else
				message.setVisibility(View.GONE);
			
			listView.setAdapter(new AdapterModelFile(app, R.layout.tab_file, listModelFile ));
		}
	}
}
