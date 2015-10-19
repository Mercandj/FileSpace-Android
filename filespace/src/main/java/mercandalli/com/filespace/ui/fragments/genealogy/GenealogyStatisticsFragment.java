/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.fragments.genealogy;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.models.ModelInformation;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.ui.activities.ApplicationDrawerActivity;
import mercandalli.com.filespace.ui.adapters.AdapterModelInformation;
import mercandalli.com.filespace.ui.fragments.FabFragment;
import mercandalli.com.filespace.utils.NetUtils;
import mercandalli.com.filespace.utils.StringPair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;

/**
 * Created by Jonathan on 28/08/2015.
 */
public class GenealogyStatisticsFragment extends FabFragment {

    private View rootView;

    private RecyclerView recyclerView;
    private AdapterModelInformation mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ModelInformation> list;
    private ProgressBar circularProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static GenealogyStatisticsFragment newInstance() {
        Bundle args = new Bundle();
        GenealogyStatisticsFragment fragment = new GenealogyStatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (ApplicationDrawerActivity) activity;
    }

    @Override
    public void onFabClick(int fab_id, FloatingActionButton fab) {

    }

    @Override
    public boolean isFabVisible(int fab_id) {
        return false;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        return -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_genealogy_statistics, container, false);
        circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

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
        List<StringPair> parameters = null;
        if (NetUtils.isInternetConnection(app))
            new TaskGet(
                    app,
                    this.app.getConfig().getUser(),
                    this.app.getConfig().getUrlServer() + this.app.getConfig().routeGenealogyStatistics,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            list = new ArrayList<ModelInformation>();
                            list.add(new ModelInformation("Genealogy Statistics", Const.TAB_VIEW_TYPE_SECTION));
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelInformation modelFile = new ModelInformation(app, array.getJSONObject(i));
                                            list.add(modelFile);
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
                    parameters
            ).execute();
    }

    public void updateAdapter() {
        if (this.recyclerView != null && this.list != null && this.isAdded()) {
            this.circularProgressBar.setVisibility(View.GONE);

            this.mAdapter = new AdapterModelInformation(app, list);
            this.recyclerView.setAdapter(mAdapter);
            this.recyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());

            this.mAdapter.setOnItemClickListener(new AdapterModelInformation.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });

            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {
        refreshList();
    }
}
