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
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Mother class of the {@link Activity} MainActivity.
 */
public abstract class ApplicationActivity extends AppCompatActivity implements
        ApplicationCallback,
        Config.ConfigCallback {

    public static FileModel mPhotoFile = null;
    public static IListener mPhotoFileListener = null;

    private Config mConfig;

    /* OnResult code */
    public static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mConfig = new Config(this, this);

        //region Handle NFC
        Intent intent = getIntent();
        String action = intent.getAction();
        NdefMessage[] msgs = null;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            Toast.makeText(this, "" + type, Toast.LENGTH_SHORT).show();

            // Check the MIME
            if (type.equals("text/plain")) {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMsgs != null) {
                    msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                    }
                }
            }
        }
        //endregion
    }

    @Override
    public void invalidateMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public Config getConfig() {
        if (mConfig == null) {
            mConfig = new Config(this, this);
        }
        return mConfig;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (mPhotoFile != null && mPhotoFile.getFile() != null) {
                List<StringPair> parameters = FileManager.getForUpload(mPhotoFile);
                (new TaskPost(this, this, getConfig().getUrlServer() + Config.routeFile, new IPostExecuteListener() {
                    @Override
                    public void onPostExecute(JSONObject json, String body) {
                        if (mPhotoFileListener != null) {
                            mPhotoFileListener.execute();
                        }
                    }
                }, parameters, mPhotoFile.getFile())).execute();
            } else {
                Toast.makeText(this, this.getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public FileModel createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_FileSpace_";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + this.getConfig().getLocalFolderName());
        FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
        fileModelBuilder.name(imageFileName + ".jpg");
        try {
            fileModelBuilder.file(File.createTempFile(imageFileName, ".jpg", storageDir));
        } catch (IOException e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return fileModelBuilder.build();
    }

    @Override
    public boolean isLogged() {
        return mConfig != null && Config.isLogged();
    }

    public abstract void refreshData();

    public abstract void updateAdapters();
}
