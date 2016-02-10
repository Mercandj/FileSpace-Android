package com.mercandalli.android.apps.files.support;

import com.google.gson.annotations.SerializedName;

public class SupportCommentResponse {

    @SerializedName("id")
    private String mId;

    @SerializedName("content")
    private String mContent;

    @SerializedName("is_dev_response")
    private boolean mIsDevResponse;

    public SupportComment toSupportComment() {
        return new SupportComment(mId, mIsDevResponse, mContent);
    }
}
