package com.mercandalli.android.apps.files.main;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.mercandalli.android.apps.files.common.Preconditions;

/**
 * The permission manager.
 */
public class Permission {

    public static final int REQUEST_CODE = 26;

    private final Activity mActivity;
    private final String[] mPermissions;
    private OnPermissionResult mOnPermissionResult;

    public Permission(Activity activity, String[] permissions) {
        mActivity = activity;
        mPermissions = permissions;
    }

    /**
     * Ask permission and call {@link OnPermissionResult}.
     *
     * @param onPermissionResult The callback.
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
            boolean allSucceed = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allSucceed = false;
                }
            }
            mOnPermissionResult.onPermissionResult(allSucceed);
        }
    }

    public interface OnPermissionResult {
        /**
         * Notify that all permissions succeeded.
         *
         * @param allSucceed True if all the permissions asked suceeded.
         */
        void onPermissionResult(boolean allSucceed);
    }
}
