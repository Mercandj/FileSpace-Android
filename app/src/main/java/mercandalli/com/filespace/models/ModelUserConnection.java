/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.models;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.ui.activities.Application;

public class ModelUserConnection extends Model {

	public String title, date_creation, url, username;
	public int viewType = Const.TAB_VIEW_TYPE_NORMAL;
	public int id_user;

	public ModelUserConnection() {
		super();
	}

	public ModelUserConnection(String date_creation, String url) {
		super();
		this.date_creation = date_creation;
		this.url = url;
	}

	public ModelUserConnection(String title, int viewType) {
		super();
		this.title = title;
		this.viewType = viewType;
	}

	public ModelUserConnection(Application app, JSONObject json) {
		super();
		this.app = app;
		try {
			if(json.has("date_creation"))
				this.date_creation = json.getString("date_creation");
			if(json.has("request_uri"))
				this.url = json.getString("request_uri");
			if(json.has("id_user"))
				this.id_user = json.getInt("id_user");
			if(json.has("username"))
				this.username = json.getString("username");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

    public String getAdapterTitle() {
        if(viewType != Const.TAB_VIEW_TYPE_NORMAL)
            return title;
        return ((this.username == null) ? "#" + this.id_user : this.username) + " : " + this.date_creation;
    }

    public String getAdapterSubtitle() { return "" + this.url; }

	@Override
	public JSONObject toJSONObject() {
		return null;
	}
}
