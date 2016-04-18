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
package com.mercandalli.android.apps.files.user;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.mercandalli.android.apps.files.common.listener.IBitmapListener;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskGetDownloadImage;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.ImageUtils;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.library.baselibrary.java.HashUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UserModel {

    private static final String ADMIN = "admin";

    public int id, id_file_profile_picture = -1;
    public String username;
    public String password;
    public String regId;
    public Date date_creation, date_last_connection;
    public long size_files, file_profile_picture_size = -1, num_files, server_max_size_end_user;
    private boolean admin = false;
    public Bitmap bitmap;
    public UserLocationModel userLocation;

    public UserModel() {

    }

    public UserModel(int id, String username, String password, String regId, boolean admin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.regId = regId;
        this.admin = admin;
    }

    public UserModel(final Activity activity, JSONObject json) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if (json.has("id")) {
                this.id = json.getInt("id");
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
                this.date_last_connection = dateFormat.parse(json.getString("date_last_connection"));
            }
            if (json.has("size_files") && !json.isNull("size_files")) {
                this.size_files = json.getLong("size_files");
            }
            if (json.has("server_max_size_end_user") && !json.isNull("server_max_size_end_user")) {
                this.server_max_size_end_user = json.getLong("server_max_size_end_user");
            }
            if (json.has(ADMIN)) {
                Object admin_obj = json.get(ADMIN);
                if (admin_obj instanceof Integer) {
                    this.admin = json.getInt(ADMIN) == 1;
                } else if (admin_obj instanceof Boolean) {
                    this.admin = json.getBoolean(ADMIN);
                }
            }

            if (json.has("id_file_profile_picture")) {
                this.id_file_profile_picture = json.getInt("id_file_profile_picture");
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
            if (ImageUtils.isImage(activity, this.id_file_profile_picture)) {
                UserModel.this.bitmap = ImageUtils.loadImage(activity, this.id_file_profile_picture);
            } else {
                FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
                fileModelBuilder.id(this.id_file_profile_picture);
                fileModelBuilder.size(this.file_profile_picture_size);
                new TaskGetDownloadImage(activity, fileModelBuilder.build(), Constants.SIZE_MAX_ONLINE_PICTURE_ICON, new IBitmapListener() {
                    @Override
                    public void execute(Bitmap bitmap) {
                        if (bitmap != null) {
                            UserModel.this.bitmap = bitmap;
                        }
                    }
                }).execute();
            }
        }
    }

    public boolean hasPicture() {
        return id_file_profile_picture != -1 && file_profile_picture_size != -1;
    }

    public String getAdapterTitle() {
        return this.username;
    }

    public String getAdapterSubtitle() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String date = dateFormat.format(date_last_connection.getTime());
        return date + "   " + FileUtils.humanReadableByteCount(size_files) + "   " + this.num_files + " file" + (this.num_files > 1 ? "s" : "");
    }

    public String getAccessLogin() {
        return this.username;
    }

    public String getAccessPassword() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDate = dateFormatGmt.format(calendar.getTime());
        return HashUtils.sha1(HashUtils.sha1(this.password) + currentDate);
    }

    public boolean isAdmin() {
        return admin;
    }

    public void delete(Activity activity, IPostExecuteListener listener) {
        if (Config.isUserAdmin() && this.id != Config.getUserId()) {
            String url = Constants.URL_DOMAIN + Config.ROUTE_USER_DELETE + "/" + this.id;
            new TaskPost(activity, url, listener).execute();
            return;
        }
        if (listener != null) {
            listener.onPostExecute(null, null);
        }
    }
}
