package mercandalli.com.jarvis.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.library.PointLong;
import mercandalli.com.jarvis.model.ModelFileContent;

/**
 * Created by Jonathan on 09/05/2015.
 */
public class ActivityFileTimer extends Application {

    private String initate, url, login;
    private boolean online;
    public Date timer_date;
    ModelFileContent modelFileContent;
    TextView txt, second;
    Runnable runnable;
    Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file_timer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Visibility
        ((ProgressBar) this.findViewById(R.id.circulerProgressBar)).setVisibility(View.GONE);
        this.txt = (TextView) ActivityFileTimer.this.findViewById(R.id.txt);
        this.second = (TextView) ActivityFileTimer.this.findViewById(R.id.second);

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
            this.online = extras.getBoolean("ONLINE");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                this.timer_date = dateFormat.parse("" + extras.getString("TIMER_DATE"));
                modelFileContent = new ModelFileContent(this, timer_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if(modelFileContent != null) {
                        txt.setText(modelFileContent.toString());
                        PointLong diff = modelFileContent.diffSecond();
                        if(diff.y<0)
                            diff.y=-diff.y;
                        second.setText(diff.x+" : "+((diff.y<10)?"0":"")+diff.y);
                    }

                    //also call the same runnable
                    handler.postDelayed(this, 50);
                }
            };
            runnable.run();
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
                if(runnable != null)
                    handler.removeCallbacksAndMessages(runnable);
                this.finish();
                this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(runnable != null)
                handler.removeCallbacksAndMessages(runnable);
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
