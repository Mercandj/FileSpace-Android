package com.mercandalli.android.filespace.file.cloud.response;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.mercandalli.android.filespace.file.filespace.FileSpaceModel;

public class FileSpaceResponse {

    @SerializedName("type")
    private String mType;

    @SerializedName("date_creation")
    private String mDateCreation;

    @SerializedName("timer_date")
    private String mTimerDate;

    @SerializedName("article_title_1")
    private String mArticleTitle1;

    @SerializedName("article_content_1")
    private String mArticleContent1;

    public FileSpaceModel createModel() {

        Date dateCreation = null;
        Date timerDate = null;
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            if (mDateCreation != null) {
                dateCreation = dateFormatGmt.parse(mDateCreation);
            }
            if (mTimerDate != null) {
                timerDate = dateFormatGmt.parse(mTimerDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new FileSpaceModel.FileSpaceModelBuilder()
                .type(mType)
                .dateCreation(dateCreation)
                .timerDate(timerDate)
                .articleTitle1(mArticleTitle1)
                .articleContent1(mArticleContent1)
                .build();
    }
}
