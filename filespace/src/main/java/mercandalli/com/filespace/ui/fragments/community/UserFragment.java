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
package mercandalli.com.filespace.ui.fragments.community;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.listeners.IModelUserListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.listeners.IStringListener;
import mercandalli.com.filespace.models.ModelUser;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activities.ApplicationDrawerActivity;
import mercandalli.com.filespace.ui.adapters.AdapterModelUser;
import mercandalli.com.filespace.ui.fragments.BackFragment;
import mercandalli.com.filespace.ui.views.DividerItemDecoration;
import mercandalli.com.filespace.utils.StringPair;

import static mercandalli.com.filespace.utils.NetUtils.isInternetConnection;

/**
 * Created by Jonathan on 30/03/2015.
 */
public class UserFragment extends BackFragment {

    private View rootView;

    private RecyclerView recyclerView;
    private AdapterModelUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ModelUser> list;
    private ProgressBar circularProgressBar;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static UserFragment newInstance() {
        Bundle args = new Bundle();
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (ApplicationDrawerActivity) activity;
    }

    public UserFragment() {
        super();
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
        if(isInternetConnection(app) && app.isLogged())
            new TaskGet(
                    app,
                    this.app.getConfig().getUser(),
                    this.app.getConfig().getUrlServer() + this.app.getConfig().routeUser,
                    new IPostExecuteListener() {
                        @Override
                        public void execute(JSONObject json, String body) {
                            list = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelUser modelUser = new ModelUser(app, array.getJSONObject(i));
                                            list.add(modelUser);
                                        }
                                    }
                                }
                                else
                                    Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
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
            this.message.setText(app.isLogged()?getString(R.string.no_internet_connection):getString(R.string.no_logged));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    int i;

    public void updateAdapter() {
        if(this.recyclerView!=null && this.list!=null && this.isAdded()) {
            this.circularProgressBar.setVisibility(View.GONE);

            if(this.list.size()==0) {
                this.message.setText(getString(R.string.no_user));
                this.message.setVisibility(View.VISIBLE);
            }
            else
                this.message.setVisibility(View.GONE);

            this.mAdapter = new AdapterModelUser(app, list, new IModelUserListener() {
                @Override
                public void execute(final ModelUser modelUser) {
                    final AlertDialog.Builder menuAleart = new AlertDialog.Builder(app);
                    String[] menuList = { getString(R.string.talk) };
                    if(app.getConfig().isUserAdmin())
                        menuList = new String[]{ getString(R.string.talk), getString(R.string.delete) };
                    menuAleart.setTitle(getString(R.string.action));
                    menuAleart.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            app.prompt("Send Message", "Write your message", "Send", new IStringListener() {
                                                @Override
                                                public void execute(String text) {
                                                    String url = app.getConfig().getUrlServer() + app.getConfig().routeUserConversation + "/" + modelUser.id;
                                                    List<StringPair> parameters = new ArrayList<>();
                                                    parameters.add(new StringPair("message", "" + text));

                                                    new TaskPost(app, url, new IPostExecuteListener() {
                                                        @Override
                                                        public void execute(JSONObject json, String body) {

                                                        }
                                                    }, parameters).execute();
                                                }
                                            }, getString(R.string.cancel), null);
                                            break;
                                        case 1:
                                            app.alert("Delete " + modelUser.username + "?", "This process cannot be undone.", getString(R.string.delete), new IListener() {
                                                @Override
                                                public void execute() {
                                                    if(app.getConfig().isUserAdmin())
                                                        modelUser.delete(new IPostExecuteListener() {
                                                            @Override
                                                            public void execute(JSONObject json, String body) {
                                                                UserFragment.this.refreshList();
                                                            }
                                                        });
                                                    else
                                                        Toast.makeText(getActivity(), "Not permitted.", Toast.LENGTH_SHORT).show();
                                                }
                                            }, getString(R.string.cancel), null);
                                            break;
                                    }
                                }
                            });
                    AlertDialog menuDrop = menuAleart.create();
                    menuDrop.show();
                }
            });
            this.recyclerView.setAdapter(mAdapter);

            if( ((ImageButton) rootView.findViewById(R.id.circle)).getVisibility()==View.GONE ) {
                ((ImageButton) rootView.findViewById(R.id.circle)).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
                ((ImageButton) rootView.findViewById(R.id.circle)).startAnimation(animOpen);
            }

            ((ImageButton) rootView.findViewById(R.id.circle)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Fab UserFragment
                    Toast.makeText(getActivity(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                }
            });

            this.mAdapter.setOnItemClickListener(new AdapterModelUser.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });

            this.swipeRefreshLayout.setRefreshing(false);
            i=0;
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
