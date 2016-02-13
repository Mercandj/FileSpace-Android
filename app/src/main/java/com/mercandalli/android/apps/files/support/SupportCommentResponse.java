package com.mercandalli.android.apps.files.support;

import com.google.gson.annotations.SerializedName;

public class SupportCommentResponse {

    @SerializedName("id")
    private String mId;

    @SerializedName("id_device")
    private String mIdDevice;

    @SerializedName("content")
    private String mContent;

    @SerializedName("is_dev_response")
    private boolean mIsDevResponse;

    @SerializedName("android_app_version_code")
    private String mAndroidAppVersionCode;

    @SerializedName("android_app_version_name")
    private String mAndroidAppVersionName;

    @SerializedName("android_device_version_sdk")
    private String mAndroidDeviceVersionSdk;

    @SerializedName("nb_comments_with_this_id_device")
    private int mNbCommentsWithThisIdDevice;

    public SupportComment toSupportComment() {
        return new SupportComment(
                mId,
                mIdDevice,
                mIsDevResponse,
                mContent,
                mAndroidAppVersionCode,
                mAndroidAppVersionName,
                mAndroidDeviceVersionSdk,
                mNbCommentsWithThisIdDevice);
    }
}
