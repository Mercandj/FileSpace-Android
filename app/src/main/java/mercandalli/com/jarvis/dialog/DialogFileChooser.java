/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.dialog;

import android.app.Dialog;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.adapter.AdapterModelFile;
import mercandalli.com.jarvis.listener.IModelFileListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.model.ModelFileType;

public class DialogFileChooser extends Dialog {
	
	private Application app;
	private ListView files;
	private List<ModelFile> listModelFile;
	private String currentUrl = "/";
	private IModelFileListener listener;
	
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
		files.setAdapter(new AdapterModelFile(app, R.layout.tab_file, listModelFile, null));
		files.setOnItemClickListener(new OnItemClickListener() {
		    @Override 
		    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		    	if(position<listModelFile.size()) {
		    		ModelFile file = listModelFile.get(position);
		    		if(file.directory) {
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
				ModelFile modelFile = new ModelFile(this.app);
				modelFile.url = file.getAbsolutePath();
				modelFile.name = file.getName();
				modelFile.type = new ModelFileType(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")+1));
				modelFile.size = ""+file.getTotalSpace();
				modelFile.directory = file.isDirectory();
				listModelFile.add(modelFile);
			}
	}
}
