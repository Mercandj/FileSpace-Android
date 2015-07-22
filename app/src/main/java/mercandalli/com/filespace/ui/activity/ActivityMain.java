/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import mercandalli.com.filespace.notificationpush.NotificationPush;
import mercandalli.com.filespace.ui.fragment.file.FileManagerFragment;
import mercandalli.com.filespace.ui.fragment.community.CommunityFragment;

public class ActivityMain extends ApplicationDrawer {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(fragment instanceof FileManagerFragment) {
			FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;					
			fragmentFileManager.refreshListServer();					
		}

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
		if(fragment instanceof FileManagerFragment) {
			FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;					
			fragmentFileManager.updateAdapterListServer();					
		}
		else if(fragment instanceof CommunityFragment) {
            CommunityFragment fragmentTalkManager = (CommunityFragment) fragment;
            fragmentTalkManager.updateAdapterListServer();
		}
	}

	@Override
	public View getFab() {
		if(fragment instanceof FileManagerFragment) {
			FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;
			return fragmentFileManager.getFab();
		}
		return null;
	}

	@Override
	public void refreshAdapters() {
		if(fragment instanceof FileManagerFragment) {
			FileManagerFragment fragmentFileManager = (FileManagerFragment) fragment;					
			fragmentFileManager.refreshAdapterListServer();					
		}
	}
}
