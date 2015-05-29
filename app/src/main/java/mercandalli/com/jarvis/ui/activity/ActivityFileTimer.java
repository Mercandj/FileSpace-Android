/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.jarvis.ui.activity;

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
import mercandalli.com.jarvis.util.PointLong;
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
        setContentView(R.layout.activity_file_timer);

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
