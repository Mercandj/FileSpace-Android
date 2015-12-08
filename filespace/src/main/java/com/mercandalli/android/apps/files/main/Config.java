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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mercandalli.android.apps.files.common.listener.IBitmapListener;
import com.mercandalli.android.apps.files.common.net.Base64;
import com.mercandalli.android.apps.files.common.net.TaskGetDownloadImage;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.common.util.HashUtils;
import com.mercandalli.android.apps.files.common.util.NetUtils;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.user.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * TODO - Need a review ! (Shared pref)
 */
public class Config {

    private ApplicationCallback app;

    // Local routes
    public static final String localFolderNameDefault = "FileSpace";
    private static final String fileName = "settings_json_1.txt";

    // Server routes
    public static final String aboutURL = "http://mercandalli.com/";
    public static final String webApplication = "http://mercandalli.com/FileSpace";
    public static final String routeFile = "file";
    public static final String routeFileDelete = "file_delete";
    public static final String routeInformation = "information";
    public static final String routeRobotics = "robotics";
    public static final String routeGenealogy = "genealogy";
    public static final String routeGenealogyDelete = "genealogy_delete";
    public static final String routeGenealogyPut = "genealogy_put";
    public static final String routeGenealogyChildren = "genealogy_children";
    public static final String routeGenealogyStatistics = "genealogy_statistics";
    public static final String routeUser = "user";
    public static final String routeUserDelete = "user_delete";
    public static final String routeUserPut = "user_put";
    public static final String routeUserMessage = "user_message";
    public static final String routeUserConversation = "user_conversation";
    public static final String routeUserConnection = "user_connection";

    /**
     * Static int to save/load
     */
    private enum ENUM_Int {
        INTEGER_USER_ID(-1, "int_user_id_1"),
        INTEGER_USER_ID_FILE_PROFILE_PICTURE(-1, "int_user_id_file_profile_picture_1"),
        INTEGER_USER_FILE_MODE_VIEW(-1, "int_user_file_mode_view_1"),;

        int value;
        String key;

