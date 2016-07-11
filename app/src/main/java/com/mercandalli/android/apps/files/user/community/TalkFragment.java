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
package com.mercandalli.android.apps.files.user.community;

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
import com.mercandalli.android.apps.files.user.AdapterModelConversationUser;
import com.mercandalli.android.apps.files.user.ConversationUserModel;
import com.mercandalli.android.apps.files.user.UserModel;
import com.mercandalli.android.library.base.dialog.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 30/03/2015.
 */
public class TalkFragment extends BackFragment {

    private View mRootView;

    private RecyclerView mRecyclerView;
    private final List<ConversationUserModel> mConversationUserModels = new ArrayList<>();
    private ProgressBar mCircularProgressBar;
    private TextView mMessageTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_user, container, false);
        mCircularProgressBar = (ProgressBar) mRootView.findViewById(R.id.circularProgressBar);
        mMessageTextView = (TextView) mRootView.findViewById(R.id.message);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRootView.findViewById(R.id.circle).setVisibility(View.GONE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        refreshList();

        return mRootView;
    }

    public void refreshList() {
        if (NetUtils.isInternetConnection(getContext()) && Config.isLogged()) {
            new TaskGet(
                    getActivity(),
                    Constants.URL_DOMAIN + Config.ROUTE_USER_CONVERSATION,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            mConversationUserModels.clear();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            mConversationUserModels.add(
                                                    new ConversationUserModel(array.getJSONObject(i)));
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
            mCircularProgressBar.setVisibility(View.GONE);
            mMessageTextView.setText(Config.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            mMessageTextView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void updateAdapter() {
        if (mRecyclerView != null && this.isAdded()) {
            mCircularProgressBar.setVisibility(View.GONE);

            if (mConversationUserModels.isEmpty()) {
                mMessageTextView.setText(getString(R.string.no_talk));
                mMessageTextView.setVisibility(View.VISIBLE);
            } else {
                mMessageTextView.setVisibility(View.GONE);
            }

            final AdapterModelConversationUser adapter = new AdapterModelConversationUser(mConversationUserModels, new IModelUserListener() {
                @Override
                public void execute(final UserModel userModel) {
                    DialogUtils.prompt(
                            getContext(),
                            "Send Message",
                            "Write your message",
                            "Send",
                            new DialogUtils.OnDialogUtilsStringListener() {
                                @Override
                                public void onDialogUtilsStringCalledBack(String text) {
                                    String url = Constants.URL_DOMAIN + Config.ROUTE_USER_MESSAGE + "/" + userModel.getId();
                                    List<StringPair> parameters = new ArrayList<>();
                                    parameters.add(new StringPair("message", "" + text));

                                    new TaskPost(getActivity(), url, new IPostExecuteListener() {
                                        @Override
                                        public void onPostExecute(JSONObject json, String body) {

                                        }
                                    }, parameters).execute();
                                }
                            }, "Cancel", null);
                }
            });
            mRecyclerView.setAdapter(adapter);

            if (mRootView.findViewById(R.id.circle).getVisibility() == View.GONE) {
                mRootView.findViewById(R.id.circle).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(getContext(), R.anim.circle_button_bottom_open);
                mRootView.findViewById(R.id.circle).startAnimation(animOpen);
            }

            mRootView.findViewById(R.id.circle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Fab TalkFragment
                    Toast.makeText(getActivity(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                }
            });

            adapter.setOnItemClickListener(new AdapterModelConversationUser.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    mConversationUserModels.get(position).open(getActivity());
                }
            });

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean back() {
        return false;
    }
}
