package com.mercandalli.android.apps.files.file.cloud.response;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.mercandalli.android.apps.files.common.util.StringUtils;

public abstract class MyResponse<T> {
    @SerializedName("result")
    protected List<T> mResult;

    @SerializedName("toast")
    protected String mToast;

    @SerializedName("apk_update")
    protected List<FileResponse> mApkUpdate;

    protected List<T> getResult(final Context context) {
        if (!StringUtils.isNullOrEmpty(getToast())) {
            Toast.makeText(context, getToast(), Toast.LENGTH_SHORT).show();
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
                e.printStackTrace();
            }
        }
        return mResult;
    }

    public String getToast() {
        return mToast;
    }
}
