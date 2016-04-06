package com.mercandalli.android.apps.files.main;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.mercandalli.android.library.mainlibrary.precondition.Preconditions;

/**
 * The permission manager.
 */
public class Permission {

    public static final int REQUEST_CODE = 26;

    private final String[] mPermissions;
    private OnPermissionResult mOnPermissionResult;

    public Permission(final String[] permissions) {
        mPermissions = permissions;
    }

    /**
     * Ask permission and call {@link OnPermissionResult}.
     *
     * @param onPermissionResult The callback.
     */
    public void askPermissions(final Activity activity, final OnPermissionResult onPermissionResult) {
        Preconditions.checkNotNull(onPermissionResult);
        mOnPermissionResult = onPermissionResult;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, mPermissions, REQUEST_CODE);
        } else {
            onPermissionResult.onPermissionResult(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            for (final int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    mOnPermissionResult.onPermissionResult(false);
                    return;
                }
            }
            mOnPermissionResult.onPermissionResult(true);
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
