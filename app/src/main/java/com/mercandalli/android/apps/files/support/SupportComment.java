package com.mercandalli.android.apps.files.support;

public class SupportComment {

    private final String mId;
    private final String mIdDevice;
    private final boolean mIsDevResponse;
    private final String mComment;

    public String mAndroidAppVersionCode;
    public String mAndroidAppVersionName;
    public String mAndroidAppNotificationId;

    public String mAndroidDeviceVersionSdk;
    public String mAndroidDeviceModel;
    public String mAndroidDeviceManufacturer;
    public String mAndroidDeviceDisplayLanguage;
    public String mAndroidDeviceCountry;

    private final int mNbCommentsWithThisIdDevice;

    public SupportComment(
            final String id,
            final String idDevice,
            final boolean isDevResponse,
            final String comment,

            final String androidAppVersionCode,
            final String androidAppVersionName,
            final String androidAppNotificationId,

            final String androidDeviceVersionSdk,
            final String androidDeviceModel,
            final String androidDeviceManufacturer,
            final String androidDeviceDisplayLanguage,
            final String androidDeviceCountry,

            final int nbCommentsWithThisIdDevice) {

        mId = id;
        mIdDevice = idDevice;
        mIsDevResponse = isDevResponse;
        mComment = comment;

        mAndroidAppVersionCode = androidAppVersionCode;
        mAndroidAppVersionName = androidAppVersionName;
        mAndroidAppNotificationId = androidAppNotificationId;

        mAndroidDeviceVersionSdk = androidDeviceVersionSdk;
        mAndroidDeviceModel = androidDeviceModel;
        mAndroidDeviceManufacturer = androidDeviceManufacturer;
        mAndroidDeviceDisplayLanguage = androidDeviceDisplayLanguage;
        mAndroidDeviceCountry = androidDeviceCountry;

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

    public String getAndroidAppNotificationId() {
        return mAndroidAppNotificationId;
    }

    public String getAndroidDeviceVersionSdk() {
        return mAndroidDeviceVersionSdk;
    }

    public String getAndroidDeviceModel() {
        return mAndroidDeviceModel;
    }

    public String getAndroidDeviceManufacturer() {
        return mAndroidDeviceManufacturer;
    }

    public String getAndroidDeviceDisplayLanguage() {
        return mAndroidDeviceDisplayLanguage;
    }

    public String getAndroidDeviceCountry() {
        return mAndroidDeviceCountry;
    }

    public int getNbCommentsWithThisIdDevice() {
        return mNbCommentsWithThisIdDevice;
    }
}
