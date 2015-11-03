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
package mercandalli.com.filespace.model;

import android.app.Activity;

import mercandalli.com.filespace.ui.activity.ApplicationCallback;
import mercandalli.com.filespace.util.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ModelConversationMessage extends Model {

    public int id, id_conversation, id_user;
    public Date date_creation;
    public String content;
    public ModelUser user;

    public ModelConversationMessage(Activity activity, ApplicationCallback app, JSONObject json) {
        super(activity, app);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if (json.has("id"))
                this.id = json.getInt("id");
            if (json.has("id_conversation"))
                this.id_conversation = json.getInt("id_conversation");
            if (json.has("id_user"))
                this.id_user = json.getInt("id_user");
            if (json.has("content"))
                this.content = json.getString("content");
            if (json.has("user"))
                this.user = new ModelUser(activity, app, json.getJSONObject("user"));
            if (json.has("date_creation") && !json.isNull("date_creation"))
                this.date_creation = dateFormat.parse(json.getString("date_creation"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        if (this.user == null)
            return "";
        return (this.user.username == null) ? "" : this.user.username;
    }

    public String getAdapterTitle() {
        return this.content;
    }

    public String getAdapterSubtitle() {
        String date = date_creation.toString();
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = TimeUtils.printDifferencePast(date_creation, dateFormatLocal.parse(dateFormatGmt.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getUsername() + "  " + date + " ago";
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}