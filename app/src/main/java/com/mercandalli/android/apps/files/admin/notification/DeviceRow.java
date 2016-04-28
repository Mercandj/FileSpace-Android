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

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.library.baselibrary.device.Device;

import static com.mercandalli.android.library.baselibrary.view.ViewUtils.dpToPx;

/* package */ class DeviceRow extends CardView implements View.OnClickListener {

    private TextView mTitle;
    private TextView mSubtitle;
    @Nullable
    private Device mDevice;

    public DeviceRow(final Context context) {
        super(context);
        init(context);
    }

    public DeviceRow(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DeviceRow(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.fragment_admin_device, this);
        final LayoutParams layoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int margin = (int) dpToPx(4);
        final int padding = 4 * margin;
        setMinimumHeight((int) dpToPx(76));
        layoutParams.setMargins(3 * margin, margin, 3 * margin, margin);
        setLayoutParams(layoutParams);
        setPadding(padding, 4 * padding, padding, 4 * padding);
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.tab_file));
        mTitle = (TextView) findViewById(R.id.fragment_admin_device_title);
        mSubtitle = (TextView) findViewById(R.id.fragment_admin_device_subtitle);

        findViewById(R.id.fragment_admin_device_more).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.fragment_admin_device_more:
                if (mDevice == null || mDevice.getId() == null) {
                    return;
                }
                DialogUtils.alert(
                        getContext(),
                        "Delete",
                        "Delete " + mDevice.getAndroidDeviceBuildModel() + " - " +
                                mDevice.getAndroidAppVersionName() + "(" +
                                mDevice.getAndroidAppVersionCode() + ") ?",
                        getResources().getString(android.R.string.yes),
                        new IListener() {
                            @Override
                            public void execute() {
                                SendNotificationManager.getInstance().delete(mDevice);
                            }
                        },
                        getResources().getString(android.R.string.no),
                        null);
                break;
        }
    }

    public void setDevice(@Nullable final Device device) {
        mDevice = device;
        if (device == null) {
            return;
        }
        mTitle.setText(device.getAndroidDeviceBuildModel() + ", " + device.getAndroidDeviceManufacturer() + ", " +
                device.getAndroidDeviceYear());
        mSubtitle.setText(device.getAndroidAppVersionName() + "(" + device.getAndroidAppVersionCode() + "), " +
                device.getAndroidDeviceVersionSdk() + ", " + device.getAndroidDeviceLanguage());
    }
}
