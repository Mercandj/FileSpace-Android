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
package com.mercandalli.android.apps.files.user;

import android.app.Activity;
import android.util.Log;

import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.library.base.java.HashUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UserModel {

    private static final String ADMIN = "admin";

    private int mId = -1;
    private int mIdFileProfilePicture = -1;
    public String username;
    public String password;
    public String regId;
    public Date date_creation;
    private Date mDateLastConnection;
    public long size_files, file_profile_picture_size = -1, num_files, server_max_size_end_user;
    private boolean admin = false;
    public String mPictureUrl;
    public UserLocationModel userLocation;

    public UserModel() {

    }

    public UserModel(
            int id, String username, String password, String regId, boolean admin) {
        mId = id;
        this.username = username;
        this.password = password;
        this.regId = regId;
        this.admin = admin;
    }

    public UserModel(JSONObject json) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        try {
            if (json.has("id")) {
                this.mId = json.getInt("id");
            }
            if (json.has("username")) {
                this.username = json.getString("username");
            }
            if (json.has("password")) {
                this.password = json.getString("password");
            }
            if (json.has("regId")) {
                this.regId = json.getString("regId");
            }
            if (json.has("date_creation") && !json.isNull("date_creation")) {
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
            }
            if (json.has("date_last_connection") && !json.isNull("date_last_connection")) {
                this.mDateLastConnection = dateFormat.parse(json.getString("date_last_connection"));
            }
            if (json.has("size_files") && !json.isNull("size_files")) {
                this.size_files = json.getLong("size_files");
            }
            if (json.has("server_max_size_end_user") && !json.isNull("server_max_size_end_user")) {
                this.server_max_size_end_user = json.getLong("server_max_size_end_user");
            }
            if (json.has(ADMIN)) {
                final Object adminObj = json.get(ADMIN);
                if (adminObj instanceof Integer) {
                    this.admin = json.getInt(ADMIN) == 1;
                } else if (adminObj instanceof Boolean) {
                    this.admin = json.getBoolean(ADMIN);
                }
            }

            if (json.has("id_file_profile_picture")) {
                this.mIdFileProfilePicture = json.getInt("id_file_profile_picture");
            }
            if (json.has("file_profile_picture_size")) {
                this.file_profile_picture_size = json.getLong("file_profile_picture_size");
            }
            if (json.has("num_files") && !json.isNull("num_files")) {
                this.num_files = json.getLong("num_files");
            }

            userLocation = new UserLocationModel(json);

        } catch (JSONException | ParseException e) {
            Log.e(getClass().getName(), "Failed to convert Json", e);
        }

        if (hasPicture()) {
            FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
            fileModelBuilder.id(this.mIdFileProfilePicture);
            fileModelBuilder.size(this.file_profile_picture_size);
            mPictureUrl = fileModelBuilder.build().getOnlineUrl();
        }
    }

    public boolean hasPicture() {
        return mIdFileProfilePicture != -1 && file_profile_picture_size != -1;
    }

    public String getAdapterTitle() {
        return this.username;
    }

    public String getAdapterSubtitle() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);
        String date = dateFormat.format(mDateLastConnection.getTime());
        return date + "   " + FileUtils.humanReadableByteCount(size_files) + "   " + this.num_files + " file" + (this.num_files > 1 ? "s" : "");
    }

    public String getAccessLogin() {
        return this.username;
    }

    public String getAccessPassword() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        final SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDate = dateFormatGmt.format(calendar.getTime());
        return HashUtils.sha1(HashUtils.sha1(this.password) + currentDate);
    }

    public boolean isAdmin() {
        return admin;
    }

    public void delete(Activity activity, IPostExecuteListener listener) {
        if (Config.isUserAdmin() && this.mId != Config.getUserId()) {
            String url = Constants.URL_DOMAIN + Config.ROUTE_USER_DELETE + "/" + this.mId;
            new TaskPost(activity, url, listener).execute();
            return;
        }
        if (listener != null) {
            listener.onPostExecute(null, null);
        }
    }

    public int getId() {
        return mId;
    }

    public int getIdFileProfilePicture() {
        return mIdFileProfilePicture;
    }

    public Date getDateLastConnection() {
        return mDateLastConnection;
    }
}
