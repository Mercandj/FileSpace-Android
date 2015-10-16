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
package mercandalli.com.filespace.ui.dialogs;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IModelFileListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.net.TaskPost;

public class DialogRequest extends Dialog {
	
	DialogFileChooser dialogFileChooser;
	private ApplicationActivity app;
	private File file;
	ModelFile modelFile;
	
	private final int GET			= 0;
	private final int POST			= 1;
	private final int PUT			= 2;
	private final int DELETE		= 3;
	private final int nbMethod		= 4;
	private int currentMethod 		= GET;
	
	public DialogRequest(final ApplicationActivity app, final IPostExecuteListener listener) {
		super(app);
		this.app = app;
		
		this.setContentView(R.layout.dialog_request);
		this.setTitle(R.string.app_name);
		this.setCancelable(true);
	    
        ((Button) this.findViewById(R.id.request)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				switch(currentMethod) {
				
				case POST:					
					if(!((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString().equals(""))
						(new TaskPost(app, app.getConfig().getUrlServer()+((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString(), new IPostExecuteListener() {
							@Override
							public void execute(JSONObject json, String body) {
								if(listener!=null)
									listener.execute(json, body);
							}
						}, file)).execute();
					break;
					
				case PUT:
					//TODO Dev: request PUT
					Toast.makeText(getContext(), getContext().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
					break;
					
				case DELETE:
                    //TODO Dev: request DELETE
					Toast.makeText(getContext(), getContext().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
					break;
					
				default: //GET
					if(!((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString().equals(""))
						(new TaskGet(app, app.getConfig().getUser(), app.getConfig().getUrlServer()+((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString(), new IPostExecuteListener() {
							@Override
							public void execute(JSONObject json, String body) {
								if(listener!=null)
									listener.execute(json, body);
							}
						}, null)).execute();					
				}
				DialogRequest.this.dismiss();
			}        	
        });
        
        ((Button) this.findViewById(R.id.fileButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogFileChooser = new DialogFileChooser(DialogRequest.this.app, new IModelFileListener() {
					@Override
					public void execute(ModelFile modelFile) {
						((TextView) DialogRequest.this.findViewById(R.id.label)).setText(""+modelFile.url);
						DialogRequest.this.file = new File(modelFile.url);
						DialogRequest.this.modelFile = modelFile;
					}					
				});
			}        	
        });
        
        ((TextView) this.findViewById(R.id.method)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentMethod++;
				if(currentMethod>=nbMethod)
					currentMethod=0;
				refreshButtonMethod();
			}        	
        });
        refreshButtonMethod();
        
        DialogRequest.this.show();
	}
	
	public void refreshButtonMethod() {
		switch(currentMethod) {
		case 1: ((TextView) this.findViewById(R.id.method)).setText("POST"); break;
		case 2: ((TextView) this.findViewById(R.id.method)).setText("PUT"); break;
		case 3: ((TextView) this.findViewById(R.id.method)).setText("DELETE"); break;
		default: ((TextView) this.findViewById(R.id.method)).setText("GET");
		}
	}
}
