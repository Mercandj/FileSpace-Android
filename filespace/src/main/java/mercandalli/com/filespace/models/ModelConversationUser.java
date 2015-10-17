/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.models;

import android.content.Intent;

import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.ui.activities.ConversationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mercandalli.com.filespace.R;

public class ModelConversationUser extends Model {

    public int id, id_conversation, id_user, num_messages;
    public Date date_creation;
    public List<ModelUser> users;
    public boolean to_all = false, to_yourself = false;

    public ModelConversationUser() {

    }

    public ModelConversationUser(ApplicationActivity app, JSONObject json) {
        super();
        this.app = app;
        this.users = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if (json.has("id"))
                this.id = json.getInt("id");
            if (json.has("id_conversation"))
                this.id_conversation = json.getInt("id_conversation");
            if (json.has("id_user"))
                this.id_user = json.getInt("id_user");
            if (json.has("num_messages"))
                this.num_messages = json.getInt("num_messages");
            if (json.has("users")) {
                JSONArray users_json = json.getJSONArray("users");
                for (int i = 0; i < users_json.length(); i++) {
                    this.users.add(new ModelUser(app, users_json.getJSONObject(i)));
                }
            }
            if (json.has("to_all"))
                this.to_all = json.getBoolean("to_all");
            if (json.has("to_yourself"))
                this.to_yourself = json.getBoolean("to_yourself");
            if (json.has("date_creation") && !json.isNull("date_creation"))
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getAdapterTitle() {
        String res = "With ";
        if (this.to_all) {
            res += "all";
        } else if (this.to_yourself) {
            res += "yourself";
        } else {
            for (ModelUser user : users)
                res += user.username + " ";
        }
        return res;
    }

    public String getAdapterSubtitle() {
        return "" + this.num_messages + "  message" + ((this.num_messages != 0) ? "s" : "");
    }

    public void open() {
        Intent intent = new Intent(this.app, ConversationActivity.class);
        intent.putExtra("LOGIN", "" + this.app.getConfig().getUser().getAccessLogin());
        intent.putExtra("PASSWORD", "" + this.app.getConfig().getUser().getAccessPassword());
        intent.putExtra("ID_CONVERSATION", "" + this.id_conversation);
        this.app.startActivity(intent);
        this.app.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
