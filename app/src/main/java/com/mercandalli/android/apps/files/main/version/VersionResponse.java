package com.mercandalli.android.apps.files.main.version;

import com.google.gson.annotations.SerializedName;
import com.mercandalli.android.apps.files.BuildConfig;

/**
 * A network response.
 */
/* package */
class VersionResponse {

    @SuppressWarnings("unused")
    @SerializedName("android_last_supported_version_code")
    private int mLastSupportedVersionCode;

    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    @SerializedName("android_version_not_supported")
    private int[] mVersionCodeNotSupported;

    /**
     * Is an update needed.
     *
     * @return True if an update is needed.
     */
    public boolean isUpdateNeeded() {
        final boolean isCurrentVersionVeryOld = mLastSupportedVersionCode > BuildConfig.VERSION_CODE;
        if (isCurrentVersionVeryOld) {
            return true;
        }
        for (int version : mVersionCodeNotSupported) {
            if (version == BuildConfig.VERSION_CODE) {
                return true;
            }
        }
        return false;
    }
}
