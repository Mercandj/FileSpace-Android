/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.adapter.AdapterModelFile;
import com.mercandalli.jarvis.listener.IModelFileListener;
import com.mercandalli.jarvis.model.ModelFile;

public class DialogFileChooser extends Dialog {
	
	Application app;
	ListView files;
	List<ModelFile> listModelFile;
	String currentUrl = "/";
	IModelFileListener listener;
	
	public DialogFileChooser(final Application app, IModelFileListener listener) {
		super(app);
		
		this.app = app;
		this.listener = listener;
		
		this.setContentView(R.layout.view_filechooser);
		this.setTitle(R.string.app_name);
		this.setCancelable(true);
		
		files = (ListView) this.findViewById(R.id.files);		
		updateAdapter();
        
        DialogFileChooser.this.show();
	}
	
	private void updateAdapter() {
		getFiles();
		files.setAdapter(new AdapterModelFile(app, R.layout.tab_file, listModelFile ));
		files.setOnItemClickListener(new OnItemClickListener() {
		    @Override 
		    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		    	if(position<listModelFile.size()) {
		    		ModelFile file = listModelFile.get(position);
		    		if(file.isDirectory) {
		    			currentUrl += file.name+"/";
		    			updateAdapter();
		    		}
		    		else {
		    			DialogFileChooser.this.listener.execute(file);
		    			DialogFileChooser.this.dismiss();
		    		}
		    	}
		    }
		});
	}
	
	private void getFiles() {
		String path = Environment.getExternalStorageDirectory().getPath()+currentUrl;
		File f = new File(path);        
		File fs[] = f.listFiles();
		listModelFile = new ArrayList<ModelFile>();
		if(fs!=null)
			for(File file : fs) {
				ModelFile modelFile = new ModelFile();
				modelFile.url = file.getAbsolutePath();
				modelFile.name = file.getName();
				modelFile.size = ""+file.getTotalSpace();
				modelFile.isDirectory = file.isDirectory();
				listModelFile.add(modelFile);
			}
	}
}
