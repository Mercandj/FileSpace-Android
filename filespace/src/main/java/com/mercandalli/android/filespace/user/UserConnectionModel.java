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
package com.mercandalli.android.filespace.user;

import com.mercandalli.android.filespace.main.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class UserConnectionModel {

    public String title, date_creation, url, username;
    public int viewType = Constants.TAB_VIEW_TYPE_NORMAL;
    public int id_user;

    public UserConnectionModel(String date_creation, String url) {
        super();
        this.date_creation = date_creation;
        this.url = url;
    }

    public UserConnectionModel(String title, int viewType) {
        super();
        this.title = title;
        this.viewType = viewType;
    }

    public UserConnectionModel(JSONObject json) {
        super();
        try {
            if (json.has("date_creation"))
                this.date_creation = json.getString("date_creation");
            if (json.has("request_uri"))
                this.url = json.getString("request_uri");
            if (json.has("id_user"))
                this.id_user = json.getInt("id_user");
            if (json.has("username"))
                this.username = json.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAdapterTitle() {
        if (viewType != Constants.TAB_VIEW_TYPE_NORMAL) {
            return title;
        }
        return ((this.username == null) ? "#" + this.id_user : this.username) + " : " + this.date_creation;
    }

    public String getAdapterSubtitle() {
        return "" + this.url;
    }
}
