/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.adapter.AdapterModelFile;
import com.mercandalli.jarvis.model.ModelFile;
import com.mercandalli.jarvis.model.ModelFileType;

public class FileManagerFragmentLocal extends Fragment {
	
	private Application app;
	private ListView listView;
	private List<ModelFile> listModelFile;
	private ProgressBar circulerProgressBar;
	private File jarvisDirectory;
	private TextView message;
	private SwipeRefreshLayout swipeRefreshLayout;	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.app = (Application) activity;
    }	
	
	public FileManagerFragmentLocal() {
		super();
	}
	
	public FileManagerFragmentLocal(Application app) {
		super();
		this.app = app;
	}	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filemanager_online, container, false);
        circulerProgressBar = (ProgressBar) rootView.findViewById(R.id.circulerProgressBar);
        circulerProgressBar.setVisibility(View.INVISIBLE);
        message = (TextView) rootView.findViewById(R.id.message);
        listView = (ListView) rootView.findViewById(R.id.listView);
        
        ((ImageView) rootView.findViewById(R.id.circle)).setVisibility(View.GONE);
        ((ImageView) rootView.findViewById(R.id.circle_ic)).setVisibility(View.GONE);        
        
		jarvisDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+this.app.getConfig().localFolderName);
		if(!jarvisDirectory.exists())
			jarvisDirectory.mkdir();
    	
    	swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshList();
			}
		});
		
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
				ModelFile modelFile = new ModelFile(app);
				modelFile.url = file.getAbsolutePath();
				modelFile.name = file.getName();
				modelFile.type = new ModelFileType(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")+1));
				modelFile.size = ""+file.getTotalSpace();
				modelFile.isDirectory = file.isDirectory();
				modelFile.file = file;
				listModelFile.add(modelFile);
			}
		
		updateAdapter();		
	}
	
	public void updateAdapter() {
		if(listView!=null && listModelFile!=null) {
			
			if(listModelFile.size()==0) {
				message.setText(getString(R.string.no_file_local));
				message.setVisibility(View.VISIBLE);
			}
			else
				message.setVisibility(View.GONE);
			
			listView.setAdapter(new AdapterModelFile(app, R.layout.tab_file, listModelFile, null));
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					listModelFile.get(position).executeLocal();
				}
			});
			
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
