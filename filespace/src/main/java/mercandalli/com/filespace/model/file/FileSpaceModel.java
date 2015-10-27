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
package mercandalli.com.filespace.model.file;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mercandalli.com.filespace.util.PointLong;
import mercandalli.com.filespace.util.StringUtils;
import mercandalli.com.filespace.util.TimeUtils;

import static mercandalli.com.filespace.model.file.FileSpaceModel.FileSpaceTypeENUM.create;

/**
 * Created by Jonathan on 22/03/2015.
 */
public class FileSpaceModel {

    public FileSpaceTypeENUM mType;
    public Date mDateCreation;

    public Timer timer = new Timer();
    public Article article = new Article();

    public FileSpaceModel() {

    }

    public FileSpaceModel(final String type) {
        this.mType = FileSpaceModel.FileSpaceTypeENUM.create(type);
    }

    public String getAdapterTitle() {
        if (this.timer.timer_date != null) {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return TimeUtils.printDifferenceFuture(this.timer.timer_date, dateFormatLocal.parse(dateFormatGmt.format(new Date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isNullOrEmpty(this.article.article_content_1))
            return this.article.article_title_1;
        if (this.mDateCreation != null)
            return this.mDateCreation.toString();
        return "null";
    }

    @Override
    public String toString() {
        if (this.timer.timer_date != null) {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return TimeUtils.printDifferenceFuture(this.timer.timer_date, dateFormatLocal.parse(dateFormatGmt.format(new Date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (mDateCreation != null)
            return mDateCreation.toString();
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
            json.put("date_creation", dateFormatGmt.format(mDateCreation));
            switch (mType) {
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
        UNKNOWN("unknown"),
        TIMER("timer"),
        ARTICLE("article");
        private String type;

        FileSpaceTypeENUM(String type) {
            this.type = type;
        }

        public static FileSpaceTypeENUM create(String type_) {
            for (FileSpaceTypeENUM var : values())
                if (var.type.contentEquals(type_))
                    return var;
            return UNKNOWN;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public class Timer {
        public Date timer_date;
    }

    public class Article {
        public String article_title_1, article_content_1;
    }

    public static class FileSpaceModelBuilder {
        private Date dateCreation;
        private FileSpaceTypeENUM type;
        private String articleTitle1, articleContent1;

        public FileSpaceModelBuilder dateCreation(final Date dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public FileSpaceModelBuilder type(final String type) {
            this.type = create(type);
            return this;
        }

        public FileSpaceModelBuilder articleTitle1(final String articleTitle1) {
            this.articleTitle1 = articleTitle1;
            return this;
        }

        public FileSpaceModelBuilder articleContent1(final String articleContent1) {
            this.articleContent1 = articleContent1;
            return this;
        }

        public FileSpaceModel build() {
            FileSpaceModel fileSpaceModel = new FileSpaceModel();
            fileSpaceModel.setDateCreation(dateCreation);
            fileSpaceModel.setType(type);
            return fileSpaceModel;
        }
    }

    public FileSpaceTypeENUM getType() {
        return mType;
    }

    public void setType(FileSpaceTypeENUM type) {
        this.mType = type;
    }

    public Date getDateCreation() {
        return mDateCreation;
    }

    public void setDateCreation(Date mDateCreation) {
        this.mDateCreation = mDateCreation;
    }
}
