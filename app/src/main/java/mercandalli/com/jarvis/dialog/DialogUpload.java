/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.dialog;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.listener.IModelFileListener;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.net.TaskPost;

public class DialogUpload extends Dialog {
	
	DialogFileChooser dialogFileChooser;
	Application app;
	File file;
	ModelFile modelFile;
    int id_file_parent;
	
	public DialogUpload(final Application app, final int id_file_parent, final IPostExecuteListener listener) {
		super(app);
		this.app = app;
        this.id_file_parent = id_file_parent;
		
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
					(new TaskPost(app, app.getConfig().getUrlServer()+app.getConfig().routeFile, new IPostExecuteListener() {
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
                        modelFile.id_file_parent = DialogUpload.this.id_file_parent;
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
