/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p>
 * LICENSE:
 * <p>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.user;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskGet;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.view.divider.DividerItemDecoration;
import com.mercandalli.android.apps.files.main.ApplicationActivity;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class ConversationActivity extends ApplicationActivity {

    private String mUrl;
    private String mIdConversation;

    private RecyclerView mRecyclerView;
    private AdapterModelConversationMessage mAdapterModelConversationMessage;
    private final List<UserConversationMessageModel> mUserConversationMessageModels = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mMessageTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText mInputEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("" + getClass().getName(), "extras == null");
            this.finish();
            this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }

        mInputEditText = (EditText) findViewById(R.id.input);

        mIdConversation = extras.getString("ID_CONVERSATION");
        mUrl = Constants.URL_DOMAIN + Config.ROUTE_USER_MESSAGE + "/" + mIdConversation;

        mProgressBar = (ProgressBar) findViewById(R.id.circularProgressBar);
        mMessageTextView = (TextView) findViewById(R.id.message);

        mRecyclerView = (RecyclerView) findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        mAdapterModelConversationMessage = new AdapterModelConversationMessage(mUserConversationMessageModels, null);
        mRecyclerView.setAdapter(mAdapterModelConversationMessage);
        mRecyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapterModelConversationMessage.setOnItemClickListener(new AdapterModelConversationMessage.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        mAdapterModelConversationMessage.setOnItemLongClickListener(new AdapterModelConversationMessage.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {

                return true;
            }
        });

        mInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String url = Constants.URL_DOMAIN + Config.ROUTE_USER_MESSAGE + "/" + mIdConversation;
                    List<StringPair> parameters = new ArrayList<>();
                    parameters.add(new StringPair("message", "" + mInputEditText.getText().toString()));
                    mInputEditText.setText("");

                    new TaskPost(ConversationActivity.this, ConversationActivity.this, url, new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            refreshList();
                        }
                    }, parameters).execute();

                    return true;
                }
                return false;
            }
        });

        refreshList();
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void updateAdapters() {
        if (mRecyclerView != null) {
            if (mUserConversationMessageModels.size() == 0) {
                if (mUrl == null) {
                    mMessageTextView.setText(getString(R.string.no_file_server));
                } else if (mUrl.equals("")) {
                    mMessageTextView.setText(getString(R.string.no_file_server));
                } else {
                    mMessageTextView.setText(getString(R.string.no_file_directory));
                }
                mMessageTextView.setVisibility(View.VISIBLE);
            } else {
                mMessageTextView.setVisibility(View.GONE);
            }

            mAdapterModelConversationMessage.replaceList(mUserConversationMessageModels);
            mRecyclerView.scrollToPosition(mUserConversationMessageModels.size() - 1);

            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(String search) {
        if (mUrl == null) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return;
        }

        List<StringPair> parameters = null;
        if (NetUtils.isInternetConnection(this)) {
            new TaskGet(
                    this,
                    this.mUrl,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            mUserConversationMessageModels.clear();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            UserConversationMessageModel modelFile = new UserConversationMessageModel(ConversationActivity.this, ConversationActivity.this, array.getJSONObject(i));
                                            mUserConversationMessageModels.add(modelFile);
                                        }
                                    }
                                } else {
                                    Toast.makeText(ConversationActivity.this, ConversationActivity.this.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e(getClass().getName(), "Failed to convert Json", e);
                            }
                            updateAdapters();
                        }
                    },
                    parameters
            ).execute();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mMessageTextView.setText(getString(R.string.no_internet_connection));
            mMessageTextView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
