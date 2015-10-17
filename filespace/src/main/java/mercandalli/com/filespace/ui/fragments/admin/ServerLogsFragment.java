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
package mercandalli.com.filespace.ui.fragments.admin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.models.ModelUserConnection;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.ui.activities.ApplicationDrawerActivity;
import mercandalli.com.filespace.ui.adapters.AdapterModelUserConnection;
import mercandalli.com.filespace.ui.fragments.BackFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;

import mercandalli.com.filespace.utils.NetUtils;


public class ServerLogsFragment extends BackFragment {

    private View rootView;

    private RecyclerView recyclerView;
    private AdapterModelUserConnection mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ModelUserConnection> list;
    private ProgressBar circularProgressBar;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static ServerLogsFragment newInstance() {
        Bundle args = new Bundle();
        ServerLogsFragment fragment = new ServerLogsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (ApplicationDrawerActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_admin_data, container, false);
        circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        this.message = (TextView) rootView.findViewById(R.id.message);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        refreshList();

        return rootView;
    }


    public void refreshList() {
        if (NetUtils.isInternetConnection(app))
            new TaskGet(
                    app,
                    this.app.getConfig().getUser(),
                    this.app.getConfig().getUrlServer() + this.app.getConfig().routeUserConnection,
                    new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            list = new ArrayList<ModelUserConnection>();

                            try {
                                if (json != null) {
                                    if (json.has("result_count_all"))
                                        list.add(new ModelUserConnection("Server Logs (" + json.getInt("result_count_all") + ")", Const.TAB_VIEW_TYPE_SECTION));
                                    else
                                        list.add(new ModelUserConnection("Server Logs", Const.TAB_VIEW_TYPE_SECTION));

                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        int array_length = array.length();
                                        for (int i = 0; i < array_length; i++) {
                                            list.add(new ModelUserConnection(app, array.getJSONObject(i)));
                                        }
                                    }

                                } else
                                    Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateAdapter();
                        }
                    },
                    null
            ).execute();
        else {
            this.circularProgressBar.setVisibility(View.GONE);
            if (isAdded())
                this.message.setText(app.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    int i;

    public void updateAdapter() {
        if (this.recyclerView != null && this.list != null && this.isAdded()) {
            this.circularProgressBar.setVisibility(View.GONE);

            this.mAdapter = new AdapterModelUserConnection(app, list);
            this.recyclerView.setAdapter(mAdapter);
            this.recyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());

            if (((ImageButton) rootView.findViewById(R.id.circle)).getVisibility() == View.GONE) {
                ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
                ((ImageButton) rootView.findViewById(R.id.circle)).startAnimation(animOpen);
            }

            ((ImageButton) rootView.findViewById(R.id.circle)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.addItem(new ModelUserConnection("Number", "" + i), 0);
                    recyclerView.scrollToPosition(0);
                    i++;
                }
            });

            this.mAdapter.setOnItemClickListener(new AdapterModelUserConnection.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

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
