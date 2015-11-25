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
package com.mercandalli.android.filespace.user.community;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.mercandalli.android.filespace.R;
import com.mercandalli.android.filespace.common.listener.IModelUserListener;
import com.mercandalli.android.filespace.common.listener.IPostExecuteListener;
import com.mercandalli.android.filespace.common.listener.IStringListener;
import com.mercandalli.android.filespace.user.ModelConversationUser;
import com.mercandalli.android.filespace.user.ModelUser;
import com.mercandalli.android.filespace.common.net.TaskGet;
import com.mercandalli.android.filespace.common.net.TaskPost;
import com.mercandalli.android.filespace.user.AdapterModelConnversationUser;
import com.mercandalli.android.filespace.common.fragment.BackFragment;
import com.mercandalli.android.filespace.common.view.divider.DividerItemDecoration;
import com.mercandalli.android.filespace.common.util.DialogUtils;
import com.mercandalli.android.filespace.common.util.NetUtils;
import com.mercandalli.android.filespace.common.util.StringPair;

/**
 * Created by Jonathan on 30/03/2015.
 */
public class TalkFragment extends BackFragment {

    private static final String BUNDLE_ARG_TITLE = "TalkFragment.Args.BUNDLE_ARG_TITLE";

    private View rootView;

    private RecyclerView recyclerView;
    private AdapterModelConnversationUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ModelConversationUser> list;
    private ProgressBar circularProgressBar;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static UserFragment newInstance() {
        return new UserFragment();
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
        List<StringPair> parameters = null;
        if (NetUtils.isInternetConnection(mActivity) && mApplicationCallback.isLogged())
            new TaskGet(
                    mActivity,
                    mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUserConversation,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            list = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelConversationUser modelUser = new ModelConversationUser(mActivity, mApplicationCallback, array.getJSONObject(i));
                                            list.add(modelUser);
                                        }
                                    }
                                } else
                                    Toast.makeText(mActivity, mActivity.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
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
            this.message.setText(mApplicationCallback.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    int i;

    public void updateAdapter() {
        if (this.recyclerView != null && this.list != null && this.isAdded()) {
            this.circularProgressBar.setVisibility(View.GONE);

            if (this.list.size() == 0) {
                this.message.setText(getString(R.string.no_talk));
                this.message.setVisibility(View.VISIBLE);
            } else
                this.message.setVisibility(View.GONE);


            this.mAdapter = new AdapterModelConnversationUser(list, new IModelUserListener() {
                @Override
                public void execute(final ModelUser modelUser) {
                    DialogUtils.prompt(mActivity, "Send Message", "Write your message", "Send", new IStringListener() {
                        @Override
                        public void execute(String text) {
                            String url = mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUserMessage + "/" + modelUser.id;
                            List<StringPair> parameters = new ArrayList<>();
                            parameters.add(new StringPair("message", "" + text));

                            new TaskPost(mActivity, mApplicationCallback, url, new IPostExecuteListener() {
                                @Override
                                public void onPostExecute(JSONObject json, String body) {

                                }
                            }, parameters).execute();
                        }
                    }, "Cancel", null);
                }
            });
            this.recyclerView.setAdapter(mAdapter);

            if (rootView.findViewById(R.id.circle).getVisibility() == View.GONE) {
                rootView.findViewById(R.id.circle).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(mActivity, R.anim.circle_button_bottom_open);
                rootView.findViewById(R.id.circle).startAnimation(animOpen);
            }

            rootView.findViewById(R.id.circle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Fab TalkFragment
                    Toast.makeText(getActivity(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                }
            });

            this.mAdapter.setOnItemClickListener(new AdapterModelConnversationUser.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    list.get(position).open();
                }
            });

            this.swipeRefreshLayout.setRefreshing(false);
            i = 0;
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }
}
