package mercandalli.com.jarvis.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.adapter.AdapterModelConnversationMessage;
import mercandalli.com.jarvis.model.ModelConversationMessage;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class ActivityConversation extends Application {

    private String login, password, url;
    private int id_conversation;

    private RecyclerView listView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AdapterModelConnversationMessage adapter;
    private ArrayList<ModelConversationMessage> list = new ArrayList<>();
    private ProgressBar circularProgressBar;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_conversation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e(""+getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }

        this.login = extras.getString("LOGIN");
        this.password = extras.getString("PASSWORD");
        this.id_conversation = extras.getInt("ID_CONVERSATION");
        this.url = this.getConfig().getUrlServer() + this.getConfig().routeUserConversation + "/" + this.id_conversation;

        this.circularProgressBar = (ProgressBar) findViewById(R.id.circulerProgressBar);
        this.message = (TextView) findViewById(R.id.message);

        this.listView = (RecyclerView) findViewById(R.id.listView);
        this.listView.setHasFixedSize(true);
        this.mLayoutManager = new LinearLayoutManager(this);
        this.listView.setLayoutManager(mLayoutManager);

        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        this.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

    }

    @Override
    public void refreshAdapters() {

    }

    @Override
    public void updateAdapters() {
        if(this.listView!=null && this.list!=null) {
            if(this.list.size()==0) {
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

            this.adapter.remplaceList(this.list);

            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(String search) {
        List<BasicNameValuePair> parameters = new ArrayList<>();
        if(search!=null)
            parameters.add(new BasicNameValuePair("search", ""+search));
        parameters.add(new BasicNameValuePair("url", ""+this.url));
        parameters.add(new BasicNameValuePair("all-public", ""+true));

        if(this.isInternetConnection()) {

        }
        else {
            this.circularProgressBar.setVisibility(View.GONE);
            this.message.setText(getString(R.string.no_internet_connection));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
