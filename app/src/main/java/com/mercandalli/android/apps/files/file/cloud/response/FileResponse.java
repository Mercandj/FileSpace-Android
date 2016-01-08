package com.mercandalli.android.apps.files.file.cloud.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mercandalli.android.apps.files.common.util.StringUtils;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileTypeModel;
import com.mercandalli.android.apps.files.file.filespace.FileSpaceModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FileResponse {
    @SerializedName("id")
    private int mId;

    @SerializedName("id_user")
    private int mIdUser;

    @SerializedName("id_file_parent")
    private int mIdFileParent;

    @SerializedName("name")
    private String mName;

    @SerializedName("url")
    private String mUrl;

    @SerializedName("size")
    private long mSize;

    @SerializedName("public")
    private long mPublic;

    @SerializedName("type")
    private String mType;

    @SerializedName("directory")
    private long mDirectory;

    @SerializedName("date_creation")
    private String mDateCreation;

    @SerializedName("is_apk_update")
    private int mIsApkUpdate;

    @SerializedName("content")
    private String mContent;

    public FileModel createModel() {

        Date dateCreation = null;
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            dateCreation = dateFormatGmt.parse(mDateCreation);
        } catch (ParseException e) {
            Log.e(getClass().getName(), "Exception", e);
        }

        FileSpaceModel content = null;
        if (!StringUtils.isNullOrEmpty(mContent)) {
            Gson gson = new Gson();
            FileSpaceResponse contentResponse = gson.fromJson(mContent, FileSpaceResponse.class);
            if (contentResponse != null) {
                content = contentResponse.createModel();
            }
        }

        return new FileModel.FileModelBuilder()
                .id(mId)
                .idUser(mIdUser)
                .idFileParent(mIdFileParent)
                .name(mName)
                .url(mUrl)
                .size(mSize)
                .isPublic(mPublic == 1)
                .type(new FileTypeModel(mType))
                .isDirectory(mDirectory == 1)
                .dateCreation(dateCreation)
                .isApkUpdate(mIsApkUpdate == 1)
                .content(content)
                .isOnline(true)
                .build();
    }
}
