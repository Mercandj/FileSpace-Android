/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.adapter.AdapterModelFile;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.listener.IModelFileListener;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.model.ModelFileType;

public class FileManagerFragmentLocal extends Fragment {
	
	private Application app;
	private ListView listView;
	private ArrayList<ModelFile> listModelFile;
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
        
        ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.GONE);
        ((ImageButton) rootView.findViewById(R.id.circle2)).setVisibility(View.GONE);

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
		if(fs!=null) {
            int tmp_id = 0;
            for (File file : fs) {
                ModelFile modelFile = new ModelFile(app);
                modelFile.id = file.hashCode()+tmp_id;
                modelFile.url = file.getAbsolutePath();
                modelFile.name = file.getName();
                modelFile.type = new ModelFileType(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1));
                modelFile.size = "" + file.getTotalSpace();
                modelFile.directory = file.isDirectory();
                modelFile.file = file;
                listModelFile.add(modelFile);
                tmp_id++;
            }
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
			
			listView.setAdapter(new AdapterModelFile(app, R.layout.tab_file, listModelFile, new IModelFileListener() {
				@Override
				public void execute(final ModelFile modelFile) {
					final AlertDialog.Builder menuAleart = new AlertDialog.Builder(FileManagerFragmentLocal.this.app);
					final String[] menuList = { "Delete" };
					menuAleart.setTitle("Action");
					menuAleart.setItems(menuList,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {
									switch (item) {
									case 0:
										FileManagerFragmentLocal.this.app.alert("Delete", "Delete file ?", "Yes", new IListener() {
											@Override
											public void execute() {
												modelFile.delete(new IPostExecuteListener() {
													@Override
													public void execute(JSONObject json, String body) {
														FileManagerFragmentLocal.this.app.refreshAdapters();
													}
												});
											}
										}, "No", null);
										break;
									}
								}
							});
					AlertDialog menuDrop = menuAleart.create();
					menuDrop.show();					
				}				
			}));
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					listModelFile.get(position).executeLocal(listModelFile);
				}
			});
			
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
