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
package com.mercandalli.android.apps.files.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mercandalli.android.apps.files.common.fragment.FabFragment;
import com.mercandalli.android.apps.files.common.listener.SetToolbarCallback;
import com.mercandalli.android.apps.files.file.local.FileLocalPagerFragment;
import com.mercandalli.android.apps.files.notificationpush.NotificationPush;
import com.mercandalli.android.apps.files.user.community.CommunityFragment;

/**
 * Main {@link Activity} launched by the xml.
 */
public class MainActivity extends NavDrawerActivity implements SetToolbarCallback, FabFragment.RefreshFabCallback {

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notification
        if (TextUtils.isEmpty(NotificationPush.regId)) {
            NotificationPush.regId = NotificationPush.registerGCM(this);
            Log.d("ActivityMain", "GCM RegId: " + NotificationPush.regId);
        } else {
            Log.d("ActivityMain", "Already Registered with GCM Server!");
            NotificationPush.mainActNotif(this);
        }

        // getDevice(this);
    }

    @Override
    public void updateAdapters() {
        if (mBackFragment instanceof FileLocalPagerFragment) {
            FileLocalPagerFragment fragmentFileManager = (FileLocalPagerFragment) mBackFragment;
            fragmentFileManager.updateAdapterListServer();
        } else if (mBackFragment instanceof CommunityFragment) {
            CommunityFragment fragmentTalkManager = (CommunityFragment) mBackFragment;
            fragmentTalkManager.updateAdapterListServer();
        }
    }

    @Override
    public void refreshData() {
        if (mBackFragment instanceof FileLocalPagerFragment) {
            FileLocalPagerFragment fragmentFileManager = (FileLocalPagerFragment) mBackFragment;
            fragmentFileManager.refreshData();
        }
    }

    @Override
    public void onRefreshFab() {
        if (getBackFragment() instanceof FabFragment.RefreshFabCallback) {
            ((FabFragment.RefreshFabCallback) getBackFragment()).onRefreshFab();
        }
    }
}
