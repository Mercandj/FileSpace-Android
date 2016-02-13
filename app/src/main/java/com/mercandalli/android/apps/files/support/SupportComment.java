package com.mercandalli.android.apps.files.support;

public class SupportComment {

    private final String mId;
    private final String mIdDevice;
    private final boolean mIsDevResponse;
    private final String mComment;
    private final String mAndroidAppVersionCode;
    private final String mAndroidAppVersionName;
    private final String mAndroidDeviceVersionSdk;
    private final int mNbCommentsWithThisIdDevice;

    public SupportComment(
            final String id,
            final String idDevice,
            final boolean isDevResponse,
            final String comment,
            final String androidAppVersionCode,
            final String androidAppVersionName,
            final String androidDeviceVersionSdk,
            final int nbCommentsWithThisIdDevice) {

        mId = id;
        mIdDevice = idDevice;
        mIsDevResponse = isDevResponse;
        mComment = comment;
        mAndroidAppVersionCode = androidAppVersionCode;
        mAndroidAppVersionName = androidAppVersionName;
        mAndroidDeviceVersionSdk = androidDeviceVersionSdk;
        mNbCommentsWithThisIdDevice = nbCommentsWithThisIdDevice;
    }

    public String getId() {
        return mId;
    }

    public String getIdDevice() {
        return mIdDevice;
    }

    public boolean isDevResponse() {
        return mIsDevResponse;
    }

    public String getComment() {
        return mComment;
    }

    public String getAndroidAppVersionCode() {
        return mAndroidAppVersionCode;
    }

    public String getAndroidAppVersionName() {
        return mAndroidAppVersionName;
    }

    public String getAndroidDeviceVersionSdk() {
        return mAndroidDeviceVersionSdk;
    }

    public int getNbCommentsWithThisIdDevice() {
        return mNbCommentsWithThisIdDevice;
    }
}
