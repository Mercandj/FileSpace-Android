/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.jarvis.ui.dialog;

import android.app.Dialog;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.ui.adapter.AdapterModelFile;
import mercandalli.com.jarvis.listener.IModelFileListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.model.ModelFileType;

public class DialogFileChooser extends Dialog {
	
	private Application app;
	private RecyclerView files;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ModelFile> listModelFile;
	private File currentFolder;
	private IModelFileListener listener;
	
	public DialogFileChooser(final Application app, IModelFileListener listener) {
		super(app);
		
		this.app = app;
		this.listener = listener;
		
		this.setContentView(R.layout.dialog_filechooser);
		this.setTitle(R.string.app_name);
		this.setCancelable(true);
		
        files = (RecyclerView) this.findViewById(R.id.files);
        files.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        files.setLayoutManager(mLayoutManager);

		this.currentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        ((Button) this.findViewById(R.id.up)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File parent = currentFolder.getParentFile();
                if(parent != null)
                    currentFolder = parent;
                updateAdapter();
            }
        });

		updateAdapter();
        
        DialogFileChooser.this.show();
	}
	
	private void updateAdapter() {
		getFiles();
        AdapterModelFile adapter = new AdapterModelFile(app, listModelFile, null);
        files.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterModelFile.OnItemClickListener() {
		    @Override 
		    public void onItemClick(View arg1, int position) {
		    	if(position<listModelFile.size()) {
		    		ModelFile file = listModelFile.get(position);
		    		if(file.directory) {
		    			currentFolder = file.getFile();
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
		String path = this.currentFolder.getAbsolutePath();
		File f = new File(path);        
		File fs[] = f.listFiles();
		listModelFile = new ArrayList<ModelFile>();
		if(fs!=null)
			for(File file : fs) {
				ModelFile modelFile = new ModelFile(this.app);
				modelFile.url = file.getAbsolutePath();
				modelFile.name = file.getName();
				modelFile.type = new ModelFileType(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")+1));
				modelFile.size = file.getTotalSpace();
				modelFile.directory = file.isDirectory();
                modelFile.setFile(file);
				listModelFile.add(modelFile);
			}
	}
}
