package com.mercandalli.android.apps.files.main.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.mercandalli.android.apps.files.file.cloud.response.FileResponse;
import com.mercandalli.android.library.base.java.StringUtils;

import java.util.List;

public abstract class MyResponse<T> {
    private static final String TAG = "MyResponse";

    @SerializedName("succeed")
    protected boolean mSucceed;

    @SerializedName("result")
    protected List<T> mResult;

    @SerializedName("toast")
    protected String mToast;

    @SerializedName("debug")
    protected String mDebug;

    @SerializedName("apk_update")
    protected List<FileResponse> mApkUpdate;

    protected List<T> getResult(final Context context) {
        if (!StringUtils.isNullOrEmpty(mToast)) {
            Toast.makeText(context, getToast(), Toast.LENGTH_SHORT).show();
        }
        if (!StringUtils.isNullOrEmpty(mDebug)) {
            Log.d(TAG, mDebug);
        }
        if (mApkUpdate != null) {
            PackageManager packageManager = context.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                for (FileResponse fileResponse : mApkUpdate) {
                    if (packageInfo.lastUpdateTime < fileResponse.createModel().getDateCreation().getTime()) {
                        Toast.makeText(context, "You have an update.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(getClass().getName(), "Exception", e);
            }
        }
        return mResult;
    }

    public String getToast() {
        return mToast;
    }

    public boolean isSucceed() {
        return mSucceed;
    }
}
