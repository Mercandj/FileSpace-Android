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
package com.mercandalli.android.apps.files.file.filespace;

import android.util.Log;

import com.mercandalli.android.apps.files.common.util.PointLong;
import com.mercandalli.android.apps.files.common.util.TimeUtils;
import com.mercandalli.android.library.mainlibrary.java.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.mercandalli.android.apps.files.file.filespace.FileSpaceModel.FileSpaceTypeENUM.create;

/**
 * Created by Jonathan on 22/03/2015.
 */
public class FileSpaceModel {

    public static final String UTC = "UTC";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_MS = "yyyy-MM-dd HH:mm:ss:SSS";

    public FileSpaceTypeENUM mType;
    public Date mDateCreation;

    private Timer mTimer = new Timer();
    private Article mArticle = new Article();

    private final SimpleDateFormat mDateFormatGmt;
    private final SimpleDateFormat mDateFormatLocal;
    private final SimpleDateFormat mDateFormatGmtMillis;
    private final SimpleDateFormat mDateFormatLocalMillis;

    public FileSpaceModel() {
        mDateFormatGmt = new SimpleDateFormat(DATE_FORMAT);
        mDateFormatGmt.setTimeZone(TimeZone.getTimeZone(UTC));
        mDateFormatLocal = new SimpleDateFormat(DATE_FORMAT);

        mDateFormatGmtMillis = new SimpleDateFormat(DATE_FORMAT_MS);
        mDateFormatGmtMillis.setTimeZone(TimeZone.getTimeZone(UTC));
        mDateFormatLocalMillis = new SimpleDateFormat(DATE_FORMAT_MS);

    }

    public String getAdapterTitle() {
        if (this.mTimer.timer_date != null) {
            try {
                return TimeUtils.printDifferenceFuture(this.mTimer.timer_date, mDateFormatLocal.parse(mDateFormatGmt.format(new Date())));
            } catch (ParseException e) {
                Log.e(getClass().getName(), "Exception", e);
            }
        }
        if (!StringUtils.isNullOrEmpty(mArticle.article_content_1)) {
            return mArticle.article_title_1;
        }
        if (mDateCreation != null) {
            return mDateCreation.toString();
        }
        return "null";
    }

    @Override
    public String toString() {
        if (mTimer.timer_date != null) {
            try {
                return TimeUtils.printDifferenceFuture(mTimer.timer_date, mDateFormatLocal.parse(mDateFormatGmt.format(new Date())));
            } catch (ParseException e) {
                Log.e(getClass().getName(), "Exception", e);
            }
        }
        if (mDateCreation != null) {
            return mDateCreation.toString();
        }
        return "null";
    }

    public PointLong diffSecond() {
        long diff = 0;
        try {
            diff = mTimer.timer_date.getTime() - mDateFormatLocalMillis.parse(mDateFormatGmtMillis.format(new Date())).getTime();
        } catch (ParseException e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new PointLong(diff / 1000, (diff / 10) % 100);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "timer");
            json.put("date_creation", mDateFormatGmt.format(mDateCreation));
            switch (mType) {
                case TIMER:
                    json.put("timer_date", "" + mDateFormatGmt.format(this.mTimer.timer_date));
                    break;
                case ARTICLE:
                    json.put("article_content_1", "" + mArticle.article_content_1);
                    json.put("article_title_1", "" + mArticle.article_title_1);
                    break;
            }

        } catch (JSONException e) {
            Log.e(getClass().getName(), "Failed to convert Json", e);
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
            for (FileSpaceTypeENUM var : values()) {
                if (var.type.contentEquals(type_)) {
                    return var;
                }
            }
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

        private Date mDateCreation, mTimerDate;
        private FileSpaceTypeENUM mType;
        private String mArticleTitle1, articleContent1;

        public FileSpaceModelBuilder dateCreation(final Date dateCreation) {
            this.mDateCreation = dateCreation;
            return this;
        }

        public FileSpaceModelBuilder type(final String type) {
            this.mType = create(type);
            return this;
        }

        public FileSpaceModelBuilder timerDate(Date timerDate) {
            this.mTimerDate = timerDate;
            return this;
        }

        public FileSpaceModelBuilder articleTitle1(final String articleTitle1) {
            this.mArticleTitle1 = articleTitle1;
            return this;
        }

        public FileSpaceModelBuilder articleContent1(final String articleContent1) {
            this.articleContent1 = articleContent1;
            return this;
        }

        public FileSpaceModel build() {
            FileSpaceModel fileSpaceModel = new FileSpaceModel();
            fileSpaceModel.setDateCreation(mDateCreation);
            fileSpaceModel.setTimerDate(mTimerDate);
            fileSpaceModel.setArticleTitle1(mArticleTitle1);
            fileSpaceModel.setArticleContent1(articleContent1);
            fileSpaceModel.setType(mType);
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


    public void setTimerDate(Date timerDate) {
        this.mTimer.timer_date = timerDate;
    }

    public void setArticleTitle1(String article_title_1) {
        mArticle.article_title_1 = article_title_1;
    }

    public void setArticleContent1(String article_content_1) {
        mArticle.article_content_1 = article_content_1;
    }

    public Timer getTimer() {
        return mTimer;
    }

    public Article getArticle() {
        return mArticle;
    }
}
