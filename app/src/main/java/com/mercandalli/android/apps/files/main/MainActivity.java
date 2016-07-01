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
package com.mercandalli.android.apps.files.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.main.version.VersionManager;

import static com.mercandalli.android.apps.files.main.FileApp.logPerformance;

/**
 * Main {@link Activity} launched by the xml.
 */
public class MainActivity extends NavDrawerActivity implements
        SetToolbarCallback,
        VersionManager.UpdateCheckedListener {

    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    public static void start(final Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VersionManager.getInstance(this).registerUpdateCheckedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VersionManager.getInstance(this).checkIfUpdateNeeded();
        logPerformance(TAG, "MainActivity#onResume() - End");
    }

    @Override
    protected void onDestroy() {
        VersionManager.getInstance(this).unregisterUpdateCheckedListener(this);
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateNeeded() {
        Toast.makeText(this, R.string.activity_main_toast_update_needed, Toast.LENGTH_SHORT).show();
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
        finish();
    }
}
