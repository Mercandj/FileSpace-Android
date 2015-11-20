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
package mercandalli.com.filespace.common.model;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.filespace.main.Constants;

public class ModelInformation extends Model {

    public String title;
    public String value;
    public int viewType = Constants.TAB_VIEW_TYPE_NORMAL;

    public ModelInformation() {
        super();
    }

    public ModelInformation(String title, String value) {
        super();
        this.title = title;
        this.value = value;
    }

    public ModelInformation(String title, int viewType) {
        super();
        this.title = title;
        this.viewType = viewType;
    }

    public ModelInformation(JSONObject json) {
        super();
        try {
            if (json.has("title"))
                this.title = json.getString("title");
            if (json.has("value"))
                this.value = json.getString("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
