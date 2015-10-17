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
package mercandalli.com.filespace.models;

import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;

import mercandalli.com.filespace.ui.activities.ApplicationActivity;

import org.json.JSONObject;

public class ModelSetting extends Model {

    public OnCheckedChangeListener toggleButtonListener = null;
    public boolean toggleButtonInitValue = false;
    public String title, subtitle;
    public View.OnClickListener onClickListener;

    public ModelSetting(ApplicationActivity app, String title) {
        super(app);
        this.title = title;
    }

    public ModelSetting(ApplicationActivity app, String title, String subtitle) {
        super(app);
        this.title = title;
        this.subtitle = subtitle;
    }

    public ModelSetting(ApplicationActivity app, String title, int viewType) {
        super(app);
        this.title = title;
        this.viewType = viewType;
    }

    public ModelSetting(ApplicationActivity app, String title, String subtitle, int viewType) {
        super(app);
        this.title = title;
        this.subtitle = subtitle;
        this.viewType = viewType;
    }

    public ModelSetting(ApplicationActivity app, String title, OnCheckedChangeListener toggleButtonListener, boolean toggleButtonInitValue) {
        super(app);
        this.title = title;
        this.toggleButtonListener = toggleButtonListener;
        this.toggleButtonInitValue = toggleButtonInitValue;
    }

    public ModelSetting(ApplicationActivity app, String title, View.OnClickListener onClickListener) {
        super(app);
        this.title = title;
        this.onClickListener = onClickListener;
    }

    public ModelSetting(ApplicationActivity app, String title, String subtitle, View.OnClickListener onClickListener) {
        super(app);
        this.title = title;
        this.subtitle = subtitle;
        this.onClickListener = onClickListener;
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
