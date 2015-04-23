package mercandalli.com.jarvis.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONObject;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.net.TaskGet;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class ActivityFileText extends Application {

    private String initate, url, login, password;
    private boolean online;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file_text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Visibility
        ((EditText) this.findViewById(R.id.txt)).setVisibility(View.GONE);
        ((ProgressBar) this.findViewById(R.id.circulerProgressBar)).setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e(""+getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }
        else {
            this.url = extras.getString("URL_FILE");
            this.login = extras.getString("LOGIN");
            this.password = extras.getString("PASSWORD");
            this.online = extras.getBoolean("ONLINE");

            new TaskGet(this, this.getConfig().getUser(), this.url, new IPostExecuteListener() {
                @Override
                public void execute(JSONObject json, String body) {
                    initate = body;
                    ((EditText) ActivityFileText.this.findViewById(R.id.txt)).setText("" + initate);
                    ((EditText) ActivityFileText.this.findViewById(R.id.txt)).setVisibility(View.VISIBLE);
                    ((ProgressBar) ActivityFileText.this.findViewById(R.id.circulerProgressBar)).setVisibility(View.GONE);
                }
            }).execute();
        }
    }

    @Override
    public void refreshAdapters() {

    }

    @Override
    public void updateAdapters() {

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
