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
package com.mercandalli.android.apps.files.admin.notification.simple;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.main.network.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationSimpleFragment extends BackFragment implements View.OnClickListener {

    private static final String INIT_GCM = "fslDUi5sxzk:APA91bHeujPF2PSUdeDjsalKdMlIZfsuQYyALRGXPHVN_IJypuSPFi1RTjw1q61anFaNetqPuOKQgYh0kQW2jhZaBOTKkMyFzQtSvfl2c_w1QG0DFUxUfZQQhuiOjo-IUUu4UKEOR8J9";
    private static final String INIT_API = "AIzaSyD7zDFkA8VEOs4egESBEy-sHHBqyEZj3WU";

    public static SendNotificationSimpleFragment newInstance() {
        return new SendNotificationSimpleFragment();
    }

    private SendNotificationSimpleOnlineApi mSendNotificationSimpleOnlineApi;
    private EditText mMessageEditText;
    private EditText mGcmEditText;
    private EditText mApiEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_admin_send_simple_notification, container, false);

        mSendNotificationSimpleOnlineApi = RetrofitUtils.getRetrofit().create(SendNotificationSimpleOnlineApi.class);
        mMessageEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_simple_message);
        mGcmEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_simple_gcm);
        mApiEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_simple_api);

        mGcmEditText.setText(INIT_GCM);
        mApiEditText.setText(INIT_API);
        rootView.findViewById(R.id.fragment_admin_send_notification_simple_circle).setOnClickListener(this);

        return rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onClick(final View v) {
        final int viewId = v.getId();
        if (viewId == R.id.fragment_admin_send_notification_simple_circle) {

            final String gcmId = mGcmEditText.getText().toString();
            final String googleApiKey = mApiEditText.getText().toString();
            final String pushMessage = mMessageEditText.getText().toString();

            if (TextUtils.isEmpty(gcmId) && TextUtils.isEmpty(googleApiKey)) {
                final Call<SendNotificationSimpleResponse> call = mSendNotificationSimpleOnlineApi.sendPushToDev(
                        new SendNotificationSimpleDevRequest(pushMessage));
                call.enqueue(new Callback<SendNotificationSimpleResponse>() {
                    @Override
                    public void onResponse(final Call<SendNotificationSimpleResponse> call, final Response<SendNotificationSimpleResponse> response) {
                        mMessageEditText.setText("");
                    }

                    @Override
                    public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {
                        mMessageEditText.setText("");
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            final Call<SendNotificationSimpleResponse> call = mSendNotificationSimpleOnlineApi.sendPush(new SendNotificationSimpleRequest(
                    gcmId,
                    googleApiKey,
                    pushMessage));
            call.enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(final Call<SendNotificationSimpleResponse> call, final Response<SendNotificationSimpleResponse> response) {
                    mMessageEditText.setText("");
                }

                @Override
                public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {
                    mMessageEditText.setText("");
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}