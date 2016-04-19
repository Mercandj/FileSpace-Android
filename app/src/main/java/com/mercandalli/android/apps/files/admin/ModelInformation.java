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
package com.mercandalli.android.apps.files.admin;

import android.util.Log;

import com.mercandalli.android.apps.files.main.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelInformation {

    private static final String TAG = "ModelInformation";
    public String title;
    public String value;
    public int viewType = Constants.TAB_VIEW_TYPE_NORMAL;

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
            if (json.has("title")) {
                this.title = json.getString("title");
            }
            if (json.has("value")) {
                this.value = json.getString("value");
            }
        } catch (JSONException e) {
            Log.e(TAG, "ModelInformation: failed to convert Json", e);
        }
    }
}
