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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.admin.notification.simple.SendNotificationSimpleResponse;
import com.mercandalli.android.apps.files.main.network.RetrofitUtils;
import com.mercandalli.android.library.base.push.PushManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mercandalli.android.library.base.view.ViewUtils.dpToPx;

public class SendNotificationHeaderView extends CardView implements
        AdapterView.OnItemSelectedListener,
        SendNotificationManager.OnFabClicked {

    @PushManager.PushType
    private String mCurrentMode = PushManager.PUSH_TYPE_NOTIFICATION_MESSAGE;

    private SendNotificationOnlineApi mSendNotificationOnlineApi;
    private EditText mMessageEditText;
    private EditText mTitleEditText;
    private EditText mPackageEditText;
    private EditText mUrlEditText;
    private SwitchCompat mDevSwitchCompat;

    public SendNotificationHeaderView(final Context context) {
        super(context);
        init(context);
    }

    public SendNotificationHeaderView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SendNotificationHeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.fragment_admin_send_notification_header, this);
        final LayoutParams layoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) dpToPx(12), (int) dpToPx(12), (int) dpToPx(12), (int) dpToPx(4));
        setLayoutParams(layoutParams);
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.tab_file));

        mSendNotificationOnlineApi =
                RetrofitUtils.getAuthorizedRetrofit().create(SendNotificationOnlineApi.class);

        mMessageEditText = (EditText) findViewById(
                R.id.fragment_admin_send_notification_message);
        mTitleEditText = (EditText) findViewById(
                R.id.fragment_admin_send_notification_title);
        mPackageEditText = (EditText) findViewById(
                R.id.fragment_admin_send_notification_package);
        mUrlEditText = (EditText) findViewById(
                R.id.fragment_admin_send_notification_url);
        mDevSwitchCompat = (SwitchCompat) findViewById(
                R.id.fragment_admin_send_notification_dev_switch);
        final Spinner spinner = (Spinner) findViewById(
                R.id.fragment_admin_send_notification_spinner);

        final List<String> list = new ArrayList<>();
        list.add("Notif message");
        list.add("Notif PlayStore");
        list.add("Notif Url");
        list.add("PlayStore");
        list.add("Url");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        SendNotificationManager.getInstance().setOnFabClicked(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        SendNotificationManager.getInstance().setOnFabClicked(null);
        super.onDetachedFromWindow();
    }

    @Override
    public void onFabClicked(final View v) {
        final int viewId = v.getId();
        if (viewId == R.id.fragment_admin_send_notification_circle) {
            final String title = mTitleEditText.getText().toString();
            final String message = mMessageEditText.getText().toString();
            final String packageStr = mPackageEditText.getText().toString();
            final String url = mUrlEditText.getText().toString();
            switch (mCurrentMode) {
                case PushManager.PUSH_TYPE_NOTIFICATION_MESSAGE:
                    sendNotificationMessage(title, message);
                    break;
                case PushManager.PUSH_TYPE_NOTIFICATION_OPEN_PLAY_STORE:
                    sendNotificationOpenStore(title, message, packageStr);
                    break;
                case PushManager.PUSH_TYPE_NOTIFICATION_OPEN_URL:
                    sendNotificationOpenUrl(title, message, url);
                    break;
                case PushManager.PUSH_TYPE_OPEN_PLAY_STORE:
                    sendOpenStore(packageStr);
                    break;
                case PushManager.PUSH_TYPE_OPEN_URL:
                    sendOpenUrl(url);
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(
            final AdapterView<?> parent,
            final View view,
            final int position,
            final long id) {
        switch (position) {
            case 0:
                mCurrentMode = PushManager.PUSH_TYPE_NOTIFICATION_MESSAGE;
                break;
            case 1:
                mCurrentMode = PushManager.PUSH_TYPE_NOTIFICATION_OPEN_PLAY_STORE;
                break;
            case 2:
                mCurrentMode = PushManager.PUSH_TYPE_NOTIFICATION_OPEN_URL;
                break;
            case 3:
                mCurrentMode = PushManager.PUSH_TYPE_OPEN_PLAY_STORE;
                break;
            case 4:
                mCurrentMode = PushManager.PUSH_TYPE_OPEN_URL;
                break;
            default:
        }
        resetEditTexts();
    }

    @Override
    public void onNothingSelected(final AdapterView<?> parent) {

    }

    private void sendNotificationMessage(final String title, final String message) {
        sendToApi(
                PushManager.PUSH_TYPE_NOTIFICATION_MESSAGE,
                title,
                message,
                "");
    }

    private void sendNotificationOpenStore(
            final String title,
            final String message,
            final String packageStr) {
        sendToApi(
                PushManager.PUSH_TYPE_NOTIFICATION_OPEN_PLAY_STORE,
                title,
                message,
                packageStr);
    }

    private void sendNotificationOpenUrl(
            final String title,
            final String message,
            final String url) {
        sendToApi(
                PushManager.PUSH_TYPE_NOTIFICATION_OPEN_URL,
                title,
                message,
                url);
    }

    private void sendOpenStore(final String packageStr) {
        sendToApi(
                PushManager.PUSH_TYPE_OPEN_PLAY_STORE,
                "",
                "",
                packageStr);
    }

    private void sendOpenUrl(final String url) {
        sendToApi(
                PushManager.PUSH_TYPE_OPEN_URL,
                "",
                "",
                url);
    }

    private void sendToApi(
            @PushManager.PushType final String type,
            final String title,
            final String message,
            final String actionData) {
        if (mDevSwitchCompat.isChecked()) {
            mSendNotificationOnlineApi.sendPushToDev(new SendNotificationRequest(
                    type,
                    title,
                    message,
                    actionData
            )).enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(
                        final Call<SendNotificationSimpleResponse> call,
                        final Response<SendNotificationSimpleResponse> response) {
                    resetEditTexts();
                }

                @Override
                public void onFailure(
                        final Call<SendNotificationSimpleResponse> call,
                        final Throwable t) {
                    resetEditTexts();
                    Toast.makeText(getContext(), "Failed " + type, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mSendNotificationOnlineApi.sendPushToAll(new SendNotificationRequest(
                    type,
                    title,
                    message,
                    actionData
            )).enqueue(new Callback<SendNotificationSimpleResponse>() {
                @Override
                public void onResponse(
                        final Call<SendNotificationSimpleResponse> call,
                        final Response<SendNotificationSimpleResponse> response) {
                    resetEditTexts();
                }

                @Override
                public void onFailure(
                        final Call<SendNotificationSimpleResponse> call,
                        final Throwable t) {
                    resetEditTexts();
                    Toast.makeText(getContext(), "Failed " + type, Toast.LENGTH_SHORT).show();
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
            case PushManager.PUSH_TYPE_NOTIFICATION_MESSAGE:
                mTitleEditText.setVisibility(View.VISIBLE);
                mMessageEditText.setVisibility(View.VISIBLE);
                mPackageEditText.setVisibility(View.GONE);
                mUrlEditText.setVisibility(View.GONE);
                break;
            case PushManager.PUSH_TYPE_NOTIFICATION_OPEN_PLAY_STORE:
                mTitleEditText.setVisibility(View.VISIBLE);
                mMessageEditText.setVisibility(View.VISIBLE);
                mPackageEditText.setVisibility(View.VISIBLE);
                mUrlEditText.setVisibility(View.GONE);
                break;
            case PushManager.PUSH_TYPE_NOTIFICATION_OPEN_URL:
                mTitleEditText.setVisibility(View.VISIBLE);
                mMessageEditText.setVisibility(View.VISIBLE);
                mPackageEditText.setVisibility(View.GONE);
                mUrlEditText.setVisibility(View.VISIBLE);
                break;
            case PushManager.PUSH_TYPE_OPEN_PLAY_STORE:
                mTitleEditText.setVisibility(View.GONE);
                mMessageEditText.setVisibility(View.GONE);
                mPackageEditText.setVisibility(View.VISIBLE);
                mUrlEditText.setVisibility(View.GONE);
                break;
            case PushManager.PUSH_TYPE_OPEN_URL:
                mTitleEditText.setVisibility(View.GONE);
                mMessageEditText.setVisibility(View.GONE);
                mPackageEditText.setVisibility(View.GONE);
                mUrlEditText.setVisibility(View.VISIBLE);
                break;
        }
    }
}
