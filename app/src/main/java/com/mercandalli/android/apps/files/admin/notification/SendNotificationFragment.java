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
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.admin.notification.simple.SendNotificationSimpleResponse;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.main.network.RetrofitUtils;
import com.mercandalli.android.library.baselibrary.push.PushManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationFragment extends BackFragment implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    @PushManager.PushType
    private String mCurrentMode = PushManager.PUSH_NOTIFICATION_TYPE_MESSAGE_ONLY;

    public static SendNotificationFragment newInstance() {
        return new SendNotificationFragment();
    }

    private SendNotificationOnlineApi mSendNotificationOnlineApi;
    private EditText mMessageEditText;
    private EditText mTitleEditText;
    private EditText mPackageEditText;
    private EditText mUrlEditText;
    private SwitchCompat mDevSwitchCompat;
    private Spinner mSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_admin_send_notification, container, false);

        mSendNotificationOnlineApi = RetrofitUtils.getRetrofit().create(SendNotificationOnlineApi.class);
        mMessageEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_message);
        mTitleEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_title);
        mPackageEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_package);
        mUrlEditText = (EditText) rootView.findViewById(R.id.fragment_admin_send_notification_url);
        mDevSwitchCompat = (SwitchCompat) rootView.findViewById(R.id.fragment_admin_send_notification_dev_switch);
        mSpinner = (Spinner) rootView.findViewById(R.id.fragment_admin_send_notification_spinner);

        final List<String> list = new ArrayList<>();
        list.add("Simple message");
        list.add("Open play store");
        list.add("Open url");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(this);

        rootView.findViewById(R.id.fragment_admin_send_notification_circle).setOnClickListener(this);
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
            final String packageStr = mPackageEditText.getText().toString();
            final String url = mUrlEditText.getText().toString();
            switch (mCurrentMode) {
                case PushManager.PUSH_NOTIFICATION_TYPE_MESSAGE_ONLY:
                    sendMessageOnly(title, message);
                    break;
                case PushManager.PUSH_NOTIFICATION_TYPE_OPEN_PLAY_STORE:
                    sendMessageOpenStore(title, message, packageStr);
                    break;
                case PushManager.PUSH_NOTIFICATION_TYPE_OPEN_URL:
                    sendMessageOpenUrl(title, message, url);
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
        switch (position) {
            case 0:
                mCurrentMode = PushManager.PUSH_NOTIFICATION_TYPE_MESSAGE_ONLY;
                break;
            case 1:
                mCurrentMode = PushManager.PUSH_NOTIFICATION_TYPE_OPEN_PLAY_STORE;
                break;
            case 2:
                mCurrentMode = PushManager.PUSH_NOTIFICATION_TYPE_OPEN_URL;
                break;
            default:
        }
        resetEditTexts();
    }

    @Override
    public void onNothingSelected(final AdapterView<?> parent) {

    }

    private void sendMessageOnly(final String title, final String message) {
        if (mDevSwitchCompat.isChecked()) {
            mSendNotificationOnlineApi.sendPushToDev(new SendNotificationRequest(
                    PushManager.PUSH_NOTIFICATION_TYPE_MESSAGE_ONLY,
                    title,
                    message,
                    ""
            )).enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(final Call<SendNotificationSimpleResponse> call, final Response<SendNotificationSimpleResponse> response) {
                    resetEditTexts();
                }

                @Override
                public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {
                    resetEditTexts();
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mSendNotificationOnlineApi.sendPushToAll(new SendNotificationRequest(
                    PushManager.PUSH_NOTIFICATION_TYPE_MESSAGE_ONLY,
                    title,
                    message,
                    ""
            )).enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(final Call<SendNotificationSimpleResponse> call, final Response<SendNotificationSimpleResponse> response) {
                    resetEditTexts();
                }

                @Override
                public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {
                    resetEditTexts();
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendMessageOpenStore(final String title, final String message, final String packageStr) {
        if (mDevSwitchCompat.isChecked()) {
            mSendNotificationOnlineApi.sendPushToDev(new SendNotificationRequest(
                    PushManager.PUSH_NOTIFICATION_TYPE_OPEN_PLAY_STORE,
                    title,
                    message,
                    packageStr
            )).enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(final Call<SendNotificationSimpleResponse> call, final Response<SendNotificationSimpleResponse> response) {
                    resetEditTexts();
                }

                @Override
                public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {
                    resetEditTexts();
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mSendNotificationOnlineApi.sendPushToAll(new SendNotificationRequest(
                    PushManager.PUSH_NOTIFICATION_TYPE_OPEN_PLAY_STORE,
                    title,
                    message,
                    packageStr
            )).enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(final Call<SendNotificationSimpleResponse> call, final Response<SendNotificationSimpleResponse> response) {
                    resetEditTexts();
                }

                @Override
                public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {
                    resetEditTexts();
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendMessageOpenUrl(final String title, final String message, final String url) {
        if (mDevSwitchCompat.isChecked()) {
            mSendNotificationOnlineApi.sendPushToDev(new SendNotificationRequest(
                    PushManager.PUSH_NOTIFICATION_TYPE_OPEN_URL,
                    title,
                    message,
                    url
            )).enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(final Call<SendNotificationSimpleResponse> call, final Response<SendNotificationSimpleResponse> response) {
                    resetEditTexts();
                }

                @Override
                public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {
                    resetEditTexts();
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mSendNotificationOnlineApi.sendPushToAll(new SendNotificationRequest(
                    PushManager.PUSH_NOTIFICATION_TYPE_OPEN_URL,
                    title,
                    message,
                    url
            )).enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(final Call<SendNotificationSimpleResponse> call, final Response<SendNotificationSimpleResponse> response) {
                    resetEditTexts();
                }

                @Override
                public void onFailure(final Call<SendNotificationSimpleResponse> call, final Throwable t) {
                    resetEditTexts();
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void resetEditTexts() {
        mTitleEditText.setText("");
        mMessageEditText.setText("");
        mPackageEditText.setText("");
        mUrlEditText.setText("");
        switch (mCurrentMode) {
            case PushManager.PUSH_NOTIFICATION_TYPE_MESSAGE_ONLY:
                mPackageEditText.setVisibility(View.GONE);
                mUrlEditText.setVisibility(View.GONE);
                break;
            case PushManager.PUSH_NOTIFICATION_TYPE_OPEN_PLAY_STORE:
                mPackageEditText.setVisibility(View.VISIBLE);
                mUrlEditText.setVisibility(View.GONE);

                break;
            case PushManager.PUSH_NOTIFICATION_TYPE_OPEN_URL:
                mPackageEditText.setVisibility(View.GONE);
                mUrlEditText.setVisibility(View.VISIBLE);
                break;
        }
    }
}
