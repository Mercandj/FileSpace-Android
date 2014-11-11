/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.dialog;

import java.io.File;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.listener.IModelFileListener;
import com.mercandalli.jarvis.listener.IPostExecuteListener;
import com.mercandalli.jarvis.model.ModelFile;
import com.mercandalli.jarvis.net.TaskPost;

public class DialogUpload extends Dialog {
	
	DialogFileChooser dialogFileChooser;
	Application app;
	File file;
	ModelFile modelFile;
	
	public DialogUpload(final Application app, final IPostExecuteListener listener) {
		super(app);
		this.app = app;
		
		this.setContentView(R.layout.view_upload);
		this.setTitle(R.string.app_name);
		this.setCancelable(true);
	    
        ((Button) this.findViewById(R.id.request)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				if(file!=null) {
					List<BasicNameValuePair> parameters = null;
					if(DialogUpload.this.modelFile != null)
						parameters = DialogUpload.this.modelFile.getForUpload();
					(new TaskPost(app, app.config.getUrlServer()+app.config.routeFile, new IPostExecuteListener() {
						@Override
						public void execute(JSONObject json, String body) {
							if(listener!=null)
								listener.execute(json, body);
						}						
					}, parameters, file)).execute();
				}
				else
					Toast.makeText(app, app.getString(R.string.no_file), Toast.LENGTH_SHORT).show();
				
				DialogUpload.this.dismiss();
			}        	
        });
        
        ((Button) this.findViewById(R.id.fileButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogFileChooser = new DialogFileChooser(DialogUpload.this.app, new IModelFileListener() {
					@Override
					public void execute(ModelFile modelFile) {
						((TextView) DialogUpload.this.findViewById(R.id.label)).setText(""+modelFile.url);
						DialogUpload.this.file = new File(modelFile.url);
						DialogUpload.this.modelFile = modelFile;
					}					
				});
			}        	
        });        
        
        DialogUpload.this.show();
	}
}
