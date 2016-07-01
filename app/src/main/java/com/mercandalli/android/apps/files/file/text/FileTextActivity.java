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
package com.mercandalli.android.apps.files.file.text;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskGet;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.main.ApplicationActivity;

import org.json.JSONObject;

/**
 * An {@link Activity} for displaying the text file: online or offline.
 */
public class FileTextActivity extends ApplicationActivity implements IPostExecuteListener {

    @NonNull
    private static final String EXTRA_MODEL_FILE_URL = "FileTextActivity.Extra.EXTRA_MODEL_FILE_URL";
    @NonNull
    private static final String EXTRA_MODEL_FILE_ONLINE = "FileTextActivity.Extra.EXTRA_MODEL_FILE_ONLINE";
    @NonNull
    private static final String EXTRA_MODEL_FILE_ARTICLE_CONTENT_1 = "FileTextActivity.Extra.EXTRA_MODEL_FILE_ARTICLE_CONTENT_1";

    @NonNull
    private static final String KEY_SAVED_TEXT = "FileTextActivity.Saved.mInitialText";

    private String mInitialText;
    private String mUrl;
    private boolean mIsOnline;

    private EditText mEditText;
    private ProgressBar mProgressBar;

    public static void start(
            @NonNull final Activity activity,
            final FileModel fileModel,
            final boolean isOnline) {
        final Intent intent = new Intent(activity, FileTextActivity.class);
        intent.putExtra(EXTRA_MODEL_FILE_URL, "" + fileModel.getOnlineUrl());

        if (FileTypeModelENUM.FILESPACE.type.equals(fileModel.getType())) {
            intent.putExtra(EXTRA_MODEL_FILE_ARTICLE_CONTENT_1, "" + fileModel.getContent().getArticle().article_content_1);
        }

        intent.putExtra(EXTRA_MODEL_FILE_ONLINE, isOnline);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_text);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mEditText = (EditText) findViewById(R.id.txt);
        mProgressBar = (ProgressBar) findViewById(R.id.circularProgressBar);

        // Visibility
        mEditText.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        final Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }
        if (extras.containsKey(EXTRA_MODEL_FILE_ARTICLE_CONTENT_1)) {
            mInitialText = extras.getString(EXTRA_MODEL_FILE_ARTICLE_CONTENT_1);
            mEditText.setText(mInitialText);
            mEditText.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mUrl = extras.getString(EXTRA_MODEL_FILE_URL);
            mIsOnline = extras.getBoolean(EXTRA_MODEL_FILE_ONLINE);

            if (savedInstanceState == null) {
                new TaskGet(this, this.mUrl, this).execute();
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(KEY_SAVED_TEXT, mInitialText);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mInitialText = savedInstanceState.getString(KEY_SAVED_TEXT);
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

    @Override
    public void onPostExecute(JSONObject json, String body) {
        mInitialText = body;
        mEditText.setText(mInitialText);
        mEditText.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
}