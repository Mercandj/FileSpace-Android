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
package com.mercandalli.android.apps.files.admin.notification;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.main.network.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationFragment extends BackFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SendNotificationAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static SendNotificationFragment newInstance() {
        return new SendNotificationFragment();
    }

    private SendNotificationOnlineApi mSendNotificationOnlineApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_admin_send_notification, container, false);

        mSendNotificationOnlineApi =
                RetrofitUtils.getAuthorizedRetrofit().create(SendNotificationOnlineApi.class);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(
                R.id.fragment_admin_send_notification_recycler_view);
        mAdapter = new SendNotificationAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(
                R.id.fragment_admin_send_notification_swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rootView.findViewById(R.id.fragment_admin_send_notification_circle).setOnClickListener(this);

        getDevices();
        return rootView;
    }

    @Override
    public void onRefresh() {
        getDevices();
    }

    @Override
    public boolean back() {
        return false;
    }

    private void getDevices() {
        if (mSendNotificationOnlineApi == null) {
            return;
        }
        final Call<DevicesResponse> callGet = mSendNotificationOnlineApi.getDevice();
        callGet.enqueue(new Callback<DevicesResponse>() {
            @Override
            public void onResponse(
                    final Call<DevicesResponse> call,
                    final Response<DevicesResponse> response) {
                final Context context = getContext();
                if (!response.isSuccessful() || context == null || !(context instanceof Activity)) {
                    return;
                }
                final Activity activity = (Activity) context;
                if (activity.isFinishing()) {
                    return;
                }
                mAdapter.setDeviceList(response.body().getResult(context));
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(
                    final Call<DevicesResponse> call,
                    final Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(final View v) {
        SendNotificationManager.getInstance().onFabClicked(v);
    }
}
