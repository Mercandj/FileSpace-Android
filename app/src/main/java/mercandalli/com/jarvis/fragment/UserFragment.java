package mercandalli.com.jarvis.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
import mercandalli.com.jarvis.adapter.AdapterModelUser;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.net.TaskGet;

/**
 * Created by Jonathan on 30/03/2015.
 */
public class UserFragment extends Fragment {

    Application app;
    private View rootView;

    private RecyclerView recyclerView;
    private AdapterModelUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ModelUser> list;
    private ProgressBar circulerProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public UserFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_user, container, false);
        circulerProgressBar = (ProgressBar) rootView.findViewById(R.id.circulerProgressBar);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.GONE);

        refreshList();

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                this.app.getConfig().getUrlServer() + this.app.getConfig().routeUser,
                new IPostExecuteListener() {
                    @Override
                    public void execute(JSONObject json, String body) {
                        list = new ArrayList<ModelUser>();
                        try {
                            if (json != null) {
                                if (json.has("result")) {
                                    JSONArray array = json.getJSONArray("result");
                                    for (int i = 0; i < array.length(); i++) {
                                        ModelUser modelUser = new ModelUser(app, array.getJSONObject(i));
                                        list.add(modelUser);
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

            this.mAdapter = new AdapterModelUser(app, list, null);
            this.recyclerView.setAdapter(mAdapter);
            this.recyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());

            if( ((ImageButton) rootView.findViewById(R.id.circle)).getVisibility()==View.GONE ) {
                ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
                ((ImageButton) rootView.findViewById(R.id.circle)).startAnimation(animOpen);
            }

            ((ImageButton) rootView.findViewById(R.id.circle)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO
                }
            });

            this.mAdapter.setOnItemClickListener(new AdapterModelUser.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });

            this.swipeRefreshLayout.setRefreshing(false);
            i=0;
        }
    }
}
