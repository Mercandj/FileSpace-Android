package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mercandalli.android.apps.files.precondition.Preconditions;

/**
 * Support utils.
 */
public class SupportUtils {

    private static String sDeviceId;

    /* package */
    static String getDeviceId(final Context context) {
        Preconditions.checkNotNull(context);
        if (sDeviceId != null) {
            return sDeviceId;
        }
        return sDeviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * Manage the soft input (keyboard).
     */
    public static void hideSoftInput(final EditText editText) {
        Preconditions.checkNotNull(editText);
        final Context context = editText.getContext();
        final InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Manage the soft input (keyboard).
     */
    public static void showSoftInput(final EditText editText) {
        Preconditions.checkNotNull(editText);
        final Context context = editText.getContext();
        final InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /* package */
    static SupportDevice getDevice(final Context context) {
        final SupportDevice supportDevice = new SupportDevice();

        //Device
        supportDevice.mAndroidDeviceVersionSdk = String.valueOf(Build.VERSION.SDK_INT);

        //App
        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            supportDevice.mAndroidAppVersionCode = String.valueOf(packageInfo.versionCode);
            supportDevice.mAndroidAppVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("DeviceUtils", "NameNotFoundException", e);
        }

        return supportDevice;
    }

    /* protected */ static boolean equalsString(@Nullable final String str1, @Nullable final String str2) {
        if (str1 == null) {
            return str2 == null || str2.isEmpty();
        }
        if (str2 == null) {
            return str1.isEmpty();
        }
        return str1.equals(str2);
    }
}
