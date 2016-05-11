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
package com.mercandalli.android.apps.files.user.community;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IModelUserListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskGet;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.common.view.divider.DividerItemDecoration;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.apps.files.user.AdapterModelUser;
import com.mercandalli.android.apps.files.user.UserModel;
import com.mercandalli.android.library.base.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link android.support.v4.app.Fragment} to see all the users.
 */
public class UserFragment extends BackFragment {

    private View rootView;

    private RecyclerView recyclerView;
    private AdapterModelUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<UserModel> list;
    private ProgressBar circularProgressBar;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static UserFragment newInstance() {
        return new UserFragment();
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

        rootView.findViewById(R.id.circle).setVisibility(View.GONE);

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
        if (NetUtils.isInternetConnection(getContext()) && Config.isLogged()) {
            new TaskGet(
                    getContext(),
                    Constants.URL_DOMAIN + Config.ROUTE_USER,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            list = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            UserModel userModel = new UserModel(array.getJSONObject(i));
                                            list.add(userModel);
                                        }
                                    }
                                } else {
                                    Toast.makeText(getContext(), R.string.action_failed, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e(getClass().getName(), "Failed to convert Json", e);
                            }
                            updateAdapter();
                        }
                    },
                    null
            ).execute();
        } else {
            this.circularProgressBar.setVisibility(View.GONE);
            this.message.setText(Config.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    int i;

    public void updateAdapter() {
        if (this.recyclerView != null && this.list != null && this.isAdded()) {
            this.circularProgressBar.setVisibility(View.GONE);

            if (this.list.size() == 0) {
                this.message.setText(getString(R.string.no_user));
                this.message.setVisibility(View.VISIBLE);
            } else {
                this.message.setVisibility(View.GONE);
            }

            this.mAdapter = new AdapterModelUser(list, new IModelUserListener() {
                @Override
                public void execute(final UserModel userModel) {
                    final AlertDialog.Builder menuAleart = new AlertDialog.Builder(getContext());
                    String[] menuList = {getString(R.string.talk)};
                    if (Config.isUserAdmin()) {
                        menuList = new String[]{getString(R.string.talk), getString(R.string.delete)};
                    }
                    menuAleart.setTitle(getString(R.string.action));
                    menuAleart.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            DialogUtils.prompt(
                                                    getContext(),
                                                    "Send Message",
                                                    "Write your message", "Send",
                                                    new DialogUtils.OnDialogUtilsStringListener() {
                                                        @Override
                                                        public void onDialogUtilsStringCalledBack(String text) {
                                                            String url = Constants.URL_DOMAIN + Config.ROUTE_USER_CONVERSATION + "/" + userModel.id;
                                                            List<StringPair> parameters = new ArrayList<>();
                                                            parameters.add(new StringPair("message", "" + text));

                                                            new TaskPost(getActivity(), url, new IPostExecuteListener() {
                                                                @Override
                                                                public void onPostExecute(JSONObject json, String body) {

                                                                }
                                                            }, parameters).execute();
                                                        }
                                                    }, getString(android.R.string.cancel), null);
                                            break;
                                        case 1:
                                            DialogUtils.alert(
                                                    getContext(),
                                                    "Delete " + userModel.username + "?",
                                                    "This process cannot be undone.",
                                                    getString(R.string.delete),
                                                    new DialogUtils.OnDialogUtilsListener() {
                                                        @Override
                                                        public void onDialogUtilsCalledBack() {
                                                            if (Config.isUserAdmin()) {
                                                                userModel.delete(getActivity(), new IPostExecuteListener() {
                                                                    @Override
                                                                    public void onPostExecute(JSONObject json, String body) {
                                                                        UserFragment.this.refreshList();
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(getActivity(), "Not permitted.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }, getString(android.R.string.cancel), null);
                                            break;
                                    }
                                }
                            });
                    AlertDialog menuDrop = menuAleart.create();
                    menuDrop.show();
                }
            });
            this.recyclerView.setAdapter(mAdapter);

            if ((rootView.findViewById(R.id.circle)).getVisibility() == View.GONE) {
                (rootView.findViewById(R.id.circle)).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(getContext(), R.anim.circle_button_bottom_open);
                (rootView.findViewById(R.id.circle)).startAnimation(animOpen);
            }

            (rootView.findViewById(R.id.circle)).setOnClickListener(new View.OnClickListener() {
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
            i = 0;
        }
    }

    @Override
    public boolean back() {
        return false;
    }
}
