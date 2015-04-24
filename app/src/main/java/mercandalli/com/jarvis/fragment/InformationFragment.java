/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.fragment;

import android.app.Activity;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.adapter.AdapterModelInformation;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelInformation;
import mercandalli.com.jarvis.net.TaskGet;


public class InformationFragment extends Fragment {

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
    
    public InformationFragment() {
    	super();
	}

	public InformationFragment(Application app) {
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

        ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.GONE);

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
		if(this.recyclerView!=null && this.list!=null && this.isAdded()) {
            this.circulerProgressBar.setVisibility(View.GONE);

            this.mAdapter = new AdapterModelInformation(app, list);
            this.recyclerView.setAdapter(mAdapter);
            this.recyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());

            if( ((ImageButton) rootView.findViewById(R.id.circle)).getVisibility()==View.GONE ) {
                ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
                ((ImageButton) rootView.findViewById(R.id.circle)).startAnimation(animOpen);
            }

	        ((ImageButton) rootView.findViewById(R.id.circle)).setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					mAdapter.addItem(new ModelInformation("Number", ""+i), 0);
					recyclerView.scrollToPosition(0);
					i++;
				}
			});

            this.mAdapter.setOnItemClickListener(new AdapterModelInformation.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });

            this.swipeRefreshLayout.setRefreshing(false);
	        i=0;
		}
	}

    @Override
    public boolean back() {
        return false;
    }
}