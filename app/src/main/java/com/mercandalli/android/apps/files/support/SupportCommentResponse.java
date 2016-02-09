package com.mercandalli.android.apps.files.support;

import com.google.gson.annotations.SerializedName;

public class SupportCommentResponse {

    @SerializedName("content")
    private String mContent;

    @SerializedName("is_dev_response")
    private boolean mIsDevResponse;

    public SupportComment toSupportComment() {
        return new SupportComment(mIsDevResponse, mContent);
    }
}