        ENUM_Int(int init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    /**
     * Static boolean to save/load
     */
    private enum ENUM_Boolean {
        BOOLEAN_AUTO_CONNECTION(true, "boolean_auto_connection_1"),
        BOOLEAN_USER_ADMIN(false, "boolean_user_admin_1");

        boolean value;
        String key;

        ENUM_Boolean(boolean init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    /**
     * Static Sctring to save/load
     */
    private enum ENUM_String {
        STRING_URL_SERVER("http://mercandalli.com/FileSpace-API/", "string_url_server_1"),
        STRING_USER_USERNAME("", "string_user_username_1"),
        STRING_USER_PASSWORD("", "string_user_password_1"),
        STRING_USER_REGID("", "string_user_regid_1"),
        STRING_USER_NOTE_WORKSPACE_1("", "string_user_note_workspace_1"),
        STRING_LOCAL_FOLDER_NAME_1("", "string_local_folder_name_1"),;

        String value;
        String key;

        ENUM_String(String init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    public Config(Activity activity, ApplicationCallback app) {
        this.app = app;
        load(activity);
    }

    private void save(Context context) {
        try {
            JSONObject tmp_json = new JSONObject();
            JSONObject tmp_settings_1 = new JSONObject();
            for (ENUM_Int enum_int : ENUM_Int.values())
                tmp_settings_1.put(enum_int.key, enum_int.value);
            for (ENUM_Boolean enum_boolean : ENUM_Boolean.values())
                tmp_settings_1.put(enum_boolean.key, enum_boolean.value);
            for (ENUM_String enum_string : ENUM_String.values())
                tmp_settings_1.put(enum_string.key, enum_string.value);
            tmp_json.put("settings_1", tmp_settings_1);
            FileUtils.writeStringFile(context, fileName, tmp_json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void load(Activity activity) {
        try {
            JSONObject tmp_json = new JSONObject(FileUtils.readStringFile(activity, fileName));
            if (tmp_json.has("settings_1")) {
                JSONObject tmp_settings_1 = tmp_json.getJSONObject("settings_1");
                for (ENUM_Int enum_int : ENUM_Int.values())
                    if (tmp_settings_1.has(enum_int.key))
                        enum_int.value = tmp_settings_1.getInt(enum_int.key);
                for (ENUM_Boolean enum_boolean : ENUM_Boolean.values())
                    if (tmp_settings_1.has(enum_boolean.key))
                        enum_boolean.value = tmp_settings_1.getBoolean(enum_boolean.key);
                for (ENUM_String enum_string : ENUM_String.values())
                    if (tmp_settings_1.has(enum_string.key))
                        enum_string.value = tmp_settings_1.getString(enum_string.key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLogged() {
        return getUserId() > -1;
    }

    public static int getUserId() {
        return ENUM_Int.INTEGER_USER_ID.value;
    }

    public void setUserId(Activity activity, int value) {
        if (ENUM_Int.INTEGER_USER_ID.value != value) {
            ENUM_Int.INTEGER_USER_ID.value = value;
            save(activity);
        }
    }

    public String getUrlServer() {
        return ENUM_String.STRING_URL_SERVER.value;
    }

    public String getUserNoteWorkspace1() {
        return ENUM_String.STRING_USER_NOTE_WORKSPACE_1.value;
    }

    public void setUserNoteWorkspace1(Activity activity, String value) {
        if (!ENUM_String.STRING_USER_NOTE_WORKSPACE_1.value.equals(value)) {
            ENUM_String.STRING_USER_NOTE_WORKSPACE_1.value = value;
            save(activity);
        }
    }

    public String getUserUsername() {
        return ENUM_String.STRING_USER_USERNAME.value;
    }

    public static String getUserToken() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDate = dateFormatGmt.format(calendar.getTime());

        String log = ENUM_String.STRING_USER_USERNAME.value;
        String pass = HashUtils.sha1(HashUtils.sha1(ENUM_String.STRING_USER_PASSWORD.value) + currentDate);

        String authentication = log + ":" + pass;
        return Base64.encodeBytes(authentication.getBytes());
    }

    public void setUserUsername(Activity activity, String value) {
        if (!ENUM_String.STRING_USER_USERNAME.value.equals(value)) {
            ENUM_String.STRING_USER_USERNAME.value = value;
            save(activity);
        }
    }

    public String getLocalFolderName() {
        return ENUM_String.STRING_LOCAL_FOLDER_NAME_1.value;
    }

    public String getUserPassword() {
        return ENUM_String.STRING_USER_PASSWORD.value;
    }

    public void setUserPassword(Activity activity, String value) {
        if (!ENUM_String.STRING_USER_PASSWORD.value.equals(value)) {
            ENUM_String.STRING_USER_PASSWORD.value = value;
            save(activity);
        }
    }

    public static String getUserRegId() {
        return ENUM_String.STRING_USER_REGID.value;
    }

    public void setUserRegId(Activity activity, String value) {
        if (!ENUM_String.STRING_USER_REGID.value.equals(value)) {
            ENUM_String.STRING_USER_REGID.value = value;
            save(activity);
        }
    }

    public Bitmap getUserProfilePicture(Activity activity) {
        File file = new File(activity.getFilesDir() + "/file_" + this.getUserIdFileProfilePicture());
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getPath());
        } else if (NetUtils.isInternetConnection(activity)) {
            FileModel.FileModelBuilder fileModelBuilder = new FileModel.FileModelBuilder();
            fileModelBuilder.id(getUserIdFileProfilePicture());
            new TaskGetDownloadImage(activity, app, fileModelBuilder.build(), 100000, new IBitmapListener() {
                @Override
                public void execute(Bitmap bitmap) {
                    //TODO photo profile
                }
            }).execute();
        }
        return null;
    }

    public int getUserIdFileProfilePicture() {
        return ENUM_Int.INTEGER_USER_ID_FILE_PROFILE_PICTURE.value;
    }

    public void setUserIdFileProfilePicture(Activity activity, int value) {
        if (ENUM_Int.INTEGER_USER_ID_FILE_PROFILE_PICTURE.value != value) {
            ENUM_Int.INTEGER_USER_ID_FILE_PROFILE_PICTURE.value = value;
            save(activity);
        }
    }

    public int getUserFileModeView() {
        return ENUM_Int.INTEGER_USER_FILE_MODE_VIEW.value;
    }

    public void setUserFileModeView(Activity activity, int value) {
        if (ENUM_Int.INTEGER_USER_FILE_MODE_VIEW.value != value) {
            ENUM_Int.INTEGER_USER_FILE_MODE_VIEW.value = value;
            save(activity);
        }
    }

    public boolean isUserAdmin() {
        return ENUM_Boolean.BOOLEAN_USER_ADMIN.value;
    }

    public void setUserAdmin(Activity activity, boolean value) {
        if (ENUM_Boolean.BOOLEAN_USER_ADMIN.value != value) {
            ENUM_Boolean.BOOLEAN_USER_ADMIN.value = value;
            save(activity);
        }
    }

    public boolean isAutoConncetion() {
        return ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value;
    }

    public void setAutoConnection(Activity activity, boolean value) {
        if (ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value != value) {
            ENUM_Boolean.BOOLEAN_AUTO_CONNECTION.value = value;
            save(activity);
        }
    }

    public UserModel getUser() {
        return new UserModel(getUserId(), getUserUsername(), getUserPassword(), getUserRegId(), isUserAdmin());
    }

    public static String getFileName() {
        return fileName;
    }

    /**
     * Reset the saved values
     * (When the user log out)
     */
    public void reset(Activity activity) {
        setUserRegId(activity, "");
        setUserUsername(activity, "");
        setUserPassword(activity, "");
        setAutoConnection(activity, true);
        setUserId(activity, -1);
        setUserAdmin(activity, false);
        setUserIdFileProfilePicture(activity, -1);
    }
}