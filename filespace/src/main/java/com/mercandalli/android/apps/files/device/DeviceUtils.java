package com.mercandalli.android.apps.files.device;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.mercandalli.android.apps.files.common.Preconditions;

import java.util.Locale;

/**
 * Static methods to get the device data (version, os, ram...)
 */
public class DeviceUtils {

    public static Device getDevice(final Context context) {
        Preconditions.checkNotNull(context);
        Device device = new Device();

        //Device
        device.buildModel = Build.MODEL;
        device.buildManufacturer = Build.MANUFACTURER;
        device.buildDisplay = Build.DISPLAY;
        device.buildBootloader = Build.BOOTLOADER;
        device.buildVersionSdk = Build.VERSION.SDK_INT;
        device.buildVersionIncremental = Build.VERSION.INCREMENTAL;
        device.buildRadioVersion = Build.getRadioVersion();
        device.versionOs = System.getProperty("os.version");
        device.displayLanguage = Locale.getDefault().getDisplayLanguage();

        //App
        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            device.versionCode = packageInfo.versionCode;
            device.versionName = packageInfo.versionName;
            device.packageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("DeviceUtils", "NameNotFoundException", e);
        }
        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        device.totalRamMemory = memInfo.totalMem;
        device.availableRamMemory = memInfo.availMem;

        //Memory external
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long blockSize = statFs.getBlockSize();
        device.totalExternalMemory = statFs.getBlockCount() * blockSize;
        device.availableExternalMemory = statFs.getAvailableBlocks() * blockSize;

        //Memory internal
        statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        blockSize = statFs.getBlockSize();
        device.totalInternalMemory = statFs.getBlockCount() * blockSize;
        device.availableInternalMemory = statFs.getAvailableBlocks() * blockSize;

        return device;
    }
}
