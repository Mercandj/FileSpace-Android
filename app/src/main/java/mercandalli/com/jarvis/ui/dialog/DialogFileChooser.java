/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.ui.dialog;

import android.app.Dialog;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
	private String currentUrl = "/";
	private IModelFileListener listener;
	
	public DialogFileChooser(final Application app, IModelFileListener listener) {
		super(app);
		
		this.app = app;
		this.listener = listener;
		
		this.setContentView(R.layout.view_filechooser);
		this.setTitle(R.string.app_name);
		this.setCancelable(true);
		
        files = (RecyclerView) this.findViewById(R.id.files);
        files.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        files.setLayoutManager(mLayoutManager);

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
				modelFile.size = file.getTotalSpace();
				modelFile.directory = file.isDirectory();
				listModelFile.add(modelFile);
			}
	}
}
