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
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.main.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationUserModel {

    public int id;
    public int id_conversation;
    public int id_user;
    public int num_messages;
    public Date date_creation;
    @NonNull
    private final List<UserModel> mUserModels = new ArrayList<>();
    public boolean to_all = false, to_yourself = false;

    public ConversationUserModel(final JSONObject json) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        try {
            if (json.has("id")) {
                this.id = json.getInt("id");
            }
            if (json.has("id_conversation")) {
                this.id_conversation = json.getInt("id_conversation");
            }
            if (json.has("id_user")) {
                this.id_user = json.getInt("id_user");
            }
            if (json.has("num_messages")) {
                this.num_messages = json.getInt("num_messages");
            }
            if (json.has("users")) {
                JSONArray users_json = json.getJSONArray("users");
                for (int i = 0; i < users_json.length(); i++) {
                    this.mUserModels.add(new UserModel(users_json.getJSONObject(i)));
                }
            }
            if (json.has("to_all")) {
                this.to_all = json.getBoolean("to_all");
            }
            if (json.has("to_yourself")) {
                this.to_yourself = json.getBoolean("to_yourself");
            }
            if (json.has("date_creation") && !json.isNull("date_creation")) {
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
            }
        } catch (JSONException | ParseException e) {
            Log.e(getClass().getName(), "Failed to convert Json", e);
        }
    }

    public String getAdapterTitle() {
        String res = "With ";
        if (this.to_all) {
            res += "all";
        } else if (this.to_yourself) {
            res += "yourself";
        } else {
            for (final UserModel user : mUserModels) {
                res += user.username + " ";
            }
        }
        return res;
    }

    public String getAdapterSubtitle() {
        return "" + this.num_messages + "  message" + ((this.num_messages != 0) ? "s" : "");
    }

    public void open(final Activity activity) {
        final Intent intent = new Intent(activity, ConversationActivity.class);
        intent.putExtra("LOGIN", "" + Config.getUser().getAccessLogin());
        intent.putExtra("PASSWORD", "" + Config.getUser().getAccessPassword());
        intent.putExtra("ID_CONVERSATION", "" + this.id_conversation);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
