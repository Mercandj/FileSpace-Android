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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.adapter.AdapterModelFile;
import mercandalli.com.jarvis.dialog.DialogAddFileManager;
import mercandalli.com.jarvis.listener.IListener;
import mercandalli.com.jarvis.listener.IModelFileListener;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.listener.IStringListener;
import mercandalli.com.jarvis.model.ModelFile;
import mercandalli.com.jarvis.net.TaskGet;


public class FileManagerFragmentOnline extends Fragment {

	private Application app;
	private ListView listView;
	private ArrayList<ModelFile> files;
	private ProgressBar circularProgressBar;
	private TextView message;
	private SwipeRefreshLayout swipeRefreshLayout;
    Animation animOpen; ImageButton circle, circle2;

    private String url = "";
    private List<ModelFile> filesToCut = new ArrayList<>();
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (Application) activity;
    }

	public FileManagerFragmentOnline() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_filemanager_online, container, false);
		circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circulerProgressBar);
		message = (TextView) rootView.findViewById(R.id.message);
		listView = (ListView) rootView.findViewById(R.id.listView);

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

        circle = ((ImageButton) rootView.findViewById(R.id.circle));
        circle.setVisibility(View.GONE);
        animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);

        circle2 = ((ImageButton) rootView.findViewById(R.id.circle2));
        circle2.setVisibility(View.GONE);

        circle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                app.dialog = new DialogAddFileManager(app, new IPostExecuteListener() {
                    @Override
                    public void execute(JSONObject json, String body) {
                    if (json != null)
                        refreshList();
                    }
                });
            }
        });

        circle2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FileManagerFragmentOnline.this.url = "";
                FileManagerFragmentOnline.this.refreshList();
            }
        });

        refreshList();

		return rootView;
	}
	
	public void refreshList() {
		refreshList(null);
	}

	public void refreshList(String search) {
		List<BasicNameValuePair> parameters = new ArrayList<>();
		if(search!=null)
			parameters.add(new BasicNameValuePair("search", ""+search));
        parameters.add(new BasicNameValuePair("url", ""+this.url));

        if(this.app.isInternetConnection())
            new TaskGet(
                app,
                this.app.getConfig().getUser(),
                this.app.getConfig().getUrlServer() + this.app.getConfig().routeFile,
                new IPostExecuteListener() {
                    @Override
                    public void execute(JSONObject json, String body) {
                        files = new ArrayList<>();
                        try {
                            if (json != null) {
                                if (json.has("result")) {
                                    JSONArray array = json.getJSONArray("result");
                                    for (int i = 0; i < array.length(); i++) {
                                        ModelFile modelFile = new ModelFile(app, array.getJSONObject(i));
                                        files.add(modelFile);
                                    }
                                }
                            }
                            else
                                Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        updateAdapter();
                    }
                },
                parameters
            ).execute();
        else {
            this.circularProgressBar.setVisibility(View.GONE);
            this.message.setText(getString(R.string.no_internet_connection));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
	}

	public void updateAdapter() {
		if(this.listView!=null && this.files!=null && this.isAdded()) {

            this.circularProgressBar.setVisibility(View.GONE);
            if( this.circle.getVisibility()==View.GONE ) {
                this.circle.setVisibility(View.VISIBLE);
                this.circle.startAnimation(animOpen);
            }

			if(this.files.size()==0) {
                if(this.url==null)
				    this.message.setText(getString(R.string.no_file_server));
                else if(this.url.equals(""))
                    this.message.setText(getString(R.string.no_file_server));
                else
                    this.message.setText(getString(R.string.no_file_directory));
				this.message.setVisibility(View.VISIBLE);
			}
			else
				this.message.setVisibility(View.GONE);
			
			save_position();
			this.listView.setAdapter(new AdapterModelFile(app, R.layout.tab_file, files, new IModelFileListener() {
				@Override
				public void execute(final ModelFile modelFile) {
					final AlertDialog.Builder menuAleart = new AlertDialog.Builder(FileManagerFragmentOnline.this.app);
					final String[] menuList = { "Download", "Rename", "Delete", "Cut" };
					menuAleart.setTitle("Action");
					menuAleart.setItems(menuList,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {
									switch (item) {
                                        case 0:
                                            modelFile.download(new IListener() {
                                                @Override
                                                public void execute() {
                                                    Toast.makeText(app, "Download finished.", Toast.LENGTH_SHORT).show();
                                                    FileManagerFragmentOnline.this.app.updateAdapters();
                                                }
                                            });
                                            break;

                                        case 1:
                                            FileManagerFragmentOnline.this.app.prompt("Rename", "Rename " + (modelFile.directory ? "directory" : "file") + " " + modelFile.url + " ?", "Ok", new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    modelFile.rename(text, new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {
                                                            FileManagerFragmentOnline.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "Cancel", null, modelFile.name);
                                            break;

                                        case 2:
                                            FileManagerFragmentOnline.this.app.alert("Delete", "Delete " + (modelFile.directory ? "directory" : "file") + " " + modelFile.url + " ?", "Yes", new IListener() {
                                                @Override
                                                public void execute() {
                                                    modelFile.delete(new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {
                                                            FileManagerFragmentOnline.this.app.refreshAdapters();
                                                        }
                                                    });
                                                }
                                            }, "No", null);
                                            break;

                                        case 3:
                                            FileManagerFragmentOnline.this.filesToCut = new ArrayList<>();
                                            FileManagerFragmentOnline.this.filesToCut.add(modelFile);
                                            Toast.makeText(app, "File ready to cut.", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
								}
							});
					AlertDialog menuDrop = menuAleart.create();
					menuDrop.show();					
				}				
			}));
			restore_position();
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(files.get(position).directory) {
                        FileManagerFragmentOnline.this.url = files.get(position).url + "/";
                        refreshList();
                    }
                    else
                        files.get(position).executeOnline(files);
				}
			});

            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					
					return true;
				}
			});

            if(this.url==null)
                this.circle2.setVisibility(View.GONE);
            else if(this.url.equals(""))
                this.circle2.setVisibility(View.GONE);
            else
                this.circle2.setVisibility(View.VISIBLE);

            this.swipeRefreshLayout.setRefreshing(false);
		}
	}
	
	int savedPosition, savedListTop;
	
    public void save_position() {
    	if(listView==null) return;
		savedPosition = listView.getFirstVisiblePosition();
	    View firstVisibleView = listView.getChildAt(0);
	    savedListTop = (firstVisibleView == null) ? 0 : firstVisibleView.getTop();
	}
	
	public void restore_position() {
		if(listView==null)  		return;
		if (savedPosition >= 0) 	listView.setSelectionFromTop(savedPosition, savedListTop);
	}
}
