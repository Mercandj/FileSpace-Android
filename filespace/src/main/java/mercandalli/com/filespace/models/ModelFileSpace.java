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

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mercandalli.com.filespace.ui.activities.Application;
import mercandalli.com.filespace.utils.PointLong;
import mercandalli.com.filespace.utils.StringUtils;
import mercandalli.com.filespace.utils.TimeUtils;

import static mercandalli.com.filespace.models.ModelFileSpace.FileSpaceTypeENUM.*;

/**
 * Created by Jonathan on 22/03/2015.
 */
public class ModelFileSpace {

    public FileSpaceTypeENUM type;
    public Date date_creation;
    private Application app;

    public Timer timer = new Timer();
    public Article article = new Article();

    public ModelFileSpace(Application app, String type) {
        this.app = app;
        this.type = create(type);
    }

    public ModelFileSpace(Application app, JSONObject json) {
        this.app = app;
        try {
            if(json.has("type") && !json.isNull("type"))
                this.type = create(json.getString("type"));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if(json.has("date_creation") && !json.isNull("date_creation"))
                    this.date_creation = dateFormat.parse(json.getString("date_creation"));
                if(json.has("timer_date") && !json.isNull("timer_date"))
                    this.timer.timer_date = dateFormat.parse(json.getString("timer_date"));
                if(json.has("article_title_1") && !json.isNull("article_title_1"))
                    this.article.article_title_1 = json.getString("article_title_1");
                if(json.has("article_content_1") && !json.isNull("article_content_1"))
                    this.article.article_content_1 = json.getString("article_content_1");
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            Log.e("model ModelFileContent", "JSONException");
            e.printStackTrace();
        }
    }

    public String getAdapterTitle() {
        if(this.timer.timer_date != null) {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return TimeUtils.printDifferenceFuture(this.timer.timer_date, dateFormatLocal.parse(dateFormatGmt.format(new Date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(!StringUtils.isNullOrEmpty(this.article.article_content_1))
            return this.article.article_title_1;
        if(this.date_creation != null)
            return this.date_creation.toString();
        return "null";
    }

    @Override
    public String toString() {
        if(this.timer.timer_date != null) {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return TimeUtils.printDifferenceFuture(this.timer.timer_date, dateFormatLocal.parse(dateFormatGmt.format(new Date())));
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
            diff = this.timer.timer_date.getTime() - dateFormatLocal.parse(dateFormatGmt.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new PointLong(diff / 1000, (diff / 10) % 100);
    }

    public JSONObject toJSONObject() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        JSONObject json = new JSONObject();
        try {
            json.put("type", "timer");
            json.put("date_creation", dateFormatGmt.format(date_creation));
            switch (this.type) {
                case TIMER:
                    json.put("timer_date", "" + dateFormatGmt.format(this.timer.timer_date));
                    break;
                case ARTICLE:
                    json.put("article_content_1", "" + this.article.article_content_1);
                    json.put("article_title_1", "" + this.article.article_title_1);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public enum FileSpaceTypeENUM {
        TIMER("timer"),
        ARTICLE("article");
        public String type;
        FileSpaceTypeENUM(String type) {
            this.type = type;
        }

        public static FileSpaceTypeENUM create(String type_) {
            for(FileSpaceTypeENUM var: values())
                if(var.type.equals(type_))
                    return var;
            return null;
        }
    }

    public class Timer {
        public Date timer_date;
    }

    public class Article {
        public String article_title_1, article_content_1;
    }
}
