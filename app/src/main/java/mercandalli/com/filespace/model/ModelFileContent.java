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

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.util.PointLong;
import mercandalli.com.filespace.util.TimeUtils;

/**
 * Created by Jonathan on 22/03/2015.
 */
public class ModelFileContent {

    public String type;
    public Date date_creation, timer_date;
    private Application app;

    public ModelFileContent(Application app, Date timer_date) {
        this.app = app;
        this.timer_date = timer_date;
    }

    public ModelFileContent(Application app, String content) {
        this.app = app;
        try {
            JSONObject json = new JSONObject(content);
            if(json.has("type") && !json.isNull("type"))
                this.type = json.getString("type");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if(json.has("date_creation") && !json.isNull("date_creation"))
                    this.date_creation = dateFormat.parse(json.getString("date_creation"));
                if(json.has("timer_date") && !json.isNull("timer_date"))
                    this.timer_date = dateFormat.parse(json.getString("timer_date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            Log.e("model ModelFileContent", "JSONException");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if(timer_date != null) {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return TimeUtils.printDifferenceFuture(timer_date, dateFormatLocal.parse(dateFormatGmt.format(new Date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(date_creation != null)
            return date_creation.toString();
        return "null";
    }

    public PointLong diffSecond() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        long diff = 0;
        try {
            diff = timer_date.getTime() - dateFormatLocal.parse(dateFormatGmt.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new PointLong(diff / 1000, (diff / 10) % 100);
    }
}
