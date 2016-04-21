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
package com.mercandalli.android.apps.files.admin.notification;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.admin.notification.simple.SendNotificationSimpleDevRequest;
import com.mercandalli.android.apps.files.admin.notification.simple.SendNotificationSimpleOnlineApi;
import com.mercandalli.android.apps.files.admin.notification.simple.SendNotificationSimpleRequest;
import com.mercandalli.android.apps.files.admin.notification.simple.SendNotificationSimpleResponse;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.main.network.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationFragment extends BackFragment implements View.OnClickListener {

    public static SendNotificationFragment newInstance() {
        return new SendNotificationFragment();
    }

    private SendNotificationSimpleOnlineApi mSendNotificationSimpleOnlineApi;
    private EditText mMessageEditText;
    private EditText mTitleEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_admin_send_notification, container, false);

        mSendNotificationSimpleOnlineApi = RetrofitUtils.getRetrofit().create(SendNotificationSimpleOnlineApi.class);
        mMessageEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_title);
        mTitleEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_message);

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
        if (viewId == R.id.fragment_admin_send_notification_circle) {

            final String title = mTitleEditText.getText().toString();
            final String message = mMessageEditText.getText().toString();


        }
    }
}
