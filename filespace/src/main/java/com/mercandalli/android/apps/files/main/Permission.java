package com.mercandalli.android.apps.files.main;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.mercandalli.android.apps.files.common.Preconditions;

public class Permission {

    public static final int REQUEST_CODE = 26;

    final Activity mActivity;
    final String[] mPermissions;

    private OnPermissionResult mOnPermissionResult;

    public Permission(Activity activity, String[] permissions) {
        mActivity = activity;
        mPermissions = permissions;
    }

    /**
     * @return True if ask request.
     */
    public void askPermissions(OnPermissionResult onPermissionResult) {
        Preconditions.checkNotNull(onPermissionResult);
        mOnPermissionResult = onPermissionResult;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(mActivity, mPermissions, REQUEST_CODE);
        } else {
            onPermissionResult.onPermissionResult(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            mOnPermissionResult.onPermissionResult(true);
        }
    }

    public interface OnPermissionResult {
        void onPermissionResult(boolean succeed);
    }
}
