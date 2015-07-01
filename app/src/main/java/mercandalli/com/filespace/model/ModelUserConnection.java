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
package mercandalli.com.filespace.model;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.filespace.config.Const;
import mercandalli.com.filespace.ui.activity.Application;

public class ModelUserConnection extends Model {

	public String title;
	public String value;
	public int viewType = Const.TAB_VIEW_TYPE_NORMAL;
	public int id_user;

	public ModelUserConnection() {
		super();
	}

	public ModelUserConnection(String title, String value) {
		super();
		this.title = title;
		this.value = value;
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
				this.title = json.getString("date_creation");
			if(json.has("request_uri"))
				this.value = json.getString("request_uri");
			if(json.has("id_user"))
				this.id_user = json.getInt("id_user");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject toJSONObject() {
		return null;
	}
}
