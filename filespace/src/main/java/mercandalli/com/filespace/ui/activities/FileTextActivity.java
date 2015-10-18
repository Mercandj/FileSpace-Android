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

import android.content.Intent;
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
import mercandalli.com.filespace.models.ModelFile;
import mercandalli.com.filespace.models.ModelFileTypeENUM;
import mercandalli.com.filespace.net.TaskGet;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class FileTextActivity extends ApplicationActivity {

    private static final String EXTRA_MODEL_FILE_URL = "FileTextActivity.Extra.EXTRA_MODEL_FILE_URL";
    private static final String EXTRA_MODEL_FILE_ONLINE = "FileTextActivity.Extra.EXTRA_MODEL_FILE_ONLINE";
    private static final String EXTRA_MODEL_FILE_ARTICLE_CONTENT_1 = "FileTextActivity.Extra.EXTRA_MODEL_FILE_ARTICLE_CONTENT_1";

    private String mInitate;
    private String mUrl;
    private boolean online;

    public static void startForSelection(ApplicationActivity app, final ModelFile modelFile, boolean isOnline) {
        final Intent intent = new Intent(app, FileTextActivity.class);
        intent.putExtra(EXTRA_MODEL_FILE_URL, "" + modelFile.onlineUrl);
        if (modelFile.type.equals(ModelFileTypeENUM.FILESPACE.type))
            intent.putExtra(EXTRA_MODEL_FILE_ARTICLE_CONTENT_1, "" + modelFile.content.article.article_content_1);
        intent.putExtra(EXTRA_MODEL_FILE_ONLINE, isOnline);
        app.startActivity(intent);
        app.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_text);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Visibility
        ((EditText) findViewById(R.id.txt)).setVisibility(View.GONE);
        ((ProgressBar) findViewById(R.id.circularProgressBar)).setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("" + getClass().getName(), "extras == null");
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        } else if (extras.containsKey(EXTRA_MODEL_FILE_ARTICLE_CONTENT_1)) {
            mInitate = extras.getString(EXTRA_MODEL_FILE_ARTICLE_CONTENT_1);
            ((EditText) findViewById(R.id.txt)).setText("" + mInitate);
            findViewById(R.id.txt).setVisibility(View.VISIBLE);
            findViewById(R.id.circularProgressBar).setVisibility(View.GONE);
        } else {
            this.mUrl = extras.getString(EXTRA_MODEL_FILE_URL);
            this.online = extras.getBoolean(EXTRA_MODEL_FILE_ONLINE);

            new TaskGet(this, this.getConfig().getUser(), this.mUrl, new IPostExecuteListener() {
                @Override
                public void execute(JSONObject json, String body) {
                    mInitate = body;
                    ((EditText) FileTextActivity.this.findViewById(R.id.txt)).setText("" + mInitate);
                    FileTextActivity.this.findViewById(R.id.txt).setVisibility(View.VISIBLE);
                    FileTextActivity.this.findViewById(R.id.circularProgressBar).setVisibility(View.GONE);
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