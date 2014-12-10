/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis_android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis_android.Application;
import mercandalli.com.jarvis_android.R;
import mercandalli.com.jarvis_android.adapter.AdapterModelInformation;
import mercandalli.com.jarvis_android.config.Const;
import mercandalli.com.jarvis_android.listener.IPostExecuteListener;
import mercandalli.com.jarvis_android.model.ModelInformation;
import mercandalli.com.jarvis_android.net.TaskGet;


public class InformationManagerFragment extends Fragment {

	Application app;
	private View rootView;
	
	private RecyclerView recyclerView;
    private AdapterModelInformation mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ModelInformation> list;
    private ProgressBar circulerProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
        
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (Application) activity;
    }
    
    public InformationManagerFragment() {
    	super();
	}

	public InformationManagerFragment(Application app) {
		super();
		this.app = app;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_information, container, false);
		circulerProgressBar = (ProgressBar) rootView.findViewById(R.id.circulerProgressBar);
		
		recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
		recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        
        refreshList();
        
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
        
        return rootView;
	}
	

	public void refreshList() {
		List<BasicNameValuePair> parameters = null;
		new TaskGet(
				app, 
				this.app.getConfig().getUser(), 
				this.app.getConfig().getUrlServer() + this.app.getConfig().routeInformation, 
				new IPostExecuteListener() {
					@Override
					public void execute(JSONObject json, String body) {
						list = new ArrayList<ModelInformation>();
						list.add(new ModelInformation("Informations", Const.TAB_VIEW_TYPE_SECTION));
						try {
							if (json != null) {
								if (json.has("result")) {
									JSONArray array = json.getJSONArray("result");
									for (int i = 0; i < array.length(); i++) {
										ModelInformation modelFile = new ModelInformation(app, array.getJSONObject(i));
										list.add(modelFile);
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
	}
	
	int i;
	
	public void updateAdapter() {
		if(recyclerView!=null && list!=null && this.isAdded()) {			
			circulerProgressBar.setVisibility(View.GONE);
			
	        mAdapter = new AdapterModelInformation(app, list);
	        recyclerView.setAdapter(mAdapter);
	        recyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
	        
	        ((ImageButton) rootView.findViewById(R.id.circle)).setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					mAdapter.addItem(new ModelInformation("Number", ""+i), 0);
					recyclerView.scrollToPosition(0);
					i++;
				}
			});
	        
	        mAdapter.setOnItemClickListener(new AdapterModelInformation.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });
	        
	        swipeRefreshLayout.setRefreshing(false);
	        i=0;
		}
	}
}
