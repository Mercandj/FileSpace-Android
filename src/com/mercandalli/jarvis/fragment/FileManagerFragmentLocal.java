/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.adapter.AdapterModelFile;
import com.mercandalli.jarvis.model.ModelFile;

public class FileManagerFragmentLocal extends Fragment {
	
	Application app;
	ListView listView;
	List<ModelFile> listModelFile;
	ProgressBar circulerProgressBar;
	File jarvisDirectory;
	TextView message;
	
	public FileManagerFragmentLocal(Application app) {
		this.app = app;
	}	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filemanager_online, container, false);
        circulerProgressBar = (ProgressBar) rootView.findViewById(R.id.circulerProgressBar);
        circulerProgressBar.setVisibility(View.INVISIBLE);
        message = (TextView) rootView.findViewById(R.id.message);
        listView = (ListView) rootView.findViewById(R.id.listView);
        
		jarvisDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+app.config.localFolderName);
		if(!jarvisDirectory.exists())
			jarvisDirectory.mkdir();
    	refreshList();
                
        return rootView;
    }
	
	public void refreshList() {
		if(jarvisDirectory==null)
			return;
			
		File fs[] = jarvisDirectory.listFiles();
		listModelFile = new ArrayList<ModelFile>();
		if(fs!=null)
			for(File file : fs) {
				ModelFile modelFile = new ModelFile();
				modelFile.url = file.getAbsolutePath();
				modelFile.size = ""+file.getTotalSpace();
				modelFile.isDirectory = file.isDirectory();
				listModelFile.add(modelFile);
			}
		
		updateAdapter();		
	}
	
	private void updateAdapter() {
		if(listView!=null && listModelFile!=null) {
			
			if(listModelFile.size()==0) {
				message.setText(getString(R.string.no_file_local));
				message.setVisibility(View.VISIBLE);
			}
			else
				message.setVisibility(View.GONE);
			
			listView.setAdapter(new AdapterModelFile(app, R.layout.tab_file, listModelFile ));
		}
	}
}
