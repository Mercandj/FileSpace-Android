package mercandalli.com.jarvis.ui.fragment;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.ui.adapter.AdapterModelUser;
import mercandalli.com.jarvis.listener.IModelUserListener;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.listener.IStringListener;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.net.TaskGet;
import mercandalli.com.jarvis.net.TaskPost;
import mercandalli.com.jarvis.ui.view.DividerItemDecoration;

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
    private ProgressBar circularProgressBar;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (Application) activity;
    }

    public UserFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_user, container, false);
        this.circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        this.message = (TextView) rootView.findViewById(R.id.message);

        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        this.recyclerView.setHasFixedSize(true);
        this.mLayoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        this.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.GONE);

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

        refreshList();

        return rootView;
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(String search) {
        List<BasicNameValuePair> parameters = null;
        if(this.app.isInternetConnection())
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
        else {
            this.circularProgressBar.setVisibility(View.GONE);
            this.message.setText(getString(R.string.no_internet_connection));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    int i;

    public void updateAdapter() {
        if(this.recyclerView!=null && this.list!=null && this.isAdded()) {
            this.circularProgressBar.setVisibility(View.GONE);

            if(this.list.size()==0) {
                this.message.setText(getString(R.string.no_user));
                this.message.setVisibility(View.VISIBLE);
            }
            else
                this.message.setVisibility(View.GONE);

            this.mAdapter = new AdapterModelUser(app, list, new IModelUserListener() {
                @Override
                public void execute(final ModelUser modelUser) {
                    app.prompt("Send Message", "Write your message", "Send", new IStringListener(){
                        @Override
                        public void execute(String text) {
                            String url = app.getConfig().getUrlServer() + app.getConfig().routeUserConversation + "/" + modelUser.id;
                            List < BasicNameValuePair > parameters = new ArrayList<>();
                            parameters.add(new BasicNameValuePair("message", "" + text));

                            new TaskPost(app, url, new IPostExecuteListener() {
                                @Override
                                public void execute(JSONObject json, String body) {

                                }
                            }, parameters).execute();
                        }
                    }, "Cancel", null);
                }
            });
            this.recyclerView.setAdapter(mAdapter);

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

    @Override
    public boolean back() {
        return false;
    }
}
