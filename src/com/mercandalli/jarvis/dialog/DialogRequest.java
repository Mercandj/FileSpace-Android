/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.dialog;

import java.io.File;

import org.json.JSONObject;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.listener.IModelFileListener;
import com.mercandalli.jarvis.listener.IPostExecuteListener;
import com.mercandalli.jarvis.model.ModelFile;
import com.mercandalli.jarvis.net.TaskGet;
import com.mercandalli.jarvis.net.TaskPost;

public class DialogRequest extends Dialog {
	
	DialogFileChooser dialogFileChooser;
	Application app;
	File file;
	ModelFile modelFile;	
	
	private final int GET			= 0;
	private final int POST			= 1;
	private final int PUT			= 2;
	private final int DELETE		= 3;
	private final int nbMethod		= 4;
	private int currentMethod 		= GET;
	
	public DialogRequest(final Application app, final IPostExecuteListener listener) {
		super(app);
		this.app = app;
		
		this.setContentView(R.layout.view_request);
		this.setTitle(R.string.app_name);
		this.setCancelable(true);
	    
        ((Button) this.findViewById(R.id.request)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				switch(currentMethod) {
				
				case POST:					
					if(!((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString().equals(""))
						(new TaskPost(app, app.config.getUrlServer()+((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString(), new IPostExecuteListener() {
							@Override
							public void execute(JSONObject json, String body) {
								if(listener!=null)
									listener.execute(json, body);
							}
						}, file)).execute();
					break;
					
				case PUT:
					//TODO
					break;
					
				case DELETE:
					//TODO
					break;
					
				default: //GET
					if(!((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString().equals(""))
						(new TaskGet(app, app.config.getUrlServer()+((EditText) DialogRequest.this.findViewById(R.id.server)).getText().toString(), new IPostExecuteListener() {
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
