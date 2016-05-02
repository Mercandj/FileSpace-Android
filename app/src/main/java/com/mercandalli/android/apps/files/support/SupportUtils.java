package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.mercandalli.android.apps.files.main.Config;

import java.util.Locale;

/**
 * Support utils.
 */
public class SupportUtils {

    /* package */
    static SupportDevice getDevice(final Context context) {
        final SupportDevice supportDevice = new SupportDevice();

        // Device
        supportDevice.mAndroidDeviceVersionSdk = String.valueOf(Build.VERSION.SDK_INT);
        supportDevice.mAndroidDeviceModel = Build.MODEL;
        supportDevice.mAndroidDeviceManufacturer = Build.MANUFACTURER;
        supportDevice.mAndroidDeviceDisplayLanguage = Locale.getDefault().getDisplayLanguage();
        supportDevice.mAndroidDeviceCountry = Locale.getDefault().getCountry();

        // App
        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            supportDevice.mAndroidAppVersionCode = String.valueOf(packageInfo.versionCode);
            supportDevice.mAndroidAppVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("DeviceUtils", "NameNotFoundException", e);
        }
        supportDevice.mAndroidAppNotificationId = Config.getNotificationId();

        return supportDevice;
    }
}
