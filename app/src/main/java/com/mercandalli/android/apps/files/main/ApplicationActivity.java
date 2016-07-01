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
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;

import org.json.JSONObject;

import java.util.List;

import static com.mercandalli.android.apps.files.main.FileApp.logPerformance;

/**
 * Mother class of the {@link Activity} MainActivity.
 */
public abstract class ApplicationActivity extends AppCompatActivity {

    private static final String TAG = "ApplicationActivity";
    public static FileModel sPhotoFile = null;
    public static IListener sPhotoFileListener = null;

    /* OnResult code */
    public static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logPerformance(TAG, "ApplicationActivity#onCreate() - Start");

        Config.getInstance(this);

        logPerformance(TAG, "ApplicationActivity#onCreate() - Start Config");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (sPhotoFile != null && sPhotoFile.getFile() != null) {
                final List<StringPair> parameters = FileManager.getInstance(this).getForUpload(sPhotoFile);
                (new TaskPost(this, Constants.URL_DOMAIN + Config.ROUTE_FILE, new IPostExecuteListener() {
                    @Override
                    public void onPostExecute(JSONObject json, String body) {
                        if (sPhotoFileListener != null) {
                            sPhotoFileListener.execute();
                        }
                    }
                }, parameters, sPhotoFile.getFile())).execute();
            } else {
                Toast.makeText(this, this.getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
