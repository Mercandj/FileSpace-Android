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
package mercandalli.com.filespace.ui.activitiy;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import javax.inject.Inject;

import mercandalli.com.filespace.config.MyApp;
import mercandalli.com.filespace.listener.SetToolbarCallback;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.notificationpush.NotificationPush;
import mercandalli.com.filespace.ui.fragment.community.CommunityFragment;
import mercandalli.com.filespace.ui.fragment.file.FileFragment;

/**
 * Main {@link Activity} launched by the xml.
 */
public class MainActivity extends ApplicationDrawerActivity implements SetToolbarCallback {

    @Inject
    FileManager mFileManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dagger
        MyApp.get(this).getAppComponent().inject(this);

        // Notif
        if (TextUtils.isEmpty(NotificationPush.regId)) {
            NotificationPush.regId = NotificationPush.registerGCM(this);
            Log.d("ActivityMain", "GCM RegId: " + NotificationPush.regId);
        } else {
            Log.d("ActivityMain", "Already Registered with GCM Server!");
            NotificationPush.mainActNotif(this);
        }
    }

    @Override
    public void updateAdapters() {
        if (mBackFragment instanceof FileFragment) {
            FileFragment fragmentFileManager = (FileFragment) mBackFragment;
            fragmentFileManager.updateAdapterListServer();
        } else if (mBackFragment instanceof CommunityFragment) {
            CommunityFragment fragmentTalkManager = (CommunityFragment) mBackFragment;
            fragmentTalkManager.updateAdapterListServer();
        }
    }

    @Override
    public void refreshData() {
        if (mBackFragment instanceof FileFragment) {
            FileFragment fragmentFileManager = (FileFragment) mBackFragment;
            fragmentFileManager.refreshData();
        }
    }
}
