/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONObject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.net.TaskGet;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class FileTextActivity extends ApplicationActivity {

    private String initate, url, login, password;
    private boolean online;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Visibility
        ((EditText) this.findViewById(R.id.txt)).setVisibility(View.GONE);
        ((ProgressBar) this.findViewById(R.id.circularProgressBar)).setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("" + getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        } else if (extras.containsKey("ARTICLE_CONTENT_1")) {
            initate = extras.getString("ARTICLE_CONTENT_1");
            ((EditText) FileTextActivity.this.findViewById(R.id.txt)).setText("" + initate);
            ((EditText) FileTextActivity.this.findViewById(R.id.txt)).setVisibility(View.VISIBLE);
            ((ProgressBar) FileTextActivity.this.findViewById(R.id.circularProgressBar)).setVisibility(View.GONE);
        } else {
            this.url = extras.getString("URL_FILE");
            this.login = extras.getString("LOGIN");
            this.password = extras.getString("PASSWORD");
            this.online = extras.getBoolean("ONLINE");

            new TaskGet(this, this.getConfig().getUser(), this.url, new IPostExecuteListener() {
                @Override
                public void execute(JSONObject json, String body) {
                    initate = body;
                    ((EditText) FileTextActivity.this.findViewById(R.id.txt)).setText("" + initate);
                    ((EditText) FileTextActivity.this.findViewById(R.id.txt)).setVisibility(View.VISIBLE);
                    ((ProgressBar) FileTextActivity.this.findViewById(R.id.circularProgressBar)).setVisibility(View.GONE);
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